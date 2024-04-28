package ulyana.MDS;

import java.util.ArrayList;

//сервер метаданных, в нем хранится файловая система
//для работы с cephfs надо всегда добавлять приставку /ceph/
public class MDS{
    static private int ID = 1;
    final private InodeDirectory root;//корневой каталог
    private InodeDirectory curInode;//директория в которой мы сейчас находимся
    final private String ROOT = "ceph";//путь к любому файлу начинается /ROOT

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

    //удаление файла
    //возвращает номер inode
    public InodeFile removeFile(String nameInode) throws Exception{
        //ищем директорию
        Pair pair = findDirectory(nameInode);
        if (pair.name == null) throw new Exception("you can't copy file without name");
        //ищем файл
        InodeFile inode = find(nameInode);
        if (inode == null) throw new Exception("such file doesn't exist: ".concat(nameInode));
        //удлалить блоки и inode
        //должны вернуть номер inode и клиент удалит из OSD
        int inodeID = inode.getID();
        pair.inode.delete(inodeID);//в директории удаляем inode
        return inode;
    }

    //удалить каталог
    public ArrayList<InodeFile> removeDirectory(String nameInode) throws Exception {
        ArrayList<InodeFile> inodeID = new ArrayList<InodeFile>();
        Pair pair = findDirectory(nameInode);
        if (pair.name == null) throw new Exception("you can't copy file without name");
        InodeDirectory inode = pair.inode.searchDirectory(pair.name);
        if (inode == null) throw new Exception("such directory doesn't exist: ".concat(nameInode));
        remove(inode, inodeID);
        int curInodeID = inode.getID();
        pair.inode.delete(curInodeID);
        return inodeID;
    }

    //чтобы пройти по иерархии и удалить все нижележащие inode
    //arraylist нужен чтобы запоминать номера inode файлов, чтобы удалить эти блоки с диска
    private void remove(InodeDirectory inode, ArrayList<InodeFile> inodeID){
        if (curInode.getID() == inode.getID()) curInode = root;
        for(int i = 0; i < inode.size(); i++){
            if (inode.get(i).getType() == 1) remove((InodeDirectory) inode.get(i), inodeID);
            else inodeID.add((InodeFile) inode.get(i));
        }
        inode.removeAll();
    }

    //найти файл, те чтобы получить inode
    public InodeFile find(String nameInode)throws Exception{
        //ищем директорию
        Pair pair = findDirectory(nameInode);
        if (pair.name == null) throw new Exception("you can't copy file without name");
        //ищем файл
        InodeFile inode = pair.inode.searchFile(pair.name);
        if (inode == null) throw new Exception("such file doesn't exist: ".concat(nameInode));
        return inode;
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

}