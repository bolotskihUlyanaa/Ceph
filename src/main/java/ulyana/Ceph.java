import java.util.*;

public class Ceph {
    private ArrayList map;
    Crush crush;

    public Ceph(ArrayList map){//передаем карту кластера
        this.map = map;
        crush = new Crush();
    }

    public void put(Block block){
        System.out.println(crush.CRUSH(block.getName(), map));
    }

    public List get(Block block){
        return crush.CRUSH(block.getName(), map);
    }
}
