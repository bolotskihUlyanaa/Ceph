package ulyana.Client;

import ulyana.MDS.InodeFile;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

//операции взаимодействия с mds с помощью сокетов
public class MDONet implements MetaDataOperation{
    final private InetAddress ip;
    final private int port;

    //ip и порт mds сервера
    public MDONet(InetAddress ip, int port){
        this.ip = ip;
        this.port = port;
    }

    //добавить файл в файловую систему
    //возвращает номер Inode при успешном добавлении файла в файловую систему
    //если произошла ошибка,то возвращается строка с произошедшей ошибкой
    public Object addInodeFile(String nameInode, int size, int countBlock)throws Exception{
        return send("addInodeFile " + nameInode + " " + size + " " + countBlock);
    }

    //добавить директорию в файловую систему
    //возвращает строку "success" при успешном добавлении директории в файловую систему
    //если произошла ошибка,то возвращается строка с произошедшей ошибкой
    public Object addInodeDirectory(String nameInode)throws Exception{
        return send("addInodeDirectory ".concat(nameInode));
    }

    //удалить файл, возвращает номер inode удаленного файла
    public InodeFile removeFile(String nameInode)throws Exception {
        return (InodeFile) send("removeFile ".concat(nameInode));
    }

    //удалить директорию, возвращает номера inode удаленных файлов
    public ArrayList<InodeFile> removeDirectory(String nameInode)throws Exception{
        return (ArrayList<InodeFile>) send("removeDirectory ".concat(nameInode));
    }

    //найти по имени файла файл в файловой системе (словарный запас отдыхает)
    public InodeFile find(String nameInode)throws Exception{
        return (InodeFile)send("find ".concat(nameInode));
    }

    //вернет либо результат команды ls, либо строку с ошибкой
    public String ls(String nameInode)throws Exception{
        if(nameInode.equals("")) nameInode = "/ceph";
        return (String) send("ls ".concat(nameInode));
    }

    //true - если удалось перейти в директорию
    //false - если произошла ошибка
    public boolean cd(String nameInode)throws Exception{
        if(nameInode.equals("")) nameInode = "/ceph/";
        Object result = send("cd ".concat(nameInode));
        if(result == null) return false;
        return (boolean) result;
    }

    public String pwd() throws Exception{
        return (String) send("pwd");
    }

    //думаю лучше открывать всегда новое соединение, в случае неполадок есть вероятность что соединение в следующий раз установится
    //бросаем исключение если ошибка в соединении, обработается на клиенте
    private Object send(String command) throws Exception{
            Socket socket = new Socket(ip, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(command);
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object result = in.readObject();
            in.close();
            out.close();
            socket.close();
            return result;
    }
}
