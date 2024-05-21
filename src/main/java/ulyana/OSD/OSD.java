package ulyana.OSD;

import java.net.InetAddress;

public class OSD{
    final private InetAddress IP;
    final private int port;
    final private Storage storage;//тип хранения

    public OSD(InetAddress IP, int port, Storage storage){
        this.IP = IP;
        this.port = port;
        this.storage = storage;
    }

    public boolean put(Block block) {
        return storage.save(block);
    }

    public Block get(String blockID) {
        return storage.get(blockID);
    }

    public boolean remove(String blockID) {
        return storage.remove(blockID);
    }

    public String toString() {
        return "{IP: ".concat(IP.getHostAddress()).concat(" port:") + port + "}\n";
    }

    public InetAddress getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }
}
