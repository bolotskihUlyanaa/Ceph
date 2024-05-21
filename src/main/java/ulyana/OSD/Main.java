package ulyana.OSD;

public class Main {
    public static void main(String []args) {
        OSDNet a = new OSDNet(Integer.parseInt(args[0]));
        a.run();
    }
}