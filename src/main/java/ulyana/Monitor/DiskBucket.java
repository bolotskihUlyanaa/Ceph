package ulyana.Monitor;

import java.io.Serializable;
import java.net.InetAddress;

//класс который представляем самый нижележащий уровень в карте кластера OSD - сами OSD
public class DiskBucket extends Bucket implements Serializable {
    final private InetAddress ip;
    final private int port;

    public DiskBucket(InetAddress ip, int port, int conditional) {
        super("OSD", ip.toString().concat(" ") + port, conditional);//имя состоит из ip и порта
        this.ip = ip;
        this.port = port;
    }

    public InetAddress getIP() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
