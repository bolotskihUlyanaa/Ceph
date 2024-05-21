package ulyana.OSD;

import java.util.ArrayList;

//хранение в оперативной памяти
public class MemoryStorage implements Storage {
    final private ArrayList<Block> blocks;

    public MemoryStorage() {
        blocks = new ArrayList<Block>();
    }

    public boolean save(Block block) {
        return blocks.add(block);
    }

    public Block get(String blockID) {
        for (Block i:blocks)
            if (i.getName().equals(blockID))
                return i;
        return null;
    }

    public boolean remove(String blockID) {
        int i = 0;
        for (; i < blocks.size(); i++)
            if (blocks.get(i).getName().equals(blockID))
                break;
        //если такого блока не нашлось
        if (i == blocks.size())
            return false;
        blocks.remove(i);
        return true;
    }
}