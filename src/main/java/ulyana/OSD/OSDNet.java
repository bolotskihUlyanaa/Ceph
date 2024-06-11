package ulyana.OSD;

import ulyana.Component;
import ulyana.Monitor.MonitorOperation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//для работы с osd по сокетам
public class OSDNet implements Component {
    private OSD osd;
    private ServerSocket serverSocket;
    final private String path = "res/OSDs/";

    public OSDNet(int port) {
        try {
            serverSocket = new ServerSocket(port);
            osd = new OSD(serverSocket.getInetAddress(), port, new DiskStorage(path + port + ".txt"));
            System.out.println("Ready to work");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OSDNet(int port, MonitorOperation monitor, DONet otherOsd) {
        try {
            serverSocket = new ServerSocket(port);
            osd = new OSD(serverSocket.getInetAddress(), port, new DiskStorage(serverSocket.getInetAddress(), port, path + port + ".txt", monitor, otherOsd));
            System.out.println("Ready to work");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                ReceiveOSDThread cliThread = new ReceiveOSDThread(clientSocket, osd);
                cliThread.start();
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
