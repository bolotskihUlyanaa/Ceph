package ulyana.Client;

import ulyana.MDS.InodeFile;
import java.util.ArrayList;

//интерфейс для операций с метаданными
public interface MetaDataOperation {

    InodeFile removeFile(String nameInode)throws Exception;

    ArrayList<InodeFile> removeDirectory(String nameInode)throws Exception;

    Object addInodeFile(String nameInode, int size, int countBlock)throws Exception;

    InodeFile find(String nameInode)throws Exception;

    Object addInodeDirectory(String nameInode)throws Exception;

    String ls(String nameInode)throws Exception;

    boolean cd(String nameInode)throws Exception;

    String pwd()throws Exception;
}
