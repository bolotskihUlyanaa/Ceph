package ulyana.Client;

import ulyana.MDS.InodeFile;
import ulyana.MDS.MDS;
import java.util.ArrayList;

//операции взаимодействия с mds напрямую без сокетов
public class MDO implements MetaDataOperation{
    final private MDS mds;

    public MDO(){
        mds = new MDS();
    }

    //удалить файл, возвращает номер inode удаленного файла
    public InodeFile removeFile(String nameInode){
        return mds.removeFile(nameInode);
    }

    //удалить директорию, возвращает номера inode удаленных файлов
    public ArrayList<InodeFile> removeDirectory(String nameInode){
        return(mds.removeDirectory(nameInode));
    }

    //добавить файл в файловую систему
    //возвращает номер Inode при успешном добавлении файла в файловую систему
    //если произошла ошибка,то возвращается строка с произошедшей ошибкой
    public Object addInodeFile(String nameInode, int size, int countBlock){
        return mds.addInodeFile(nameInode, size, countBlock);
    }

    //найти по имени файла файл в файловой системе (словарный запас отдыхает)
    public InodeFile find(String nameInode){
        return mds.find(nameInode);//возвращает null если файла не существует в ceph
    }

    //добавить директорию в файловую систему
    //возвращает строку "success" при успешном добавлении директории в файловую систему
    //если произошла ошибка,то возвращается строка с произошедшей ошибкой
    public Object addInodeDirectory(String nameInode){
        return mds.addInodeDirectory(nameInode);
    }

    //вернет либо результат команды ls, либо строку с ошибкой
    public String ls(String nameInode){
        return mds.ls(nameInode);
    }

    //true - если удалось перейти в директорию
    //false - если произошла ошибка
    public boolean cd(String nameInode){
        return mds.cd(nameInode);
    }

    public String pwd(){
        return mds.pwd();
    }
}
