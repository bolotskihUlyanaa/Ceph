package ulyana.Monitor;

import org.junit.jupiter.api.Test;
import java.net.*;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

public class BucketTest {
    @Test
    public void add() {
        try {
            Bucket clusterMap = new Bucket("root", "root");
            Bucket row1 = new Bucket("row", "row1");
            clusterMap.add(row1);
            //проверка что на одном уровне должны быть сегменты только одного типа, то есть другого типа не добавятся
            clusterMap.add(new DiskBucket(InetAddress.getByName("localhost"), 1000));
            clusterMap.add(new Bucket("cabinet", "cabinet1"));
            ArrayList<Bucket> expected = new ArrayList<>();
            expected.add(row1);
            assertEquals(expected, clusterMap.getMap());
            //проверка метода find
            //не стала выносить в отдельный тест только из-за одной строки
            assertEquals(row1, clusterMap.find("row1"));

            //проверить добавление ещё одного уровня
            DiskBucket disk1000 = new DiskBucket(InetAddress.getByName("localhost"), 1000);
            DiskBucket disk1001 = new DiskBucket(InetAddress.getByName("localhost"), 1001);
            clusterMap.find("row1").add(disk1000);
            clusterMap.find("row1").add(disk1001);
            expected = new ArrayList<>();
            expected.add(disk1000);
            expected.add(disk1001);
            assertEquals(expected, clusterMap.find("row1").getMap());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
