package ulyana.OSD;

import ulyana.Client.*;

public class OSD implements Bucket{
    final private String IP;
    final private int port;
    private Storage storage;
    private boolean overload;//флаг не перегружено ли устройство
    private boolean failed;//флаг работает ли устройство
//может быть нужен текущий размер OSD

    public OSD(String IP, int port, Storage storage){
        this.IP = IP;
        this.port = port;
        this.storage = storage;
        overload = false;
        failed = false;
    }

    public void put(Block block){storage.save(block);}

    public Block get(String blockID){return storage.get(blockID);}

    public void remove(String blockID){
        storage.remove(blockID);
    }

    public boolean isFailed() {return failed;}

    public boolean isOverload() {return overload;}

    public String toString(){return "{IP: ".concat(IP).concat(" port:") + port + "}\n";}
}
