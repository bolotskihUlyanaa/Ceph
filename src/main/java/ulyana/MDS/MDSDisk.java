package ulyana.MDS;

import java.io.*;
import java.util.ArrayList;

//операции если mds хранится на диске
public class MDSDisk{
    final private String path;//путь где будет храниться файл

    public MDSDisk(String path){
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

    //добавить файл
    public Object addInodeFile(String nameInode, int size, int countBlock) throws IOException, ClassNotFoundException{
        MDS mds = load();
        Object result = mds.addInodeFile(nameInode, size, countBlock);
        saveToFile(mds);
        return result;
    }

    //добавить подкаталог
    public Object addInodeDirectory(String nameInode) throws IOException, ClassNotFoundException{
        MDS mds = load();
        Object result = mds.addInodeDirectory(nameInode);
        saveToFile(mds);
        return result;
    }

    //удаление файла
    public InodeFile removeFile(String nameInode) throws IOException, ClassNotFoundException{
        MDS mds = load();
        InodeFile result = mds.removeFile(nameInode);
        saveToFile(mds);
        return result;
    }

    //удалить каталог
    public ArrayList<InodeFile> removeDirectory(String nameInode) throws IOException, ClassNotFoundException{
        MDS mds = load();
        ArrayList<InodeFile> result = mds.removeDirectory(nameInode);
        saveToFile(mds);
        return result;
    }

    //найти файл, т.е. получить inode
    public InodeFile find(String nameInode) throws IOException, ClassNotFoundException{
        MDS mds = load();
        return mds.find(nameInode);
    }

    //посмотреть какие есть inode в текущем каталоге
    public String ls(String nameInode) throws IOException, ClassNotFoundException{
        MDS mds = load();
        return mds.ls(nameInode);
    }

    //перейти в директорию
    public boolean cd(String nameInode) throws IOException, ClassNotFoundException{
        MDS mds = load();
        boolean result = mds.cd(nameInode);
        saveToFile(mds);
        return result;
    }

    //узнать путь к текущему каталогу
    public String pwd() throws IOException, ClassNotFoundException{
        MDS mds = load();
        return mds.pwd();
    }

    //загрузить mds из файла
    public MDS load() throws IOException, ClassNotFoundException {
        FileInputStream inputStream = new FileInputStream(path);
        //файл может быть пустой потому что в нем ещё не хранили данные,
        //чтобы не выдавало ошибку нужна эта проверка
        MDS mds;
        if (inputStream.available() > 0) {
            ObjectInputStream in = new ObjectInputStream(inputStream);
            mds = (MDS) in.readObject();
            in.close();
        } else mds = new MDS();
        inputStream.close();
        return mds;
    }

    //сохранить в файл
    public void saveToFile(MDS mds) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(path);
        ObjectOutputStream out = new ObjectOutputStream(outputStream);
        out.writeObject(mds);
        out.close();
        outputStream.close();
    }
}
