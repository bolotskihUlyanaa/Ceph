package ulyana.Monitor;

import java.net.InetAddress;
import java.util.ArrayList;
import org.junit.Assert;
import org.mockito.Mockito;
import org.junit.jupiter.api.Test;

public class CalculateOSDTest {

    @Test
    public void getOSDs() {
        try {
            MO mon = Mockito.mock(MO.class);
            int pgNum = 10;
            int countOfReplica = 3;
            Bucket clusterMap = new Bucket("root", "root");
            for (int i = 0; i < 10; i++) {
                clusterMap.add(new DiskBucket(InetAddress.getLocalHost(), 11000 + i));
            }
            Mockito.doReturn(clusterMap.getMap()).when(mon).getMap();
            Mockito.doReturn(pgNum).when(mon).getPGNum();
            Mockito.doReturn(countOfReplica).when(mon).getCountOfReplica();

            String nameBlock = "1.1";
            Crush crush = new Crush();
            int pg = nameBlock.hashCode() % pgNum;
            ArrayList<DiskBucket> osdsDestination = crush.crush(Integer.toString(pg), clusterMap.getMap(), countOfReplica);
            CalculateOSD calculateOSD = new CalculateOSD(mon);
            ArrayList<DiskBucket> osdCalculate = calculateOSD.getOSDs(nameBlock);
            Assert.assertEquals(countOfReplica, osdCalculate.size());
            for (int i = 0; i < countOfReplica; i++) {
                Assert.assertEquals(osdsDestination.get(i), osdCalculate.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
