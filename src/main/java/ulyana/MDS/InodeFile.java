package ulyana.MDS;

import java.io.Serializable;

//inode типа файл
public class InodeFile extends Inode implements Serializable {
    private int countBlock;//количество блоков

    public InodeFile(String nameInode, String path, int numberInode, int size, int countBlock) {
        name = nameInode;
        layout = path;
        number = numberInode;
        this.size = size;
        this.countBlock = countBlock;
        type = 0;
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
}