package ulyana;

public class ComponentThread extends Thread {
    Component obj;

    public ComponentThread(Component obj) {
        this.obj = obj;
    }

    public void run() {
        obj.run();
    }
}
