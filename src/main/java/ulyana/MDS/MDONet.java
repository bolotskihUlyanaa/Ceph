package ulyana.MDS;

import ulyana.Client.SendThread;
import java.net.InetAddress;
import java.util.ArrayList;

//операции взаимодействия с mds с помощью сокетов
public class MDONet implements MetaDataOperation {
    final private InetAddress ip;
    final private int port;

    //ip и порт mds сервера
    public MDONet(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Object addInodeFile(String nameInode, int size, int countBlock) throws Exception {
        return SendThread.send(ip, port,"addInodeFile " + nameInode + " " + size + " " + countBlock);
    }

    public Object addInodeDirectory(String nameInode) throws Exception {
        return SendThread.send(ip, port, "addInodeDirectory ".concat(nameInode));
    }

    public InodeFile removeFile(String nameInode) throws Exception {
        return (InodeFile) SendThread.send(ip, port, "removeFile ".concat(nameInode));
    }

    public ArrayList<InodeFile> removeDirectory(String nameInode) throws Exception {
        return (ArrayList<InodeFile>) SendThread.send(ip, port, "removeDirectory ".concat(nameInode));
    }

    //найти по имени файла файл в файловой системе
    public InodeFile find(String nameInode) throws Exception {
        return (InodeFile) SendThread.send(ip, port, "find ".concat(nameInode));
    }

    public String ls(String nameInode) throws Exception {
        if(nameInode.equals("")) nameInode = "/ceph";
        return (String) SendThread.send(ip, port, "ls ".concat(nameInode));
    }

    public boolean cd(String nameInode) throws Exception {
        if(nameInode.equals(""))
            nameInode = "/ceph/";
        Object result = SendThread.send(ip, port, "cd ".concat(nameInode));
        if(result == null)
            return false;
        return (boolean) result;
    }

    public String pwd() throws Exception {
        return (String) SendThread.send(ip, port, "pwd");
    }
}
