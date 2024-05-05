package ulyana.MDS;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//для работы с сервером метаданных с помощью сокетов
public class MDSNet {
    final private MDSDisk mds;
    private ServerSocket serverSocket;

    public MDSNet(int port){
        mds = new MDSDisk("res/MDS/" + port + ".txt");
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();//принимаем подключение клиента
                //работу с клиентом переводим в отдельный поток
                ReceiveMDSThread cliThread = new ReceiveMDSThread(clientSocket, mds);
                cliThread.start();
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}
