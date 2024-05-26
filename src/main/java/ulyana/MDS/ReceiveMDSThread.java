package ulyana.MDS;

import java.io.*;
import java.net.Socket;

//поток для работы с конкретным клиентом
//получение запросов по сокетам и отправка результата выполнения фунций в mds
public class ReceiveMDSThread extends Thread {
    final private Socket socket;
    final private MDSDisk mds;

    public ReceiveMDSThread(Socket clientSocket, MDSDisk mds) {
        this.socket = clientSocket;
        this.mds = mds;
    }

    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object outObject = null;
            String inObject = (String) in.readObject();
            String[] input = inObject.split(" ");
            switch (input[0]) {
                case ("addInodeFile"):
                    outObject = mds.addInodeFile(input[1], Integer.parseInt(input[2]), Integer.parseInt(input[3]));
                    break;
                case ("update"):
                    outObject = mds.update(input[1], input[2], Integer.parseInt(input[3]), Integer.parseInt(input[4]));
                    break;
                case ("block"):
                    outObject = mds.blockFile(input[1], input[2]);
                    break;
                case ("unblock"):
                    outObject = mds.unblockFile(input[1], input[2]);
                    break;
                case ("addInodeDirectory"):
                    outObject = mds.addInodeDirectory(input[1]);
                    break;
                case ("removeFile"):
                    outObject = mds.removeFile(input[1], input[2]);
                    break;
                case ("removeDirectory"):
                    outObject = mds.removeDirectory(input[1], input[2]);
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
