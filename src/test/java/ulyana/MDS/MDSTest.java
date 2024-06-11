package ulyana.MDS;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class MDSTest {
    private MDS mds;

    @BeforeEach
    public void init(){
        mds = new MDS();
    }

    @Test
    public void addInodeFile(){//проверка на добавление файла
        //проверка на добавление файла без имени
        assertEquals("you can't create file without name", mds.addInodeFile("", 1000, 1, new Date()));
        //проверка что при добавление первого inode id = 2
        assertEquals(2, mds.addInodeFile("t", 1000, 1, new Date()));
        //проверка на добавление файла с таким же именем
        assertEquals("such file already exist: t", mds.addInodeFile("t", 1000, 1, new Date()));
    }

    @Test
    public void addInodeDirectory(){//проверка на добавление директории
        //проверка на добавление директории без имени
        assertEquals("you can't create directory without name", mds.addInodeDirectory(""));
        //проверка успешного добавления
        assertEquals(2, mds.addInodeDirectory("res"));
        //проверка на добавление директории с таким же именем
        assertEquals("such directory already exist: res", mds.addInodeDirectory("res"));
    }

    @Test
    public void removeFile(){//проверка на удаление файла по имени
        mds.addInodeFile("t", 1000, 1, new Date());
        InodeFile inode = (InodeFile) mds.removeFile("noname", "t");
        assertEquals(2, inode.getID());//проверяем по id возвращаемого inode
        assertEquals("such file doesn't exist", mds.removeFile("noname", "t"));//проверка удаления несуществующего файла
        assertNull(mds.find("t"));//проверка что при поиске этот файл не найдется
    }

    @Test
    public void removeFileBlock(){//удаление заблокированного файла
        mds.addInodeFile("t", 1000, 1, new Date());
        mds.blockFile("ulyana", "t", new Date());
        assertEquals("this file block by ulyana", mds.removeFile("noname", "t"));
        InodeFile inode = (InodeFile) mds.removeFile("ulyana", "t");
        assertEquals(2, inode.getID());
        assertNull(mds.find("t"));
    }

    @Test
    public void removeDirectory(){//проверка на удаление директории по имени
        //удаление директории без потомков
        mds.addInodeDirectory("res");
        ArrayList<InodeFile> res = (ArrayList<InodeFile>) mds.removeDirectory("noname", "res");
        assertEquals(0, res.size());//проверяем размер вернувшегося массива удаленных inodeFile
        assertEquals("such directory doesn't exist", mds.removeDirectory("noname", "res"));//проверка удаления несуществующего каталога
        assertEquals("", mds.ls("/"));
    }

    @Test
    public void removeDirBlock(){//проверка на удаление директории в которой заблокированных файл
        mds.addInodeDirectory("tar");
        mds.addInodeDirectory("tar/res");
        mds.addInodeFile("tar/res/t", 1000, 1, new Date());
        mds.blockFile("ulyana", "tar/res/t", new Date());
        assertEquals("blocked files cannot be deleted", mds.removeDirectory("noname", "tar"));
        ArrayList<InodeFile> res = (ArrayList<InodeFile>) mds.removeDirectory("ulyana", "tar");
        assertEquals(1, res.size());//проверяем размер вернувшегося массива удаленных inodeFile
        assertEquals("", mds.ls("/"));
    }

    @Test
    public void removeDirectoryWithSon(){//удаление директории с потомками
        mds.addInodeDirectory("res");
        mds.addInodeDirectory("res/src");
        mds.addInodeDirectory("res/src/tar");
        mds.addInodeFile("res/src/tar/test", 1000, 1, new Date());
        mds.addInodeFile("res/src/tar.txt", 1000, 1, new Date());
        ArrayList<InodeFile> res = (ArrayList<InodeFile>) mds.removeDirectory("noname", "res");
        assertEquals(2, res.size());//проверяем количество удаленных inodeFile внутри директории
        assertEquals(5, res.get(0).getID());//проверяем удаленные inodeFile по ID
        assertEquals(6, res.get(1).getID());
    }

    @Test
    public void find(){//проверка поиска файлов
        mds.addInodeFile("tar.txt", 1000, 1, new Date());//не многоуровневая файловая система
        mds.addInodeDirectory("res");
        mds.addInodeFile("res/src.txt", 1000, 1, new Date());
        assertEquals(2, mds.find("tar.txt").getID());//проверка по id
        assertEquals(4, mds.find("res/src.txt").getID());//проверка по id
        assertNull(mds.find("res/src/qwe.txt"));//поиск несуществующего файла
    }


    @Test
    public void cd(){//проверка перехода в другую директорию
        mds.addInodeDirectory("res");
        mds.addInodeDirectory("res/src");
        mds.addInodeDirectory("res/src/tar");
        assertTrue(mds.cd("res/src"));//проверка что переход в директорию будет успешный
        assertEquals("/ceph/res/src", mds.pwd());
        assertFalse(mds.cd("qwe"));//проверка что не перейдет в несуществующую директорию
        assertTrue(mds.cd(".."));//проверка что переход на директорию выше будет успешный
        assertEquals("/ceph/res", mds.pwd());
        assertTrue(mds.cd("/"));//проверка что переход в корневой каталог будет успешный
        assertEquals("/ceph", mds.pwd());
    }

    @Test
    public void findDirectory(){//найти директорию в который расположен inode
        mds.addInodeDirectory("res");
        mds.addInodeDirectory("res/src");
        try {
            mds.findDirectory(null);
        } catch (Exception e) {
            assertEquals("no such directory: ", e.getMessage());
        }
        try {
            mds.findDirectory("res/asd/q");//проверка поиска несуществующей директории
        } catch (Exception e) {
            assertEquals("no such directory: res/asd/q", e.getMessage());
        }
        try {
            MDS.Pair pair = mds.findDirectory("res/src");
            assertEquals("src res", pair.toString());
            mds.cd("res/src");
            pair = mds.findDirectory("/res/src");//проверка поиска с корневого каталога
            assertEquals("src res", pair.toString());
            pair = mds.findDirectory("/ceph/res/src");//проверка поиска с корневого каталога
            assertEquals("src res", pair.toString());
            pair = mds.findDirectory("");//проверка если не введем имя
            assertEquals("null src", pair.toString());
        } catch (Exception e) {
            assertEquals(1, 0);//если попали в обработчик исключений то значит тест не прошли
        }
    }

    @Test
    public void ls(){
        mds.addInodeDirectory("res");
        mds.addInodeDirectory("res/src");
        assertEquals("res ", mds.ls(""));
        assertEquals("src ", mds.ls("res"));
        assertEquals("res ", mds.ls("/ceph/"));
        assertEquals("res ", mds.ls("/ceph"));
        assertEquals("res ", mds.ls("/"));
    }

    @Test
    public void blockFile(){
        mds.addInodeFile("txt", 0, 0, new Date());
        assertEquals("users without name can't block file", mds.blockFile("noname", "txt", new Date()));
        assertEquals("users without name can't block file", mds.blockFile(null, "txt", new Date()));
        assertEquals("such file doesn't exist", mds.blockFile("ulyana", "t", new Date()));
        assertEquals(true, mds.blockFile("ulyana", "txt", new Date()));
        assertEquals("this file already block by ulyana", mds.blockFile("ksusha", "txt", new Date()));
        InodeFile i = mds.find("txt");
        assertEquals("ulyana", i.getUserBlock());
    }

    @Test
    public void unblockFile(){
        mds.addInodeFile("txt", 0, 0, new Date());
        assertEquals(true , mds.unblockFile("noname", "txt", new Date()));
        assertEquals(true , mds.unblockFile(null, "txt", new Date()));
        mds.blockFile("ulyana", "txt", new Date());
        assertEquals("this file block by ulyana" , mds.unblockFile("noname", "txt", new Date()));
        assertEquals("this file block by ulyana" , mds.unblockFile(null, "txt", new Date()));
        assertEquals("such file doesn't exist", mds.unblockFile("ulyana", "t", new Date()));
        assertEquals(true, mds.unblockFile("ulyana", "txt", new Date()));
        InodeFile i = mds.find("txt");
        assertEquals(null, i.getUserBlock());
    }

    @Test
    public void updateFile(){
        mds.addInodeFile("txt", 0, 0, new Date());
        assertEquals(true, mds.updateFile("noname", "txt", 0, 0, new Date()));
        assertEquals(true, mds.updateFile(null, "txt", 0, 0, new Date()));
        mds.blockFile("ulyana", "txt", new Date());
        assertEquals(true, mds.updateFile("ulyana", "txt", 4096, 1, new Date()));
        InodeFile i = mds.find("txt");
        assertEquals(4096, i.size());
        assertEquals(1, i.getCountBlock());
        assertEquals("you don't have access to this file", mds.updateFile("noname", "txt", 0, 0, new Date()));
        assertEquals("you don't have access to this file", mds.updateFile(null, "txt", 0, 0, new Date()));
        assertEquals("such file doesn't exist", mds.updateFile("ulyana", "t", 0, 0, new Date()));
    }
}
