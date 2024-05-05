package ulyana.OSD;

//должен ли следить osd чтобы не было повторяющихся номеров блоков
public class Main {
    public static void main(String []args)  {
            OSDNetServer a = new OSDNetServer(Integer.parseInt(args[0]));
            a.run();
    }
}