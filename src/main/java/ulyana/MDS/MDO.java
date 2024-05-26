package ulyana.MDS;

import java.util.ArrayList;

//операции взаимодействия с mds напрямую без сокетов
public class MDO implements MetaDataOperation {
    final private MDS mds;

    public MDO() {
        mds = new MDS();
    }

    public Object removeFile(String name, String nameInode) {
        return mds.removeFile(name, nameInode);
    }

    public Object removeDirectory(String name, String nameInode) {
        return(mds.removeDirectory(name, nameInode));
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

    public Object update(String nameUser, String nameInode, int size, int countBlock) {
        return mds.updateFile(nameUser, nameInode, size, countBlock);
    }

    public Object block(String nameUser, String nameInode) {
        return mds.blockFile(nameUser, nameInode);
    }

    public Object unblock(String nameUser, String nameInode) {
        return mds.unblockFile(nameUser, nameInode);
    }
}
