package ulyana.Monitor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

//монитор хранит все карты: расположение серверов OSD и расположение серверов MDS (пока что только карту osd)
public class Monitor {
    final private Bucket clusterMap;//карта OSD

    public Monitor(){
        clusterMap = new Bucket("root", "root");//создаем корень
        try {
            //пока создадим 1 хоста
            for (int i = 0; i < 2; i++) {
                clusterMap.add(new DiskBucket(InetAddress.getByName("localhost"), 11000 + i));
            }
            //clusterMap.find(nameBucket).add(d); чтобы добавить потомка для определенного узла
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Bucket> getClusterMap(){
        return clusterMap.getMap();
    }
}
