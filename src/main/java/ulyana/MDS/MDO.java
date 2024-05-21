package ulyana.MDS;

import java.util.ArrayList;

//операции взаимодействия с mds напрямую без сокетов
public class MDO implements MetaDataOperation {
    final private MDS mds;

    public MDO() {
        mds = new MDS();
    }

    public InodeFile removeFile(String nameInode) {
        return mds.removeFile(nameInode);
    }

    public ArrayList<InodeFile> removeDirectory(String nameInode) {
        return(mds.removeDirectory(nameInode));
    }

    public Object addInodeFile(String nameInode, int size, int countBlock) {
        return mds.addInodeFile(nameInode, size, countBlock);
    }

    //найти по имени файла файл в файловой системе
    public InodeFile find(String nameInode) {
        return mds.find(nameInode);//возвращает null если файла не существует в ceph
    }

    public Object addInodeDirectory(String nameInode) {
        return mds.addInodeDirectory(nameInode);
    }

    public String ls(String nameInode) {
        return mds.ls(nameInode);
    }

    public boolean cd(String nameInode) {
        return mds.cd(nameInode);
    }

    public String pwd() {
        return mds.pwd();
    }
}
