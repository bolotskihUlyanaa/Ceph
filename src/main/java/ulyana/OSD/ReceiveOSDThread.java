package ulyana.OSD;

import ulyana.Client.Block;
import java.io.*;
import java.net.Socket;

//поток для работы с отдельным клиентом
//работа с клиентом подразумевает под собой получание запроса, его выполнение и возврат результата
public class ReceiveOSDThread extends Thread {
    final private Socket socket;
    final private OSD osd;

    public ReceiveOSDThread(Socket clientSocket, OSD osd){
        this.socket = clientSocket;
        this.osd = osd;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object readObject = in.readObject();
            Object outObject = null;//объект который вернем
            if (readObject instanceof String) {
                //приходит строка когда запрашивается блок или когда нужно удалить блок
                //когда требуется удалить блок приходит "rm numberBlock"
                //когда нужно найти блок приходит просто номер блока
                if(((String) readObject).startsWith("rm")){
                    String[] objects = ((String) readObject).split(" ");
                    outObject = osd.remove(objects[1]);
                }
                else {
                    outObject = osd.get((String) readObject);//находим блок
                }
            }
            if (readObject instanceof Block) {//когда приходит блок значит надо его сохранить
                outObject = osd.put((Block) readObject);
            }
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(outObject);
            in.close();
            out.close();
            socket.close();//закрывает in/out putStream
        }
        catch (Exception e) {//проблемы с соединением
            System.out.println(e.getMessage());
        }
    }
}
