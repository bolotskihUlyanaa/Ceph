package ulyana.MDS;

import ulyana.OSD.DONet;
import ulyana.Monitor.MONet;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

//для работы с сервером метаданных с помощью сокетов
public class MDSNet {
    final private MDSDisk mds;
    final private ServerSocket serverSocket;

    public MDSNet(int port) throws Exception {
        mds = new MDSDisk(new DONet(), new MONet(InetAddress.getByName("localhost"), 8888));
        serverSocket = new ServerSocket(port);
    }

    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                ReceiveMDSThread clientThread = new ReceiveMDSThread(clientSocket, mds);//работу с клиентом переводим в отдельный поток
                clientThread.start();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
