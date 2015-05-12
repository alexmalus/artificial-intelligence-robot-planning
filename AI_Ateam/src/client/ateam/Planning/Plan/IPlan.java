package client.ateam.Planning.Plan;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lasse on 25-04-2015.
 */
public class IPlan extends ArrayList<Action> implements Comparable {
    /*
    find solution
    create list of actions in linked list
    replanning mechanism?
     */
    /**
     * Eclipse needed this
     */
    private static final long serialVersionUID = 1L;

    /**
     * The agent who created this plan
     */
    protected Agent creator;

    /**
     * This is a point that points to the next uncommit action in the plan
     */
    //public int progressPoint = 0;

    /**
     * Create a new Plan with an agent as owner
     * @param own
     */
    public Plan(Agent own) {
        super();
        this.creator = own;
    }

    public Agent getCreator() {
        return this.creator;
    }

    /**
     * Insert a number of NoOp at a given postion in the plan
     * @param count The number of NoOp
     * @param index The index
     */
    public void insertNoOpsAt(int count, int index) {
        for(int i=0; i<count; i++) this.add(index, new Action(ActionType.NOOP));
    }

    /**
     * This static method will take a list of plans and check them all for collision.
     * Any plans conflicting will be marked in the individual plan objects, with references to conflicting plans.
     * @param plans A list of all plans.
     * @param depthLimit, the depth of the check, -1 = noDepthLimit
     * @return If all plans are not conflicting the method return true, else false
     */
    public static ArrayList<PlanConflict> consolidatePlans(ArrayList<Plan> plans,
                                                           int depthLimit, ArrayList<Integer> agentPositions) {
		/*ArrayList<Integer> agentPositions = new ArrayList<Integer>();

		for(Plan p : plans){
			agentPositions.add(p.owner.getIndexOnMap());
		}*/


        ArrayList<Plan> plansClone = new ArrayList<Plan>();
        for(Plan p: plans) {
            plansClone.add((Plan) p.clone());
        }


        return Plan.consolidatePlans2(plansClone, depthLimit,
                Level.realMap.clone(), agentPositions, 0);

        //return true;
    }

    private static ArrayList<PlanConflict> consolidatePlans2(ArrayList<Plan> plans, int depthLimit,
                                                             int[] map, ArrayList<Integer> agentPositions, int depth) {
        if (depthLimit == 0)
            return null;

        ArrayList<Action> curActions = new ArrayList<Action>();
        ArrayList<Integer> curAgents = new ArrayList<Integer>();
        //ArrayList<Integer> curAgentPos = new ArrayList<Integer>();
        ArrayList<PlanConflict> conflicts = null;

        // Find the first step of all plans, and remove it
        for (int i = 0; i < plans.size(); i++) {
            Plan p = plans.get(i);
            if(!p.isEmpty()){
                Action a = p.get(0);
                if (a != null) {
                    p.remove(0);
                    curActions.add(a);
                    curAgents.add(i);
                    //curAgentPos.add(agentPositions.get(i));
                }
            }
        }

        // No more actions
        if (curActions.size() == 0)
            return null;

        // Check that actions are applicable
        for (int i = 0; i < curActions.size(); i++) {
            int curPos = agentPositions.get(curAgents.get(i));
            Action act = curActions.get(i);
            if (!Level.isActionApplicable(map, act, curPos, true)) {
                if (conflicts == null) conflicts = new ArrayList<PlanConflict>(4);
                conflicts.add(new PlanConflict(depth, plans.get(curAgents.get(i))));
            }
        }

        // Check to see if any pairs of actions conflict
        for (int i = 0; i < curActions.size(); i++) {
            for (int j = i + 1; j < curActions.size(); j++) {
                int curPosI = agentPositions.get(curAgents.get(i));
                int curPosJ = agentPositions.get(curAgents.get(j));
                if (Action.conflict(curPosI, curActions.get(i),
                        curPosJ, curActions.get(j))){
                    if (conflicts == null) conflicts = new ArrayList<PlanConflict>(4);
                    conflicts.add(new PlanConflict(depth, plans.get(curAgents.get(i)), plans.get(curAgents.get(j))));
                }
            }
        }

        if (!(conflicts == null || conflicts.isEmpty()))
            return conflicts;

        // Check that actions are applicable
//		for (int i = 0; i < curActions.size(); i++) {
//			int curPos = agentPositions.get(curAgents.get(i));
//			Action act = curActions.get(i);
//			if (!Level.isActionApplicable(map, act, curPos, true)) {
//                if (conflicts == null) conflicts = new ArrayList<PlanConflict>(4);
//				conflicts.add(new PlanConflict(depth, plans.get(curAgents.get(i)).owner));
//            }
//		}

        // Execute the actions and update the map and agent positions
        for(int i = 0; i < curActions.size();i++){
            Level.applyAction(map, curActions.get(i),agentPositions.get(curAgents.get(i)), true);
            int newPos = curActions.get(i).newAgentPosition(agentPositions.get(curAgents.get(i)));
            agentPositions.set(curAgents.get(i), newPos);
        }

        // Recurse on the rest of the plans
        if (conflicts == null || conflicts.isEmpty())
            return Plan.consolidatePlans2(plans, --depthLimit, map, agentPositions, ++depth);
        else
            return conflicts;
    }

    /**
     * Prints the plan to std err
     */
    public void print() {
        for (int a=0; a<this.size(); a++) {
            System.err.println(this.get(a).toString());
        }
    }

    /**
     * Comparable interface method, for sorting plans.
     * Sort plans smallest first
     * @param b
     * @return
     */
    @Override
    public int compareTo(Object b) throws ClassCastException {
        Plan b2 = (Plan) b;
        if (this.size() > b2.size())
            return -1;
        else if (this.size() < b2.size())
            return 1;
        else
            return 0;
    }
}
