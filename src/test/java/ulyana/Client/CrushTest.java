package ulyana.Client;

import org.junit.Test;
import ulyana.Monitor.*;
import java.net.*;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

public class CrushTest {
    @Test
    public void select(){
        try {
            //создадим карту из одного компьютера и проверим что выберем этот компьютер
            ArrayList<Bucket> map = new ArrayList<Bucket>();
            map.add(new DiskBucket(InetAddress.getLocalHost(), 10000));
            Crush crush = new Crush();
            crush.setMap(map);
            crush.select("123.3", 1, "OSD");
            ArrayList<ArrayList<Bucket>> result = crush.get();
            assertEquals(map, result.get(0));

            //проверка на иерархической структуре
            //создаем карту из двух row, в одном row один диск
            Bucket clusterMap = new Bucket("root", "root");
            Bucket row1 = new Bucket("row", "row1");
            Bucket row2 = new Bucket("row", "row2");
            clusterMap.add(row1);
            clusterMap.add(row2);
            DiskBucket disk1001 = new DiskBucket(InetAddress.getByName("localhost"), 1001);
            DiskBucket disk1002 = new DiskBucket(InetAddress.getByName("localhost"), 1002);
            clusterMap.find("row1").add(disk1001);
            clusterMap.find("row2").add(disk1002);
            crush = new Crush();//создаем новый объект так как карта меняется, а метод setMap не заново задает карту
            crush.setMap(clusterMap.getMap());
            //выбираем 2 сегмента типа row
            crush.select("123.1", 2, "row");
            result = crush.get();
            ArrayList<Bucket> expected = new ArrayList<>();
            expected.add(row1);
            expected.add(row2);
            assertEquals(expected, result.get(0));
            //выбираем в каждом row один диск
            crush = new Crush();
            crush.setMap(result.get(0).get(0).getMap());
            crush.setMap(result.get(0).get(1).getMap());
            crush.select("123.1", 1, "OSD");
            result = crush.get();
            expected = new ArrayList<>();
            expected.add(disk1001);
            expected.add(disk1002);
            assertEquals(expected, result.get(0));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void isPrime(){
        int[] primeNumber = new int[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29 ,31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101};
        boolean[] expected = new boolean[102];
        for(int i = 0; i < primeNumber.length; i++){
            expected[primeNumber[i]] = true;
        }
        Crush crush = new Crush();
        for(int i = 1; i < 102; i++){
            assertEquals(expected[i], crush.isPrime(i));
        }
    }

    @Test
    public void getPrimeNumber(){
        Crush crush = new Crush();
        int expected = 0;
        for(int i = 0; i < 101; i++){
            if(i < 2) expected = 2;
            if(i == 2) expected = 3;
            if(i >= 3 && i < 5) expected = 5;
            if(i >= 5 && i < 7) expected = 7;
            if(i >= 7 && i < 11) expected = 11;
            if(i >= 11 && i < 13) expected = 13;
            if(i >= 13 && i < 17) expected = 17;
            if(i >= 17 && i < 19) expected = 19;
            if(i >= 19 && i < 23) expected = 23;
            if(i >= 23 && i < 29) expected = 29;
            if(i >= 29 && i < 31) expected = 31;
            if(i >= 31 && i < 37) expected = 37;
            if(i >= 37 && i < 41) expected = 41;
            if(i >= 41 && i < 43) expected = 43;
            if(i >= 43 && i < 47) expected = 47;
            if(i >= 47 && i < 53) expected = 53;
            if(i >= 53 && i < 59) expected = 59;
            if(i >= 59 && i < 61) expected = 61;
            if(i >= 61 && i < 67) expected = 67;
            if(i >= 67 && i < 71) expected = 71;
            if(i >= 71 && i < 73) expected = 73;
            if(i >= 73 && i < 79) expected = 79;
            if(i >= 79 && i < 83) expected = 83;
            if(i >= 83 && i < 89) expected = 89;
            if(i >= 89 && i < 97) expected = 97;
            if(i >= 97) expected = 101;
            assertEquals(expected, crush.getPrimeNumber(i));
        }
    }
}
