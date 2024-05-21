package ulyana.Monitor;

import java.util.ArrayList;

//операции при подключении к монитору напрямую
public class MO implements MonitorOperation{
    private final Monitor monitor;

    public MO() throws Exception {
        monitor = new Monitor();
    }

    public int getCountOfReplica() {
        return monitor.getCountReplica();
    }

    public ArrayList<Bucket> getMap() {
        return monitor.getClusterMap();
    }

    public int getPGNum() {
        return monitor.getPGNum();
    }
}
