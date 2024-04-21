package ulyana;

public class OSD {
    final private String IP;
    private Block block;
    private boolean overload;//флаг не перегружено ли устройство
    private boolean failed;//флаг работает ли устройство

    public OSD(String IP){
        this.IP = IP;
        overload = false;
        failed = false;
    }

    public void put(Block block){
        this.block = block;
    }

    public Block get(){
        return block;
    }

    public boolean isFailed() {
        return failed;
    }

    public boolean isOverload() {
        return overload;
    }

    public String toString(){
        return "{IP: ".concat(IP).concat("\n") + block + "}\n";
    }
}
