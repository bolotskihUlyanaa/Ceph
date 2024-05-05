package ulyana.OSD;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//для работы с osd с помощью сокетов
public class OSDNetServer {
    private OSD osd;
    private ServerSocket serverSocket;
    final private String path = "res/OSDs/";

    public OSDNetServer(int port) {
        try {
            serverSocket = new ServerSocket(port);//слушаем порт
            //создаем хранилище в постоянной памяти
            osd = new OSD(serverSocket.getInetAddress(), port, new DiskStorage(path + port + ".txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();//принимаем подключение
                //переводим работу с клиентом в отдельный поток
                //работа с клиентом подразумевает под собой получание запроса, его выполнение и возврат результата
                ReceiveOSDThread cliThread = new ReceiveOSDThread(clientSocket, osd);
                cliThread.start();
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

}
