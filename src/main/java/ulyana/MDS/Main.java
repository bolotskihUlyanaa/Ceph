package ulyana.MDS;

import ulyana.Client.Block;

import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {
        MDSNet mds = new MDSNet(9999);
        mds.run();

        /*

        MDS mds = new MDS();
        mds.addInodeDirectory("res");
        mds.addInodeDirectory("res/src");
        mds.addInodeDirectory("qwe");
        mds.cd("res/src");
        //System.out.println(mds.getID());


        String path = "res/MDS/test.txt";
        FileOutputStream a = new FileOutputStream(path);
        ObjectOutputStream outputStream = new ObjectOutputStream(a);
        outputStream.writeObject(mds);
        outputStream.close();
        a.close();


        FileInputStream inputStream = new FileInputStream(path);
        //файл может быть пустой потому что в нем ещё не хранят блоки или стерли данные,
        //чтобы не выдавало ошибку нужна эта проверка
        MDS i = null;
        if (inputStream.available() > 0) {
            ObjectInputStream in = new ObjectInputStream(inputStream);
            i = (MDS) in.readObject();
            in.close();
        }
        inputStream.close();

        System.out.println(i.pwd());
        //System.out.println(mds.getID());
        //System.out.println(i.ls("/ceph/"));

         */

    }
}