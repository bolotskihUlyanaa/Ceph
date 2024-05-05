package ulyana.MDS;

import java.io.*;
import java.net.Socket;

//поток для работы с конкретным клиентом
//получение запросов по сокетам и отправка результата выполнения фунций в mds
public class ReceiveMDSThread extends Thread {
    final private Socket socket;
    final private MDSDisk mds;

    public ReceiveMDSThread(Socket clientSocket, MDSDisk mds){
        this.socket = clientSocket;
        this.mds = mds;
    }

    public void run(){
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object outObject = null;//объект который вернем
            String obj = (String) in.readObject();
            String[] input = obj.split(" ");
            //в зависимости от команды которая пришла будем обращаться к msd с разным запросом
            switch (input[0]) {
                case ("addInodeFile"):
                    outObject = mds.addInodeFile(input[1], Integer.parseInt(input[2]), Integer.parseInt(input[3]));
                    break;
                case ("addInodeDirectory"):
                    outObject = mds.addInodeDirectory(input[1]);
                    break;
                case ("removeFile"):
                    outObject = mds.removeFile(input[1]);
                    break;
                case ("removeDirectory"):
                    outObject = mds.removeDirectory(input[1]);
                    break;
                case ("find"):
                    outObject = mds.find(input[1]);
                    break;
                case ("ls"):
                    outObject = mds.ls(input[1]);
                    break;
                case ("cd"):
                    outObject = mds.cd(input[1]);
                    break;
                case ("pwd"):
                    outObject = mds.pwd();
                    break;
            }
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            if (!socket.isClosed()) out.writeObject(outObject);
            in.close();
            out.close();
            socket.close();//закрывает in/out putStream
        }
        catch (Exception e) {
            System.out.println(e.getMessage());//может возникнуть когда команду не отправили и определили новый out in socket
        }
    }

}
