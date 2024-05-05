package ulyana.Client;

import ulyana.Monitor.DiskBucket;

//интерфейс для операций с блоками
public interface DataOperation {

    boolean put(DiskBucket disk, Block block) throws Exception;

    Block get(DiskBucket disk, String blockID) throws Exception;

    boolean remove(DiskBucket disk, String blockID) throws Exception;

}
