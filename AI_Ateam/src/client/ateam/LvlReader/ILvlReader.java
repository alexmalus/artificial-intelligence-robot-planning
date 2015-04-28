package client.ateam.LvlReader;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Lasse on 24-04-2015.
 */
public interface ILvlReader {

    Map<Character, String> readColors() throws Exception;

    String readLevel() throws IOException;

    void sendCommand() throws IOException;
}
