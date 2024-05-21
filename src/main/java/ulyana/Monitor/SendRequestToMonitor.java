package ulyana.Monitor;

import java.io.*;
import java.net.*;

//поток для отправки запросов к монитору
public class SendRequestToMonitor {
    final private InetAddress ip;
    final private int port;

    public SendRequestToMonitor(String ip, int port) throws Exception {
        this.ip = InetAddress.getByName(ip);
        this.port = port;
    }

    //бросаем исключение если ошибка в соединении, обрабатывается на клиенте
    public Object send(String command) throws Exception {
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
