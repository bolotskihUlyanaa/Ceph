package ulyana.OSD;

import ulyana.Monitor.DiskBucket;

// для операций с osd
public interface DataOperation {

    boolean put(DiskBucket disk, Block block) throws Exception;

    Block get(DiskBucket disk, String blockID) throws Exception;

    boolean remove(DiskBucket disk, String blockID) throws Exception;
}
