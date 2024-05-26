package ulyana.MDS;

import java.util.ArrayList;

//интерфейс для операций с метаданными
public interface MetaDataOperation {

    Object removeFile(String name, String nameInode) throws Exception;

    Object removeDirectory(String name, String nameInode) throws Exception;

    Object addInodeFile(String nameInode, int size, int countBlock) throws Exception;

    InodeFile find(String nameInode) throws Exception;

    Object addInodeDirectory(String nameInode) throws Exception;

    String ls(String nameInode) throws Exception;

    boolean cd(String nameInode) throws Exception;

    String pwd() throws Exception;

    Object update(String nameUser, String nameInode, int size, int countBlock) throws Exception;

    Object block(String nameUser, String nameInode) throws Exception;

    Object unblock(String nameUser, String nameInode) throws Exception;
}
