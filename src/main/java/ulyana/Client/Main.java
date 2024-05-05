package ulyana.Client;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) {
        Client client = null;
        try {
            client = new Client(new MDONet(InetAddress.getByName("localhost"), 9999), new DONet());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                System.out.print("%");
                client.commandLine(reader.readLine().trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}