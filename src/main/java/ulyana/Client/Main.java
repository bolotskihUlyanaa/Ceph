package ulyana.Client;

import ulyana.Monitor.MONet;
import ulyana.OSD.*;
import ulyana.MDS.*;
import java.io.*;
import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {
        try {
            String name;
            if (args.length == 0) {
                name = "noname";
            } else {
                name = args[0];
            }
            Client client = new Client(name, new MDONet(InetAddress.getByName("localhost"), 9999), new DONet(), new MONet(InetAddress.getByName("localhost"), 8888));
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    System.out.print("%");
                    client.commandLine(reader.readLine().trim());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}