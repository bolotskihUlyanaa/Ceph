package ulyana.Monitor;

import java.util.ArrayList;

//для операций с монитором
public interface MonitorOperation {

    int getCountOfReplica() throws Exception;

    ArrayList<Bucket> getMap() throws Exception;

    int getPGNum() throws Exception;
}
