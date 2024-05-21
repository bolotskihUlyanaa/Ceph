package ulyana.Monitor;

import java.io.*;
import java.net.InetAddress;
import java.util.*;

//монитор хранит пока что только карту osd
public class Monitor {
    private int OSDPoolDefaultSize;//коэффициент репликации
    private ArrayList<Host> addressOSD;
    final private Bucket clusterMap;//карта OSD
    final private String nameFileConf = "cephConf.txt";
    private int port;//порт на котором слушает монитор
    private int pgNum;//количество плейсмент групп

    class Host {
        InetAddress ip;
        int port;

        public Host(String ip, int port) throws Exception {
                this.ip = InetAddress.getByName(ip);
                this.port = port;
        }

        public String toString() {
            return ip.getHostAddress().concat(" ").concat(Integer.toString(port));
        }

    }

    public Monitor() throws Exception {
        clusterMap = new Bucket("root", "root");
        readConf(nameFileConf);
        for (Host host: addressOSD) {
            clusterMap.add(new DiskBucket(host.ip, host.port));
        }
        //clusterMap.find(nameBucket).add(d); чтобы добавить потомка для определенного узла
    }

    public int getCountReplica() {
        return OSDPoolDefaultSize;
    }

    public ArrayList<Bucket> getClusterMap() {
        return clusterMap.getMap();
    }

    //чтобы сформировать строку из всех адресов
    public String addressToString() {
        StringBuilder str = new StringBuilder();
        for (Host host: addressOSD) {
            str.append(host);
            str.append("#");
        }
        return str.toString();
    }

    //прочитать конфигурационный файл
    public void readConf(String nameFile) throws Exception {
        File file = new File(nameFile);
        if (!file.isFile())
            throw new Exception("Such conf file doesn't exist: " + nameFile);
        Properties properties = new Properties();
        properties.load(new FileReader(file));
        OSDPoolDefaultSize = Integer.parseInt(properties.getProperty("OSDPoolDefaultSize"));
        port = Integer.parseInt(properties.getProperty("MonitorPort"));
        pgNum = Integer.parseInt(properties.getProperty("pgNum"));
        String[] address = properties.getProperty("OSDs").split("#");
        addressOSD = new ArrayList<Host>();
        for (String host : address) {
            String[] parts = host.split(" ");
            if (parts.length == 2) {
                addressOSD.add(new Host(parts[0], Integer.parseInt(parts[1])));
            }
            else{
                throw new Exception("Error in conf.txt \nMust be pair: ip port");
            }
        }
    }

    //запись в конфигурационный файл
    public void writeConf(String nameFile) throws Exception {
        File file = new File(nameFile);
        if (!file.isFile())
            throw new Exception("Such conf file doesn't exist: " + nameFile);
        Properties properties = new Properties();
        properties.setProperty("MonitorPort", Integer.toString(port));
        properties.setProperty("OSDPoolDefaultSize", Integer.toString(OSDPoolDefaultSize));
        properties.setProperty("OSDs", addressToString());
        properties.setProperty("pgNum", Integer.toString(pgNum));
        properties.store(new FileWriter(file), "");
    }

    public int getPort(){
        return port;
    }

    public int getPGNum(){
        return pgNum;
    }
}
