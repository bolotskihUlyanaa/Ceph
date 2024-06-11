package ulyana.MDS;

import org.junit.Assert;
import ulyana.Monitor.*;
import ulyana.OSD.DO;

import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import java.net.InetAddress;
import java.util.ArrayList;

public class MDSSaveToOSDTest {
    @Test
    public void init() {
        try {
            MDS mds = new MDS();
            DO osd = new DO();
            MO mon = Mockito.mock(MO.class);
            int pgNum = 10;
            int countOfReplica = 3;
            Bucket clusterMap = new Bucket("root", "root", 1);
            for (int i = 0; i < 10; i++) {
                clusterMap.add(new DiskBucket(InetAddress.getLocalHost(), 11000 + i, 1));
            }
            Mockito.doReturn(clusterMap.getMap()).when(mon).getMap();
            Mockito.doReturn(pgNum).when(mon).getPGNum();
            Mockito.doReturn(countOfReplica).when(mon).getCountOfReplica();
            MDSSaveToOSD mdsSaveToOSD = new MDSSaveToOSD(mds, osd, mon);
            mdsSaveToOSD.start();
            mdsSaveToOSD.join();
            int pg = "0".hashCode() % pgNum;
            Crush crush = new Crush();
            ArrayList<DiskBucket> osdsDestination = crush.crush(Integer.toString(pg), clusterMap.getMap(), countOfReplica);//вычисление в какой компьютер сохранить
            for(int i = 0; i < countOfReplica; i++){
                Assert.assertNotNull(osd.get(osdsDestination.get(i), "0"));
            }
            MDSSaveToOSD mdsSaveToOSDNew = new MDSSaveToOSD(mds, osd, mon);
            mdsSaveToOSDNew.start();
            mdsSaveToOSDNew.join();
            for(int i = 0; i < countOfReplica; i++){
                Assert.assertNotNull(osd.get(osdsDestination.get(i), "0"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
