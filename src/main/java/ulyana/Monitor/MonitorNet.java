package ulyana.Monitor;

import java.net.ServerSocket;
import java.net.Socket;

//для работы с монитором с помощью сокетов
public class MonitorNet {
    final private Monitor monitor;
    final private ServerSocket serverSocket;

    public MonitorNet() throws Exception {
        monitor = new Monitor();
        serverSocket = new ServerSocket(monitor.getPort());
    }

    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                ReceiveMonitorThread cliThread = new ReceiveMonitorThread(clientSocket, monitor);
                cliThread.start();
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}
