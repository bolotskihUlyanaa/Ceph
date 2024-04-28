package ulyana.OSD;

import ulyana.Client.Block;
import java.io.*;
import java.util.ArrayList;

public class DiskStorage implements Storage {
    final private String path;

    public DiskStorage(String path) {
        File file = new File(path);
        try {
            file.createNewFile();//если такого файла не существует, создадим его
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        this.path = path;
    }

    //сохранить в файл
    public void save(Block block) {
        ArrayList<Block> blocks = load();
        blocks.add(block);
        saveToFile(blocks);
    }

    //сериализация
    public void saveToFile(ArrayList<Block> blocks) {
        try {
            FileOutputStream a = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(a);
            for (Block i : blocks) out.writeObject(i);
            out.close();
            a.close();
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    //десериализация
    public ArrayList<Block> load() {
        ArrayList<Block> array = new ArrayList<Block>();
        try {
            FileInputStream a = new FileInputStream(path);
            if(a.available() > 0) {
                ObjectInputStream in = new ObjectInputStream(a);
                while (a.available() > 0) {
                    Block block = (Block) in.readObject();
                    array.add(block);
                }
                in.close();
            }
            a.close();
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        return array;
    }

    //найти блок в OSD
    public Block get(String blockId) {
        ArrayList<Block> blocks = load();
        for(Block i:blocks) //ищем блок по ID
            if (i.getName().equals(blockId)) return i;
        return null;
    }

    //удаление блока
    public void remove(String blockID){
        ArrayList<Block> blocks = load();
        int i = 0;
        for(; i < blocks.size(); i++)
            if(blocks.get(i).getName().equals(blockID)) break;
        blocks.remove(i);
        saveToFile(blocks);
    }
}