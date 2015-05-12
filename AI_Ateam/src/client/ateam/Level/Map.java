package client.ateam.Level;

/**
 * Created by joh on 03/05/15.
 */
import java.util.TreeMap;
import java.util.TreeSet;

public class Map implements Comparable<Map> {

    public int[] map;
    private Map parent;
    private TreeMap<Integer, Integer> changes = new TreeMap<Integer,Integer>();
    public int length;
    private boolean changed = true;
    private TreeSet<Integer> cachedChanges;
    private int hashvalue = 0;

    public Map(int[] m, Map p) {
        map = m;
        parent = p;
        length = m.length;
        if (p != null) {
            changes = new TreeMap<Integer,Integer>(p.changes());
            this.hashvalue = p.getHashValue();
        }
    }

    public int get(int i) {
        if (changes.containsKey(i))
            return changes.get(i);
        return map[i];
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public void set(int i, int v) {
        this.changed = true;
        if (parent == null)
            map[i] = v;
        else if (v == map[i]) {
            this.hashvalue -= this.getKeyValueHash(i, changes.get(i));
            changes.remove(i);
        }
        else {
            if (changes.containsKey(i)) {
                this.hashvalue -= this.getKeyValueHash(i, changes.get(i));
                changes.remove(i);
            }
            changes.put(i, v);
            this.hashvalue += this.getKeyValueHash(i, v);
        }
    }

    public TreeSet<Integer> getChanges() {
        if (this.changed) {
            TreeSet<Integer> changed = new TreeSet<Integer>();
            changed.addAll(changes.keySet());
            cachedChanges = changed;
            this.changed = false;
            return changed;
        }
        return cachedChanges;
    }

    public TreeMap<Integer,Integer> changes() {
        return this.changes;
    }

    public int compareTo(Map other) {
        TreeSet<Integer> ch = new TreeSet<Integer>();
        ch.addAll(this.changes.keySet());
        ch.addAll(other.changes().keySet());

        for (Integer i : ch) {
            int a = this.get(i);
            int b = other.get(i);
            if (a < b)
                return -1;
            else if (a > b)
                return 1;
        }

        return 0;
    }

    public int getHashValue() {
        return this.hashvalue;
    }

    private int getKeyValueHash(int i, int v) {
        return i * v + i * i * i + v * v * v * v * v;
    }

    public String toString(){
        String mapS ="";
        int row = 0;
        for(int i = 0; i < length;i++){
            int curRow = ArrayLevel.getRowFromIndex(i);
            if(curRow != row) {
                mapS += "\n";
                row = curRow;
            }

            int b = (this.get(i));

            if(ArrayLevel.isWall(b))
                mapS += "+";
            else if(ArrayLevel.isBox(b))
                mapS += ArrayLevel.getBoxLetter(this.get(i));
            else if(ArrayLevel.isAgent(b))
                mapS += ArrayLevel.getAgentId(b);
            else if(ArrayLevel.isGoal(b))
                mapS += (""+ArrayLevel.getGoalLetter(b)).toLowerCase();
            else mapS += " ";
        }

        return mapS;
    }

}

