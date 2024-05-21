package ulyana.OSD;

import ulyana.Client.SendThread;
import ulyana.Monitor.DiskBucket;

//класс для обращения к osd при помощи сокетов
public class DONet implements DataOperation{

    public boolean put(DiskBucket disk, Block block) throws Exception {
        return (boolean) SendThread.send(disk.getIP(), disk.getPort(), block);//отправляем блок на ip и порт (osd)
    }

    public Block get(DiskBucket disk, String blockID) throws Exception {
        return (Block) SendThread.send(disk.getIP(), disk.getPort(), blockID);//отправляем на osd номер блока чтобы его взять
    }

    public boolean remove(DiskBucket disk, String blockID) throws Exception {
        return (boolean) SendThread.send(disk.getIP(), disk.getPort(), "rm ".concat(blockID));
    }
}
