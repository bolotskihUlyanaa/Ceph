package ulyana.MDS;

public class Main {
    public static void main(String[] args) {
        try {
            MDSNet mds = new MDSNet(9999);
            mds.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}