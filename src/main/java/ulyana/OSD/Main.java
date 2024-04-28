package ulyana.OSD;

import ulyana.Client.Client;

public class Main {
    public static void main(String []args)  {
        try {

            /*
            ulyana.OSD.DiskStorage disk = new DiskStorage("res/OSD.txt");
            byte[] mas = new byte[20];
            for(int i = 0; i < 20; i++){
                mas[i] = (byte) i;
            }
            Block a = new Block("6666666", "/123456", 0, mas);
            Block b = new Block("777", "/1234567", 22, mas);
            //disk.save(a);
            //disk.save(b);
            System.out.println(disk.load());
            System.out.println(disk.get("6666666"));

             */

/*
        //CRUSH
        //создаем блок
        Block a = new Block("111111", "/Users/ulanabolotskih/Downloads/BigInt.hpp", 0, new byte[20]);
        //Создаем карту, пока что состоит только из компьютеров
        ArrayList<OSD> map = new ArrayList<OSD>();
        for(int i = 0; i < 100; i++){
            map.add(new OSD("192.168.0." + i));
        }
        Ceph ceph = new Ceph(map);
        ceph.put(a);
        System.out.println(ceph.get(a));

 */
            //Работа с файловой системой
            Client c = new Client();
            c.commandLine();
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
}