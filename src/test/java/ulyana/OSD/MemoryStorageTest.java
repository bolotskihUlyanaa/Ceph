package ulyana.OSD;

import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

public class MemoryStorageTest {
    @Test
    public void init(){//проверка методов save и remove
        //создаем хранилище из 3х блоков
        MemoryStorage storage = new MemoryStorage();
        Block block1 = new Block("111", 0, new byte[100]);
        Block block2 = new Block("222", 0, new byte[100]);
        Block block3 = new Block("333", 0, new byte[100]);
        storage.save(block1);
        storage.save(block2);
        storage.save(block3);
        //проверяем поиск по ID
        assertEquals(block1, storage.get("111"));
        assertNull(storage.get("444"));//так как такого блока не существует

        //удаляем блок
        assertTrue(storage.remove("111"));
        assertNull(storage.get("111"));

        //удаление несуществующего блока
        assertFalse(storage.remove("111"));
    }
}
