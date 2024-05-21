package ulyana.MDS;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class MDSTest {
    private MDS mds;

    @BeforeEach
    public void init(){
        mds = new MDS();
    }

    @Test
    public void addInodeFile(){//проверка на добавление файла
        //проверка на добавление файла без имени
        assertEquals("you can't create file without name", mds.addInodeFile("", 1000, 1));
        //проверка что при добавление первого inode id = 2
        assertEquals(2, mds.addInodeFile("t", 1000, 1));
        //проверка на добавление файла с таким же именем
        assertEquals("such file already exist: t", mds.addInodeFile("t", 1000, 1));
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
        mds.addInodeFile("t", 1000, 1);
        assertEquals(2, mds.removeFile("t").getID());//проверяем по id возвращаемого inode
        assertNull(mds.removeFile("t"));//проверка удаления несуществующего файла
        assertNull(mds.find("t"));//проверка что при поиске этот файл не найдется
    }

    @Test
    public void removeDirectory(){//проверка на удаление директории по имени
        //удаление директории без потомков
        mds.addInodeDirectory("res");
        ArrayList<InodeFile> res = mds.removeDirectory("res");
        assertEquals(0, res.size());//проверяем размер вернувшегося массива удаленных inodeFile
        assertNull(mds.removeDirectory("res"));//проверка удаления несуществующего каталога
        assertEquals("", mds.ls("/"));
    }

    @Test
    public void removeDirectoryWithSon(){//удаление директории с потомками
        mds.addInodeDirectory("res");
        mds.addInodeDirectory("res/src");
        mds.addInodeDirectory("res/src/tar");
        mds.addInodeFile("res/src/tar/test", 1000, 1);
        mds.addInodeFile("res/src/tar.txt", 1000, 1);
        ArrayList<InodeFile> res = mds.removeDirectory("res");
        assertEquals(2, res.size());//проверяем количество удаленных inodeFile внутри директории
        assertEquals(5, res.get(0).getID());//проверяем удаленные inodeFile по ID
        assertEquals(6, res.get(1).getID());
    }

    @Test
    public void find(){//проверка поиска файлов
        mds.addInodeFile("tar.txt", 1000, 1);//не многоуровневая файловая система
        mds.addInodeDirectory("res");
        mds.addInodeFile("res/src.txt", 1000, 1);
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
}
