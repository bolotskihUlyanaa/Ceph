package ulyana;

import java.util.*;

public class Ceph {
    private ArrayList map;
    Crush crush;

    public Ceph(ArrayList map){//передаем карту кластера
        this.map = map;
        crush = new Crush();
    }

    public void put(Block block){
        List<OSD> osds = crush.CRUSH(block.getName(), map);
        for(OSD o:osds) o.put(block);
    }

    public Block get(Block block){
        List<OSD> osds = crush.CRUSH(block.getName(), map);
        return osds.get(0).get();
    }
}
