package ulyana.OSD;

import ulyana.Client.Block;
import java.util.ArrayList;

//может быть использовать хэшмэп?
public class MemoryStorage implements Storage{
    private ArrayList<Block> blocks;

    public MemoryStorage(){
        blocks = new ArrayList<Block>();
    }

    //сохранить блок
    public void save(Block block){
        blocks.add(block);
    }

    //найти блок по ID
    public Block get(String blockID) {
        for(Block i:blocks)
            if(i.getName().equals(blockID)) return i;
        return null;
    }

    //удаление блока
    public void remove(String blockID){
        int i = 0;
        for(; i < blocks.size(); i++)
            if(blocks.get(i).getName().equals(blockID)) break;
        blocks.remove(i);
    }
}