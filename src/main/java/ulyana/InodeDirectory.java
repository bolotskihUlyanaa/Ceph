package ulyana;

import java.util.ArrayList;

public class InodeDirectory extends Inode{
    private ArrayList<Inode> nodes;//ссылки на нижние узлы

    public InodeDirectory(String nameInode, String path, int numberInode) {
        name = nameInode;
        layout = path;
        number = numberInode;
        nodes = new ArrayList<Inode>();
        type = 1;
    }

    public void addInode(Inode inode){
        nodes.add(inode);
    }

    public int size(){
        return nodes.size();
    }

    public Inode get(int i){return nodes.get(i);}

    public InodeDirectory searchDirectory(String nameDirectory){
        for(Inode i:nodes) if (i.toString().equals(nameDirectory) && i.getType() == 1) return (InodeDirectory)i;
        return null;
    }

    public InodeFile searchFile(String nameDirectory){
        for(Inode i:nodes) if (i.toString().equals(nameDirectory) && i.getType() == 0) return (InodeFile)i;
        return null;
    }
}
