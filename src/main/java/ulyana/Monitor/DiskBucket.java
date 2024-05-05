package ulyana.Monitor;

import java.net.InetAddress;

//класс который представляем самый нижележащий уровень в карте кластера OSD - сами OSD
public class DiskBucket extends Bucket{
    final private InetAddress ip;
    final private int port;
    private boolean isFailture;
    private boolean isOverload;

    public DiskBucket(InetAddress ip, int port){
        super("OSD", ip.toString().concat(" ") + port);//имя состоит из ip и порта
        this.ip = ip;
        this.port = port;
        isFailture = false;
        isOverload = false;
    }

    public InetAddress getIP(){
        return ip;
    }

    public int getPort(){
        return port;
    }

}
