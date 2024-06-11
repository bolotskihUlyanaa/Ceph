package ulyana.Monitor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

//поток для получения запросов на информацию из конфигурационного файла
public class ReceiveMonitorThread extends Thread {
    final private Socket socket;
    final private Monitor monitor;

    public ReceiveMonitorThread(Socket clientSocket, Monitor monitor) {
        this.socket = clientSocket;
        this.monitor = monitor;
    }

    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object outObject = null;
            String command = (String) in.readObject();
            if(command.equals("get map OSDs"))
                outObject = monitor.getClusterMap();
            if(command.equals("get count of replica"))
                outObject = monitor.getCountReplica();
            if(command.equals("get count of pg"))
                outObject = monitor.getPGNum();
            if(command.startsWith("osdChangeCon")) {
                String[] args = command.split(" ");
                if (args.length != 4) outObject = 0;
                else {
                    outObject = monitor.setConditionToHost(InetAddress.getByName(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                }
            }
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(outObject);
            in.close();
            out.close();
            socket.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
