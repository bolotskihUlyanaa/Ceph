package ulyana.OSD;

interface Storage {

    boolean save(Block block);

    Block get(String blockID);

    boolean remove(String blockID);
}