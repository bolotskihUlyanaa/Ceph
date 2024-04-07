package ulyana;

import java.util.ArrayList;

public class Main {
    public static void main(String []args) {
        //создаем блок
        Block a = new Block("111111", "/Users/ulanabolotskih/Downloads/BigInt.hpp", 0, 20);

        //Создаем карту, пока что состоит только из компьютеров
        ArrayList<OSD> map = new ArrayList<OSD>();
        for(int i = 0; i < 100; i++){
            map.add(new OSD("192.168.0." + i));
        }

        Ceph ceph = new Ceph(map);
        ceph.put(a);
        System.out.println(ceph.get(a));
    }
}