package ulyana;

public class OSD extends Object {
    private String IP;
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
        StringBuilder data = new StringBuilder();
        data.append("{IP: " + IP + "\n");
        data.append(block + "}\n");
        return data.toString();
    }
}
