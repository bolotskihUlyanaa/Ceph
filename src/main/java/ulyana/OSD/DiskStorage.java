package ulyana.OSD;

import ulyana.Client.Block;
import java.io.*;
import java.util.ArrayList;

//для хранения блоков в постоянной памяти компьютера
public class DiskStorage implements Storage {
    final private String path;//путь до файла в постоянной памяти компьютера

    public DiskStorage(String path) {
        try {
            String[] directoires = path.split("/");
            for (int j = 0; j <= directoires.length - 1; j++) {
                StringBuilder directory = new StringBuilder();
                for (int i = 0; i < j; i++) {
                    directory.append(directoires[i] + "/");
                }
                File f = new File(directory.toString());
                if (!f.isDirectory()) f.mkdirs();
            }
            File file = new File(path);
            file.createNewFile();//если такого файла не существует, создадим его
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        this.path = path;
    }

    //сохранить блок
    public boolean save(Block block) {
        try {
            ArrayList<Block> blocks = load();//загружаем все блоки из файла
            blocks.add(block);//добавляем к полученным из файла блокам, блок который прислали для сохранения
            saveToFile(blocks);//сохранить все обратно в файл
            return true;
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
            return false;
        }
        catch(Exception ex){
            return false;
        }
    }

    //сохранить блоки в файл, сериализация
    public void saveToFile(ArrayList<Block> blocks) throws Exception {
        FileOutputStream outStream = new FileOutputStream(path);
        ObjectOutputStream out = new ObjectOutputStream(outStream);
        for (Block i : blocks) out.writeObject(i);
        out.close();
        outStream.close();
    }

    //прочитать блоки из файла, десериализация
    public ArrayList<Block> load() throws Exception {
        ArrayList<Block> array = new ArrayList<Block>();
        FileInputStream inStream = new FileInputStream(path);
        //файл может быть пустой потому что в нем ещё не хранят блоки или стерли данные,
        //чтобы не выдавало ошибку нужна эта проверка
        if (inStream.available() > 0) {
            ObjectInputStream in = new ObjectInputStream(inStream);
            while (inStream.available() > 0) {
                Block block = (Block) in.readObject();
                array.add(block);
            }
            in.close();
        }
        inStream.close();
        return array;
    }

    //найти блок в OSD по id
    public Block get(String blockId) {
        try {
            ArrayList<Block> blocks = load();
            for (Block i : blocks) { //ищем блок по ID
                if (i.getName().equals(blockId)) return i;
            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }

    //удаление блока
    public boolean remove(String blockID){
        try {
            ArrayList<Block> blocks = load();
            int i = 0;
            //ищем блок по id
            for (; i < blocks.size(); i++)
                if (blocks.get(i).getName().equals(blockID)) break;
            //если такого блока не нашлось
            if(i == blocks.size()) return false;
            blocks.remove(i);
            saveToFile(blocks);
            return true;
        }catch(IOException ex) {
            System.out.println(ex.getMessage());
            return false;
        } catch(Exception ex){
            return false;
        }
    }
}