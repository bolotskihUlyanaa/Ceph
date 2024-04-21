package ulyana;

public class InodeFile extends Inode{
    private int countBlock;//количество блоков

    public InodeFile(String nameInode, String path, int numberInode, int size, int countBlock) {
        name = nameInode;
        layout = path;
        number = numberInode;
        this.size = size;
        this.countBlock = countBlock;
        type = 0;
    }

    //создать пустой файл
    public InodeFile(String nameInode, String path, int numberInode){
        name = nameInode;
        layout = path;
        number = numberInode;
        size = 0;
        countBlock = 0;
        type = 0;
    }

    public int size(){return size;}

    public void setSize(int size){this.size = size;}

    public void setCountBlock(int countBlock){
        this.countBlock = countBlock;
    }

    public int getCountBlock(){
        return countBlock;
    }
}