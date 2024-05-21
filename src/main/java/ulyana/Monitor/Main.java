package ulyana.Monitor;

public class Main {
    public static void main(String []args) {
        try {
            MonitorNet monitor = new MonitorNet();
            monitor.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
