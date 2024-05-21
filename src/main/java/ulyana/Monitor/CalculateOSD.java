package ulyana.Monitor;

import java.util.ArrayList;

//чтобы вычислить на каких osd нужно сохранить блок
public class CalculateOSD {
    private ArrayList<Bucket> map;
    private int countOfReplica;
    private int pgNum;
    private Crush crush;

    public CalculateOSD(MonitorOperation monitor) throws Exception {
        map = monitor.getMap();//получить у монитора карту
        if (map == null)
            throw new Exception("couldn't get a map");
        countOfReplica = monitor.getCountOfReplica();//получить у монитора коэффициент репликации
        if (countOfReplica == 0)
            throw new Exception("couldn't get a countOfReplica");
        pgNum = monitor.getPGNum();//получить у монитора количество плейстмент групп
        if (pgNum == 0)
            throw new Exception("couldn't get a pgNum");
        crush = new Crush();
    }

    public ArrayList<DiskBucket> getOSDs(String idBlock) {
        int pg = idBlock.hashCode() % pgNum;
        return crush.crush(Integer.toString(pg), map, countOfReplica);//вычисление в какой компьютер сохранить
    }
}
