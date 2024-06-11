package ulyana.OSD;

import ulyana.Monitor.MONet;

import java.net.InetAddress;

public class Main {
    public static void main(String []args) {
        try {
            if (!(args.length > 0 && args.length < 3)) throw new Exception("need to specify the port or port and key");
            int port = Integer.parseInt(args[0]);
            OSDNet osd;
            if (args.length == 2 && args[1].equals("-sync"))
                osd = new OSDNet(port, new MONet(InetAddress.getByName("localhost"), 8888), new DONet());
            else
                osd = new OSDNet(port);
            osd.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}