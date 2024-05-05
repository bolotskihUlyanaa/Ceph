package ulyana.OSD;

import ulyana.Client.*;
import java.net.InetAddress;

//object storage device(demon)
//хранит блоки
public class OSD{
    final private InetAddress IP;
    final private int port;
    final private Storage storage;//тип хранения в оперативной или постоянной памяти

    public OSD(InetAddress IP, int port, Storage storage){
        this.IP = IP;
        this.port = port;
        this.storage = storage;
    }

    //положить блок в osd
    public boolean put(Block block){return storage.save(block);}

    //взять блок из osd
    public Block get(String blockID){return storage.get(blockID);}

    //удалить блок в osd
    public boolean remove(String blockID){
        return storage.remove(blockID);
    }

    public String toString(){return "{IP: ".concat(IP.getHostAddress()).concat(" port:") + port + "}\n";}

    public InetAddress getIP(){
        return IP;
    }

    public int getPort(){
        return port;
    }
}
