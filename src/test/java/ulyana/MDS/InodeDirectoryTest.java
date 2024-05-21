package ulyana.MDS;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class InodeDirectoryTest {

    //тест для методом addInode, searchDirectory, searchFile, delete, removeAll
    @Test
    public void init(){
        InodeDirectory root = new InodeDirectory("root", "", 1);
        InodeDirectory users = new InodeDirectory("users", "/root", 2);
        InodeDirectory application = new InodeDirectory("application", "/root", 3);
        InodeFile file = new InodeFile("file.txt", "/root", 4, 1000, 1);
        //проверка addInode
        root.addInode(users);
        root.addInode(application);
        root.addInode(file);

        ArrayList<Inode> expected = new ArrayList<>();
        expected.add(users);
        expected.add(application);
        expected.add(file);
        assertEquals(expected, root.getNodes());

        //проверка searchDirectory
        assertEquals(users, root.searchDirectory("users"));
        assertNull(root.searchDirectory("noname"));//поиск несуществующей директории

        //проверка searchFile
        assertEquals(file, root.searchFile("file.txt"));
        assertNull(root.searchFile("noname"));//поиск несуществующего файла

        //проверка removeAll
        root.removeAll();
        assertEquals(0, root.getNodes().size());

        //проверка delete
        root.addInode(users);
        root.delete(1);//удаление несуществующего файла
        root.delete(2);
        assertEquals(0, root.getNodes().size());
    }
}
