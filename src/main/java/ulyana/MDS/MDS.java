package ulyana.MDS;

import java.io.Serializable;
import java.util.ArrayList;

//сервер метаданных, в нем хранится файловая система
public class MDS implements Serializable {
    private static final long SerialVersionUID = 1600;//для корректной сериализации
    private int ID = 1;
    final private InodeDirectory root;//корневой каталог
    private InodeDirectory curInode;//директория в которой мы сейчас находимся
    final private String ROOT = "ceph";//путь к любому файлу начинается /ROOT

    //вспомагательный класс для функций
    class Pair {
        final private String name;
        final private InodeDirectory inode;

        public Pair(String name, InodeDirectory directory) {
            this.name = name;
            inode = directory;
        }

        //для MDSTest.findDirectory
        public String toString() {
            if (name == null)
                return "null ".concat(inode.toString());
            else
                return name.concat(" ").concat(inode.toString());
        }
    }

    public MDS() {
        root = new InodeDirectory(ROOT, "", ID++);
        curInode = root;
    }

    //добавить файл
    //вернулась строка - это причина почему файл нельзя создать
    //вернулось число - номер inode который создали
    //почему возвращаем id? чтобы потом сохранить блоки а для этого нужен ID
    public Object addInodeFile(String nameInode, int size, int countBlock) {
        try {
            Pair pair = findDirectory(nameInode);//ищем директорию в которую нужно добавить файл
            if (pair.name == null)
                throw new Exception("you can't create file without name");
            //проверка не существует ли уже файл с таким названием
            if (pair.inode.searchFile(pair.name) != null)
                throw new Exception("such file already exist: ".concat(nameInode));
            InodeFile inode = new InodeFile(pair.name, pair.inode.getPath(), ID++, size, countBlock);
            pair.inode.addInode(inode);//сохраняем новый inode в директорию
            return inode.getID();
        } catch(Exception ex) {
            return ex.getMessage();
        }
    }

    //добавить подкаталог
    //вернулась строка - это причина почему подкаталог нельзя создать
    //вернулось число - номер inode который создали
    public Object addInodeDirectory(String nameInode) {
        try {
            Pair pair = findDirectory(nameInode);//ищем директорию в которую нужно добавить директорию
            if (pair.name == null)
                throw new Exception("you can't create directory without name");
            //проверка не существует ли уже каталог с таким названием в данном каталоге
            if (pair.inode.searchDirectory(pair.name) != null)//ищем директорию в директории
                throw new Exception("such directory already exist: ".concat(nameInode));
            //создаем новую inode-директорию и добавляем ее в директорию
            InodeDirectory inode = new InodeDirectory(pair.name, pair.inode.getPath(), ID++);
            pair.inode.addInode(inode);
            return inode.getID();
        } catch(Exception ex){
            return ex.getMessage();
        }
    }

    //удаление файла
    //возвращает inode если такой файл найден и удален, иначе null
    public InodeFile removeFile(String nameInode) {
        try {
            Pair pair = findDirectory(nameInode);//ищем директорию в которой нужно удалить файл
            if (pair.name == null)
                throw new Exception("such directory doesn't exist");//каталог не найден
            InodeFile inode = pair.inode.searchFile(pair.name);//ищем файл в директории
            if (inode == null)
                throw new Exception("such file doesn't exist");//файл не найден
            int inodeID = inode.getID();
            pair.inode.delete(inodeID);//в директории удаляем inode
            return inode;
        } catch(Exception ex) {
            return null;
        }
    }

    //удалить каталог
    //возвращает список inode которые удалили
    //произошла ошибка - вернется null
    public ArrayList<InodeFile> removeDirectory(String nameInode) {
        try {
            ArrayList<InodeFile> inodes = new ArrayList<InodeFile>();
            Pair pair = findDirectory(nameInode);//ищем директорию в которой нужно удалить директорию
            if (pair.name == null)
                throw new Exception("such directory doesn't exist");
            InodeDirectory inode = pair.inode.searchDirectory(pair.name);//ищем директорию в директории
            if (inode == null)
                throw new Exception("such directory doesn't exist");
            remove(inode, inodes);//удалить всех потомков
            int curInodeID = inode.getID();//взять id директории которую нужно удалить
            pair.inode.delete(curInodeID);//удалить из родительской директории
            return inodes;
        } catch(Exception ex) {
            return null;
        }
    }

    //рекурсивное удаление директории
    //пройти по иерархии и удалить все нижележащие inode
    //arraylist нужен чтобы запоминать inode файлов, чтобы удалить эти блоки с диска
    private void remove(InodeDirectory inode, ArrayList<InodeFile> inodes) {
        if (curInode.getID() == inode.getID())
            curInode = root;//если нужно удалить директорию в которой сейчас находимся
        for(int i = 0; i < inode.size(); i++){//пройти по всем потомкам
            if (inode.get(i).getType() == 1)
                remove((InodeDirectory) inode.get(i), inodes);//если потомок - директория, то вызвать директорию
            else
                inodes.add((InodeFile) inode.get(i));//если потомок файл - сохранить его inode
        }
        inode.removeAll();//удалить всех потомков
    }

    //найти файл
    //если inode найден возвращает его, иначе null
    public InodeFile find(String nameInode) {
        try {
            Pair pair = findDirectory(nameInode);//ищем директорию
            if (pair.name == null)
                throw new Exception("you can't find a file without a name");
            InodeFile inode = pair.inode.searchFile(pair.name);//ищем файл в директории
            if (inode == null)
                throw new Exception("such file doesn't exist");
            return inode;
        } catch(Exception ex) {
            return null;
        }
    }

    //перейти в директорию
    //ходим только по директориям
    //false если директория не найдена
    public boolean cd(String nameInode) {
        try {
            if (nameInode.equals(".."))//перенестись на один уровень выше или в директорию родителя
                nameInode = curInode.getLayout();// + '/';//то есть нужно перейти в каталог родителя в котором лежит подкаталог в котором мы сейчас находимся
            Pair pair = findDirectory(nameInode);//ищем родителя в котором лежит директория
            if (pair.name == null)
                curInode = pair.inode;//если не указали путь то остаемся в той же директории
            else {
                InodeDirectory inode = pair.inode.searchDirectory(pair.name);//ищем нужную директорию в каталоге родителя
                if (inode == null)
                    throw new Exception("such directory doesn't exist: ");
                curInode = inode;
            }
            return true;
        } catch(Exception ex) {
            return false;
        }
    }

    //посмотреть какие есть inode в текущем каталоге
    public String ls(String nameInode) {
        try {
            InodeDirectory inode;
            Pair pair = findDirectory(nameInode);//ищем родителя в котором лежит директории
            if (pair.name == null)
                inode = pair.inode;//не указали путь и остаемся в той же директории или указали корневой каталог и переходим к корню
            else {
                inode = pair.inode.searchDirectory(pair.name);//ищем в родительском каалоге нужную директорию
                if (inode == null)
                    throw new Exception("such directory doesn't exist: ".concat(nameInode));
            }
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < inode.size(); i++) {
                str.append(inode.get(i));
                str.append(" ");
            }
            return str.toString();
        } catch(Exception ex) {
            return ex.getMessage();
        }
    }

    public String pwd() {
        return curInode.getPath();
    }

    //найти директорию в которой располагается inode, то есть если папка res/src/q мы будем искать res/src !!!!
    //если такую директорию не нашли возвращает null, иначе возвращает пару Pair: inodeDirectory в котором хотим создать и имя Inode который ходим создать
    public Pair findDirectory(String nameInode) throws Exception {
        InodeDirectory inode = curInode;
        if (nameInode.startsWith("/".concat(ROOT)))//если файл начинается /ROOT, то обрезаем это
            nameInode = nameInode.substring(ROOT.length() + 1);
        if (nameInode.startsWith("/")) {//если путь начинается с /, то поиск начинаем с корня, те /... тоже самое что и /root/...
            nameInode = nameInode.substring(1);
            inode = root;
        }
        if (nameInode.equals(""))//если после манипуляций выше осталась пустая строка, те не ввели имя файла
            return new Pair(null, inode);
        //ищем нужную директорию
        String[] words = nameInode.split("/");
        for (int i = 0; i < words.length - 1; i++) {
            inode = inode.searchDirectory(words[i]);//ищем в родителе нужную директорию и переходим в нее
            if (inode == null)
                throw new Exception("no such directory: ".concat(nameInode));
        }
        //возвращаем имя файла или каталога на самом нижнем уровне
        //и директорию в которой должен лежать этот inode
        return new Pair(words[words.length - 1], inode);
    }
}