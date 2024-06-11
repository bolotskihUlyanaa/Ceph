package ulyana.MDS;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

//inode типа файл
public class InodeFile extends Inode implements Serializable {
    private int countBlock;//количество блоков
    private int size;//размер файла
    private String userBlock = null;//имя пользователя который заблокировал
    private Date date;

    public InodeFile(String nameInode, String path, int numberInode, int size, int countBlock, Date date) {
        super(nameInode, path, numberInode, 0);
        this.countBlock = countBlock;
        this.size = size;
        this.date = date;
    }

    public InodeFile(InodeFile inode){
        this(inode.getName(), inode.getLayout(), inode.getID(), inode.size, inode.countBlock, inode.date);
        //this.countBlock = inode.getCountBlock();
        //this.size = inode.size();
    }

    public int size() {
        return size;
    }

    public void setSize(int size, Date date) {
        this.size = size;
        this.date = date;
    }

    public void setCountBlock(int countBlock) {
        this.countBlock = countBlock;
    }

    public int getCountBlock() {
        return countBlock;
    }

    public Date getDate(){
        return date;
    }

    public String getMetadata(){
        StringBuilder str = new StringBuilder();
        str.append("name " + getPath() + '\n');
        str.append("number inode: " + getID() + '\n');
        str.append("size: " + size + '\n');
        str.append("block by: " + userBlock + '\n');
        str.append("date: " + date);
        return str.toString();
    }

    public void setBlock(String name, Date date) {
        userBlock = name;
        this.date = date;
    }

    //проверка имеет ли name разрешение на изменение
    public boolean access(String name){
        if (userBlock == null) return true;
        return userBlock.equals(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InodeFile inodeFile = (InodeFile) o;
        return countBlock == inodeFile.countBlock && size == inodeFile.size && getID() == inodeFile.getID()
                && getName().equals(inodeFile.getName()) && getLayout().equals(inodeFile.getLayout())
                && getType() == inodeFile.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(countBlock, size, userBlock);
    }

    public String getUserBlock(){
        return userBlock;
    }
}