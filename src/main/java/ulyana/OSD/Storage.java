package ulyana.OSD;

import ulyana.Client.Block;

//интерфейс определяющий хранение в постоянной или оперативной памяти
interface Storage{

    boolean save(Block block);

    Block get(String blockID);

    boolean remove(String blockID);
}