package ulyana.OSD;

public class Main {
    public static void main(String []args) {
        try {
            if (args.length != 1) throw new RuntimeException("only need to specify the port");
            int port = Integer.parseInt(args[0]);
            OSDNet a = new OSDNet(port);
            a.run();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}