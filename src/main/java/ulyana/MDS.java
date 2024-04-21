package ulyana;

import java.io.*;
import java.util.ArrayList;

//сервер метаданных, в нем хранится файловая система
//для работы с cephfs надо всегда добавлять приставку /ceph/
public class MDS{
    static private int ID = 1;
    final private ArrayList<Block> blocks;
    final private InodeDirectory root;//корневой каталог
    private InodeDirectory curInode;//директория в которой мы сейчас находимся
    final private String ROOT = "ceph";//путь к любому файлу начинается /ROOT
    final int sizeBlock = 4096;//размер блока для разделения файла

    //вспомагательный класс для функций
    class Pair{
        final private String name;
        final private InodeDirectory inode;

        public Pair(String name, InodeDirectory directory){
            this.name = name;
            inode = directory;
        }
    }

    public MDS(){
        root = new InodeDirectory(ROOT, "", ID++);
        curInode = root;
        blocks = new ArrayList<Block>();
    }

    //добавить файл
    public InodeFile addInodeFile(String nameInode) throws Exception {
        Pair pair = findDirectory(nameInode);
        if(pair.name == null) throw new Exception("you can't create file without name");
        //проверка не существует ли уже файл с таким названием
        if(pair.inode.searchFile(pair.name) != null) {
            throw new Exception("such file already exist: ".concat(nameInode));
        }
        InodeFile inode = new InodeFile(pair.name, pair.inode.getPath(), ID++);
        pair.inode.addInode(inode);
        return inode;
    }

    //добавить подкаталог
    public void addInodeDirectory(String nameInode) throws Exception {
        Pair pair = findDirectory(nameInode);
        if(pair.name == null) throw new Exception("you can't create directory without name");
        //проверка не существует ли уже папка с таким названием в данном каталоге
        if(pair.inode.searchDirectory(pair.name) != null) {
            throw new Exception("such directory already exist: ".concat(nameInode));
        }
        pair.inode.addInode(new InodeDirectory(pair.name, pair.inode.getPath(), ID++));
    }

    //копирование файла внутри файловой системы
    public void copyInodeFS(String nameInodeSource, String nameInodeDestination) throws Exception {
        //ищем директорию источника
        Pair pairSource = findDirectory(nameInodeSource);
        if(pairSource.name == null) throw new Exception("you can't copy file without name");
        //ищем файл источник
        InodeFile inodeSource = pairSource.inode.searchFile(pairSource.name);
        if(inodeSource == null) throw new Exception("such file doesn't exist: ".concat(nameInodeSource));
        InodeFile inodeDestination = addInodeFile(nameInodeDestination);
        inodeDestination.setSize(inodeSource.size());
        inodeDestination.setCountBlock(inodeSource.getCountBlock());
        int IDSource = inodeSource.getID();//узнаем номер inode, который нужно скопировать
        int IDDestination = inodeDestination.getID();//узнаем номер inode, в который нужно скопировать
        for(int i = 0; i < inodeSource.getCountBlock(); i++){//собираем все блоки
            int numBlock = searchBlock(IDSource + "." + i);
            Block block = new Block(IDDestination + "." + i, inodeDestination.getPath(), i * sizeBlock, blocks.get(numBlock).getData());
            blocks.add(block);
        }
    }

    //копирование файла из ceph в локальный компьютер
    public void copyInodeFromFS(String nameInode, File directory, String nameFileCopy) throws Exception {//откуда и куда копируем
        //ищем директорию
        Pair pair = findDirectory(nameInode);
        if (pair.name == null) throw new Exception("you can't copy file without name");
        //ищем файл
        InodeFile inode = pair.inode.searchFile(pair.name);
        if (inode == null) throw new Exception("such file doesn't exist: ".concat(nameInode));

        //открываем файл источник, проверяем существует ли уже файл с таким именем
        File file = new File(directory, nameFileCopy);
        if(file.exists()){
            if(file.isDirectory()){
                file = new File(file, inode.toString());
            }
            else{
                throw new Exception("file with that name already exists: ".concat(nameFileCopy));
            }
        }
        else{
            File parent = file.getParentFile();
            if(!parent.isDirectory()) throw new Exception("such path doesn't exist: ".concat(nameFileCopy));
        }
        if (!file.isFile()) throw new Exception("error name file: ".concat(nameFileCopy));

        int ID = inode.getID();//узнаем номер inode, который нужно скопировать
        FileOutputStream fileBytes = new FileOutputStream(file);
        for (int i = 0; i < inode.getCountBlock(); i++) {//собираем все блоки
            int numBlock = searchBlock(ID + "." + i);
            fileBytes.write(blocks.get(numBlock).getData());
        }
        fileBytes.close();
    }

    //копирование из локального компьютера в ceph
    public void copyInodeToFS(File directory, String nameFileCopy, String nameInode) throws Exception {//откуда копируем и куда
        //открываем файл источник, проверяем существует ли такой файл
        File file = new File(directory, nameFileCopy);
        if (!file.exists()) throw new Exception("such file doesn't exist: ".concat(nameFileCopy));
        InodeFile inode = addInodeFile(nameInode);
        FileInputStream fileBytes = new FileInputStream(file);
        int size = fileBytes.available();//узнаем размер файла
        int countBlock = (size / sizeBlock) + 1;//вычисляем количество блоков, которые понадобятся для сохранения файла
        int inodeID = inode.getID();//узнаем ID файла, тк имя блока состоит из номера inode и (сейчас - номера блока)
        int countRead = sizeBlock;//сколько байт считать в блок
        inode.setSize(size);
        inode.setCountBlock(countBlock);
        //делим файл на блоки
        for (int j = 0; j < countBlock; j++) {
            if (fileBytes.available() < countRead) countRead = fileBytes.available();
            byte[] data = new byte[countRead];
            fileBytes.read(data);
            Block block = new Block(inodeID + "." + j, inode.getPath(), j * sizeBlock, data);
            blocks.add(block);
        }
        fileBytes.close();
    }

    //ходим только по директориям
    public void cd(String nameInode) throws Exception {
        //перенестись на один уровень выше
        if(nameInode.equals("..")) {
            nameInode = curInode.getLayout();//то есть нужно перейти в каталог в котором лежит подкаталог в котором мы сейчас находимся
        }
        Pair pair = findDirectory(nameInode);
        if(pair.name == null) curInode = pair.inode;
        else {
            InodeDirectory inode = pair.inode.searchDirectory(pair.name);
            if (inode == null) {
                throw new Exception("such directory doesn't exist: ".concat(nameInode));
            }
            curInode = inode;
        }
    }

    //посмотреть какие есть inode в текущем каталоге
    public String ls(String nameInode) throws Exception{
        InodeDirectory inode;
        Pair pair = findDirectory(nameInode);
        if(pair.name == null) inode = pair.inode;
        else{
            inode = pair.inode.searchDirectory(pair.name);
            if (inode == null) {
                throw new Exception("such directory doesn't exist: ".concat(nameInode));
            }
        }
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < inode.size(); i++) {
            str.append(inode.get(i));
            str.append(" ");
        }
        return str.toString();
    }

    //узнать путь к текущему каталогу
    public String pwd(){
        return curInode.getPath();
    }

    //если такую директорию не нашли возвращает null, иначе возвращает inodeDirectory в котором хотим создать и имя Inode который ходим создать
    public Pair findDirectory(String nameInode) throws Exception {
        InodeDirectory inode = curInode;
        if(nameInode.startsWith("/".concat(ROOT))){//если файл начинается /ROOT, то обрезаем это
            nameInode = nameInode.substring(ROOT.length() + 1);
        }
        if(nameInode.startsWith("/")){//если путь начинается с /, то поиск начинаем с корня, те /... тоже самое что и /root/...
            nameInode = nameInode.substring(1);
            inode = root;
        }
        if(nameInode.equals("")){//если после манипуляций выше осталась пустая строка, те не ввели имя файла
            return new Pair(null, inode);
        }
        //ищем нужную директорию
        String[] words = nameInode.split("/");
        for(int i = 0; i < words.length - 1; i++){
            inode = inode.searchDirectory(words[i]);
            if(inode == null){
                throw new Exception("no such directory: ".concat(nameInode));
            }
        }
        return new Pair(words[words.length - 1], inode);
    }

    //поиск блока в MDS
    public int searchBlock(String ID){
        for(int i = 0; i < blocks.size(); i++){
            if(blocks.get(i).getName().equals(ID)) return i;
        }
        return -1;
    }
}