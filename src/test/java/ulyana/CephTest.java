package ulyana;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ulyana.Client.Client;
import ulyana.MDS.MDO;
import ulyana.Monitor.Bucket;
import ulyana.Monitor.DiskBucket;
import ulyana.Monitor.MO;
import ulyana.OSD.DO;

import java.net.InetAddress;

public class CephTest {
    private MDO mds;
    private DO osd;
    private Client client;

    @BeforeEach
    public void init(){
        MO mon = Mockito.mock(MO.class);
        int pgNum = 10;
        int countOfReplica = 3;
        Bucket clusterMap = new Bucket("root", "root", 1);
        try {
            for (int i = 0; i < 10; i++) {
                clusterMap.add(new DiskBucket(InetAddress.getLocalHost(), 11000 + i, 1));
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        Mockito.doReturn(clusterMap.getMap()).when(mon).getMap();
        Mockito.doReturn(pgNum).when(mon).getPGNum();
        Mockito.doReturn(countOfReplica).when(mon).getCountOfReplica();
        mds = new MDO();
        osd = null;
        try {
            osd = new DO();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        client = new Client(mds, osd, mon);
        client.commandLine("mkdir /ceph/res /ceph/tar /ceph/res/test /ceph/res/src");
    }

    @Test
    public void mkdir(){
        client.commandLine("mkdir /ceph/test /ceph/noname/test /ceph/res/test /ceph/res/t");
        Assert.assertEquals("res tar test ", mds.ls(""));
        Assert.assertEquals("test src t ", mds.ls("res"));
    }

    @Test
    public void ls(){
        Assert.assertEquals("res tar ", mds.ls(""));
        Assert.assertEquals("res tar ", mds.ls(""));
        Assert.assertEquals("res tar ", mds.ls("/"));
        Assert.assertEquals("res tar ", mds.ls("/ceph"));
        Assert.assertEquals("res tar ", mds.ls("/ceph/"));
        Assert.assertEquals("test src ", mds.ls("res"));
        Assert.assertEquals("", mds.ls("res/src"));
        Assert.assertEquals("", mds.ls("res/src/"));
        Assert.assertEquals("such directory doesn't exist: noname", mds.ls("noname"));
    }

    @Test
    public void touch(){
        client.commandLine("touch /ceph/text /ceph/res/png /ceph/");
        Assert.assertEquals("res tar text ", mds.ls(""));
        Assert.assertEquals("text", mds.find("text").toString());
        Assert.assertEquals("png", mds.find("res/png").toString());
    }

    @Test
    public void cd(){
        Assert.assertEquals("/ceph", mds.pwd());
        client.commandLine("cd /ceph/res/png");
        Assert.assertEquals("/ceph", mds.pwd());
        client.commandLine("cd /ceph/res/test");
        Assert.assertEquals("/ceph/res/test", mds.pwd());
        client.commandLine("cd /ceph/..");
        Assert.assertEquals("/ceph/res", mds.pwd());
        client.commandLine("cd /ceph//");
        Assert.assertEquals("/ceph", mds.pwd());
    }

    @Test
    public void rm() {
        client.commandLine("touch /ceph/text /ceph/res/png");
        client.commandLine("rm /ceph/text /ceph/res /ceph/noname");
        Assert.assertEquals("res tar ", mds.ls(""));
        client.commandLine("cd /ceph/res/src");
        client.commandLine("rm -r /ceph//tar /ceph//res /ceph/noname");
        Assert.assertEquals("", mds.ls(""));
        Assert.assertEquals("/ceph", mds.pwd());
    }

    @Test
    public void cp(){
        client.commandLine("touch /ceph/text");
        client.commandLine("cp /ceph/text /ceph/png");
        Assert.assertEquals("res tar text png ", mds.ls(""));
    }
}
