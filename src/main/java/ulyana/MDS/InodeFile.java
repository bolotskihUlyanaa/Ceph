package ulyana.MDS;

import java.io.Serializable;

//inode типа файл
public class InodeFile extends Inode implements Serializable {
    private int countBlock;//количество блоков
    private int size;//размер файла
    private String userBlock = null;//имя пользователя который заблокировал

    public InodeFile(String nameInode, String path, int numberInode, int size, int countBlock) {
        super(nameInode, path, numberInode, 0);
        this.countBlock = countBlock;
        this.size = size;
    }

    public InodeFile(InodeFile inode){
        super(inode.toString(), inode.getPath(), inode.getID(), 0);
        this.countBlock = inode.getCountBlock();
        this.size = inode.size();
    }

    public int size() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setCountBlock(int countBlock) {
        this.countBlock = countBlock;
    }

    public int getCountBlock() {
        return countBlock;
    }

    public String getMetadata(){
        StringBuilder str = new StringBuilder();
        str.append("name " + getPath() + '\n');
        str.append("number inode: " + getID() + '\n');
        str.append("size: " + size + '\n');
        str.append("block by: " + userBlock);
        return str.toString();
    }

    public void setBlock(String name) {
        userBlock = name;
    }

    //проверка имеет ли name разрешение на изменение
    public boolean access(String name){
        if (userBlock == null) return true;
        return userBlock.equals(name);
    }

    public String getUserBlock(){
        return userBlock;
    }
}