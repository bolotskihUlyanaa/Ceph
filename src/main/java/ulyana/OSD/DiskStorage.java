package ulyana.OSD;

import ulyana.Client.SendThread;
import ulyana.MDS.InodeDirectory;
import ulyana.MDS.InodeFile;
import ulyana.Monitor.CalculateOSD;
import ulyana.Monitor.DiskBucket;
import ulyana.Monitor.MonitorOperation;

import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//для хранения блоков в постоянной памяти компьютера
public class DiskStorage implements Storage {
    final private String path;//путь до файла компьютера
    private ArrayList<Block> blocks;

    public DiskStorage(String path) {
        this.path = path;
        try {
            String[] directories = path.split("/");
            for (int j = 0; j <= directories.length - 1; j++) {
                StringBuilder directory = new StringBuilder();
                for (int i = 0; i < j; i++) {
                    directory.append(directories[i] + "/");
                }
                File f = new File(directory.toString());
                if (!f.isDirectory())
                    f.mkdirs();
            }
            File file = new File(path);
            if (!file.createNewFile()) {
                blocks = load();
            } else {
                blocks = new ArrayList<>();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public DiskStorage(InetAddress ip, int port, String path, MonitorOperation monitor, DONet otherOsd) {
        this(path);
        try {
            recovery(ip, port, monitor, otherOsd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recovery(InetAddress ip, int port, MonitorOperation monitor, DONet otherOsd) throws Exception {
        InodeDirectory mds = (InodeDirectory) SendThread.send(InetAddress.getLocalHost(), 9999, "mds");
        List<InodeFile> inodesFile = new ArrayList<>();
        DeepFirstSearch(mds, inodesFile);
        CalculateOSD calculateOSD = new CalculateOSD(monitor);
        for (InodeFile inode:inodesFile) {
            Date dateFile = inode.getDate();
            for (int i = 0; i < inode.getCountBlock(); i++) {
                String idBlock = inode.getID() + "." + i;
                int index = searchBlock(idBlock);
                if (index != -1) {
                    Block oldBlock = blocks.get(index);
                    if (oldBlock.getDate().equals(dateFile)) continue;
                    blocks.remove(index);
                }
                ArrayList<DiskBucket> osds = calculateOSD.getOSDs(idBlock);
                Block block = null;
                for (DiskBucket disk : osds) {
                    if (!(ip.equals(disk.getIP()) && port == disk.getPort()))
                        block = otherOsd.get(disk, idBlock);
                    if (block != null) break;
                }
                if (block != null)
                    blocks.add(block);
            }
        }
        saveToFile();
    }

    private void DeepFirstSearch(InodeDirectory inode, List<InodeFile> inodesFile) {
        for (int i = 0; i < inode.size(); i++) {
            if (inode.get(i).getType() == 1)
                DeepFirstSearch((InodeDirectory) inode.get(i), inodesFile);
            else
                inodesFile.add((InodeFile) inode.get(i));
        }
    }

    //сохранить блок
    public boolean save(Block block) {
        try {
            blocks.add(block);//добавляем к полученным из файла блокам, блок который прислали для сохранения
            saveToFile();//сохранить все обратно в файл
            return true;
        } catch(IOException ex) {
            System.out.println(ex.getMessage());
            return false;
        } catch(Exception ex) {
            return false;
        }
    }

    //сохранить блоки в файл, сериализация
    public void saveToFile() throws Exception {
        FileOutputStream outStream = new FileOutputStream(path);
        ObjectOutputStream out = new ObjectOutputStream(outStream);
        for (Block i : blocks)
            out.writeObject(i);
        out.close();
        outStream.close();
    }

    //прочитать блоки из файла, десериализация
    public ArrayList<Block> load() throws Exception {
        ArrayList<Block> array = new ArrayList<>();
        FileInputStream inStream = new FileInputStream(path);
        //файл может быть пустой потому что в нем ещё не хранят блоки или стерли данные
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

    public int searchBlock(String blockId){
        for (int i = 0; i < blocks.size(); i++){
            if (blocks.get(i).getName().equals(blockId)) return i;
        }
        return -1;
    }

    public Block get(String blockId) {
        int index = searchBlock(blockId);
        if (index != -1) return blocks.get(index);
        else return null;
    }

    //удаление блока
    public boolean remove(String blockId) {
        try {
            int index = searchBlock(blockId);
            if (index == -1) return false;//если такого блока не нашлось
            blocks.remove(index);
            saveToFile();
            return true;
        } catch(IOException ex) {
            System.out.println(ex.getMessage());
            return false;
        } catch(Exception ex) {
            return false;
        }
    }
}