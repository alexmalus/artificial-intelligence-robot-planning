package client.ateam.LvlReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileLvlReader implements ILvlReader {

    private BufferedReader serverMessages;

    public FileLvlReader(BufferedReader serverMsg){
        this.serverMessages = serverMsg;
    }
    //this is redundant
    @Override
    public Map<Character, String> readColors() throws Exception {
        /*
        fetches the color definitions and returns a hash map, can be linked to agents later (or refactored to perform agent creation here?)
         */
        Map< Character, String > colors = new HashMap< Character, String >();
        String line, color;
        int colorLines = 0;

        while ( ( line = serverMessages.readLine() ).matches( "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$" ) ) {
            line = line.replaceAll( "\\s", "" );
            String[] colonSplit = line.split( ":" );
            color = colonSplit[0].trim();
            for ( String id : colonSplit[1].split( "," ) ) {
                colors.put( id.trim().charAt( 0 ), color );
            }
            colorLines++;

        }
        if ( colorLines > 0 ) {
            throw new Exception( "Box colors not supported" );
        }

        return colors;
    }
    /*
    fetches the level and returns an arraylist containing each line
     */

    //TODO: this function should return a string and not an arraylist of strings
//    @Override
//    public ArrayList<String> readLevel() throws IOException {
//        ArrayList<String> allLines = new ArrayList<String>();
//        String line;
//        while ( ( line = serverMessages.readLine() ).matches( "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$" ) ) {
//            line = line.replaceAll("\\s", "");
//            if(!line.equals("")) {
//                allLines.add(line);
//            }
//        }
//
//        return allLines;
//    }
    @Override
    public String readLevel() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sendCommand() {

    }

}