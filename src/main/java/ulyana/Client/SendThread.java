package ulyana.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

//для отправки запросов
public class SendThread {
    static public Object send(InetAddress ip, int port, Object command) throws IOException, ClassNotFoundException {
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
