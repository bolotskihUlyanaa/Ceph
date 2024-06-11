package ulyana;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import ulyana.Client.*;
import ulyana.MDS.*;
import ulyana.Monitor.*;
import ulyana.OSD.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class FaultToleranceTest {
    private MDONet mdoNet;
    private DONet doNet;
    private Client client;
    private ComponentThread mds;
    private MDSNet mds2;
    private ComponentThread monitor;
    private MonitorNet mon;
    private ComponentThread osd1;
    private ComponentThread osd2;
    private MonitorOperation mo;
    private final String sourPath = "res/test/p.bmp";
    private final String resPath = "res/test/res";
    int portMonitor = 9006;

    @Test
    public void init() throws Exception{
        List<DiskBucket> disks = new ArrayList<>();
        for(int i = 0; i < 2; i++) {
            disks.add(new DiskBucket(InetAddress.getByName("localhost"), 9002 + i, 1));
        }
        mon = new MonitorNet(new Monitor(portMonitor, 2, 1, disks));
        monitor = new ComponentThread(mon);
        monitor.start();
        osd1 = new ComponentThread(new OSDNet(9002));
        osd2 = new ComponentThread(new OSDNet(9003));
        osd1.start();
        osd2.start();
        mds2 = new MDSNet(9999, portMonitor);
        mds = new ComponentThread(mds2);
        mds.start();
        mdoNet = new MDONet(InetAddress.getByName("localhost"), 9999);
        doNet = new DONet();
        mo = new MONet(InetAddress.getByName("localhost"), portMonitor);
        client = new Client(mdoNet, doNet, mo);
        client.commandLine("mkdir /ceph//res /ceph//tar");
        client.commandLine("cd /ceph//res");
        faultMDS();
        faultOSD();

        File file = new File("res/OSDs/9002.txt");
        file.delete();
        file = new File("res/OSDs/9003.txt");
        file.delete();
        file = new File("res/OSDs/9004.txt");
        file.delete();
    }

    public void faultMDS() throws Exception {
        MDSNet mdsNew = new MDSNet(9011, portMonitor);
        Assert.assertEquals(true, mds2.getMDS().equals(mdsNew.getMDS()));
    }

    public void faultOSD() throws IOException, ClassNotFoundException {
        client.commandLine("cp " + sourPath + " /ceph/p");
        client.commandLine("cp /ceph/p " + resPath);
        equalsFile();

        //теперь отключим 1 osd
        SendThread.send(InetAddress.getByName("localhost"), portMonitor, "osdChangeCon localhost 9002 0");
        client.commandLine("cp /ceph/p " + resPath);
        equalsFile();

        //создаетя новый osd и синхронизируется со вторым osd, затем второй osd отключается
        OSDNet osdSync = new OSDNet(9004, new MONet(InetAddress.getByName("localhost"), portMonitor), new DONet());
        ComponentThread osd3 = new ComponentThread(osdSync);
        osd3.start();
        SendThread.send(InetAddress.getByName("localhost"), portMonitor, "osdChangeCon localhost 9004 1");
        SendThread.send(InetAddress.getByName("localhost"), portMonitor, "osdChangeCon localhost 9003 0");
        client.commandLine("cp /ceph/p " + resPath);
        equalsFile();

        //теперь отключим последний osd
        SendThread.send(InetAddress.getByName("localhost"), portMonitor, "osdChangeCon localhost 9004 0");
        try {
            client.commandLine("cp /ceph/p " + resPath);
        } catch (Exception ex) {
            Assert.assertEquals("get file failure", ex.getMessage());
        }
        File f = new File(resPath);
        f.delete();
    }

    public void equalsFile() throws IOException{
        FileInputStream sourFile = new FileInputStream(sourPath);
        FileInputStream resFile = new FileInputStream(resPath);
        int sourAv = sourFile.available();
        byte[] sourData = new byte[sourAv];
        sourFile.read(sourData);
        int resAv = resFile.available();
        byte[] resData = new byte[resAv];
        resFile.read(resData);
        Assert.assertEquals(sourAv, resAv);
        for (int i = 0; i < resAv; i++) {
            Assert.assertEquals(sourData[i], resData[i]);
        }
        sourFile.close();
        resFile.close();
        File f = new File(resPath);
        f.delete();
    }
}
