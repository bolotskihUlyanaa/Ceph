package ulyana.Monitor;

import ulyana.Client.SendThread;

import java.net.InetAddress;
import java.util.ArrayList;

//операции если общение с монитором происходит по сети
public class MONet implements MonitorOperation {
    final private InetAddress ip;
    final private int port;

    public MONet(InetAddress ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public int getCountOfReplica() throws Exception {
        return (int) SendThread.send(ip, port, "get count of replica");
    }

    public ArrayList<Bucket> getMap() throws Exception {
        return (ArrayList<Bucket>) SendThread.send(ip, port, "get map OSDs");
    }

    public int getPGNum() throws Exception {
        return (int) SendThread.send(ip, port, "get count of pg");
    }
}
