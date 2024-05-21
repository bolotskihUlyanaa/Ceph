package ulyana.OSD;

import ulyana.Monitor.DiskBucket;
import java.net.InetAddress;
import java.util.ArrayList;

//операции к osd напрямую, без сокетов
public class DO implements DataOperation {
    final private ArrayList<OSD> OSDs;

    public DO() throws Exception {
        OSDs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {//создаем osd без сериализации
            OSDs.add(new OSD(InetAddress.getLocalHost(), 11000 + i, new MemoryStorage()));
            //если хотим создать с сериализацией new DiskStorage("res/OSDs/" + i + ".txt")
        }
    }

    //сохранить блок
    public boolean put(DiskBucket disk, Block block){
        OSD osd = find(disk.getIP(), disk.getPort());//найти osd по ip и порту
        if (osd != null)//если нашли нужный OSD
            return osd.put(block);//обращаемся к osd для сохранения блока
        return false;//если нужный osd не нашли
    }

    //взять блок
    public Block get(DiskBucket disk, String blockID){
        OSD osd = find(disk.getIP(), disk.getPort());//найти osd по ip и порту
        if(osd == null)
            return null;//если нашли нужный OSD
        return osd.get(blockID);//если блок не найден метод вернет null, иначе вернет блок
    }

    //удалить блок по имени
    public boolean remove(DiskBucket disk, String blockID){
        OSD osd = find(disk.getIP(), disk.getPort());//найти osd по ip и порту
        if (osd != null)//если нашли нужный OSD
            return osd.remove(blockID);//обращаемся к osd для удаления блока
        else return false;//если нужный osd не нашли
    }

    //ищем в списке osd нужный нам osd по ip и порту, если такого osd нет, то возвращает null
    private OSD find(InetAddress ip, int port){
        for(OSD osd: OSDs){//проходим по всем osd в кластере
            if (osd.getIP().equals(ip) && osd.getPort() == port)
                return osd;//сравниваем по ip и порту
        }
        return null;
    }
}
