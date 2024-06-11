package ulyana.MDS;

import ulyana.Monitor.*;
import ulyana.OSD.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import static java.lang.String.*;

//отдельный поток для сохранения на osd
public class MDSSaveToOSD extends Thread {
    final private MDS mds;
    final private DataOperation osd;
    final private MonitorOperation monitor;
    final private String inodeNumber = "0";//нужен для сохранения на osd
    final private int sizeBlock = 4096;

    public MDSSaveToOSD(MDS mds, DataOperation osd, MonitorOperation monitor) {
        this.osd = osd;
        this.mds = mds;
        this.monitor = monitor;
    }

    public void run() {
        try {
            Date date = new Date();
            //представляем mds в виде потока байтов
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            out.writeObject(mds);
            byte[] mdsByte = outputStream.toByteArray();
            int sizeCurrent = mdsByte.length;
            String sizeStringCurrent = valueOf(sizeCurrent);
            byte[] sizeCurrentBytes = sizeStringCurrent.getBytes();
            int countBlockCurrent = (sizeCurrent / sizeBlock) + 1;
            CalculateOSD calculateOSD = new CalculateOSD(monitor);
            ArrayList<DiskBucket> osds = calculateOSD.getOSDs(inodeNumber);
            Block block = null;
            for(DiskBucket disk:osds) {
                block = osd.get(disk, inodeNumber);
                if (block != null) break;
            }
            boolean wasSaved;
            if (block == null)
                wasSaved = false;
            else
                wasSaved = true;
            int countBlockPrev = 0;
            if (wasSaved) {
                String sizeStringPrevious = new String(block.getData());
                int sizePrevious = Integer.parseInt(sizeStringPrevious);
                countBlockPrev = (sizePrevious / sizeBlock) + 1;
            }
            block = new Block(inodeNumber, 0, sizeCurrentBytes, date);
            for (DiskBucket disk : osds) {
                if (wasSaved) {
                    osd.remove(disk, inodeNumber);
                    //if (!osd.remove(disk, inodeNumber))
                    //    throw new Exception("Save mds to osd error \nRemove inode failure");
                }
                osd.put(disk, block);
                //if (!osd.put(disk, block))
                //    throw new Exception("Save mds to osd error \nSave file failure");
            }

            //делим mds на блоки
            int countRead = sizeBlock;
            for (int i = 0; i < countBlockCurrent; i++) {
                if (sizeCurrent < sizeBlock)
                    countRead = sizeCurrent;
                byte[] data = new byte[countRead];
                System.arraycopy(mdsByte, i * sizeBlock, data, 0, countRead);
                String id = inodeNumber.concat(".") + i;
                block = new Block(id, sizeBlock * i, data, date);
                osds = calculateOSD.getOSDs(inodeNumber);
                for (DiskBucket disk : osds) {
                    if (wasSaved && countBlockPrev > i) {
                        osd.remove(disk, id);
                        //if (!osd.remove(disk, id))
                        //    throw new Exception("Save mds to osd error \nRemove inode failure");
                    }
                    osd.put(disk, block);
                    //if (!osd.put(disk, block))
                    //    throw new Exception("Save mds to osd error \nSave file failure");
                }
                sizeCurrent -= sizeBlock;
            }
            out.close();
            outputStream.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
