package ulyana.Client;

import ulyana.Monitor.DiskBucket;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

//класс для обращения к osd при помощи сокетов
public class DONet implements DataOperation{

    //сохранить блок
    //true - успех false - сохранить не получилось
    public boolean put(DiskBucket disk, Block block) throws Exception{
        return (boolean) send(disk.getIP(), disk.getPort(), block);//отправляем на osd блок на ip и порт
    }

    //отправляем на osd номер блока чтобы его взять
    public Block get(DiskBucket disk, String blockID) throws Exception{
        return (Block) send(disk.getIP(), disk.getPort(), blockID);//если null - взять блок не получилось
    }

    //удалить блок
    //true - успех false - удалить не получилось
    public boolean remove(DiskBucket disk, String blockID) throws Exception{
        return (boolean) send(disk.getIP(), disk.getPort(), "rm ".concat(blockID));
    }

    //думаю лучше открывать всегда новое соединение, в случае неполадок есть вероятность что соединение в следующий раз установится
    //бросаем исключение если ошибка в соединении, обработается на клиенте
    private Object send(InetAddress ip, int port, Object object) throws Exception{
            Socket socket = new Socket(ip, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(object);
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object result = in.readObject();
            in.close();
            out.close();
            socket.close();
            return result;
    }
}
