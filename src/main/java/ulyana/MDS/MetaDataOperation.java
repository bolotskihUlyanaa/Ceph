package ulyana.MDS;

import java.util.ArrayList;
import java.util.Date;

//интерфейс для операций с метаданными
public interface MetaDataOperation {

    Object removeFile(String name, String nameInode) throws Exception;

    Object removeDirectory(String name, String nameInode) throws Exception;

    Object addInodeFile(String nameInode, int size, int countBlock, Date date) throws Exception;

    InodeFile find(String nameInode) throws Exception;

    Object addInodeDirectory(String nameInode) throws Exception;

    String ls(String nameInode) throws Exception;

    boolean cd(String nameInode) throws Exception;

    String pwd() throws Exception;

    Object update(String nameUser, String nameInode, int size, int countBlock, Date date) throws Exception;

    Object block(String nameUser, String nameInode, Date date) throws Exception;

    Object unblock(String nameUser, String nameInode, Date date) throws Exception;
}
