package ulyana.OSD;

import ulyana.Client.Block;

interface Storage{
    void save(Block block);

    Block get(String blockID);

    void remove(String blockID);
}