public class Block {
    private String ID;//нужно переделать в тот тип, чтобы название характеризовалось номером inode и номером строки
    private String path;
    private int begin;
    private int end;

    public Block(String ID, String path, int begin, int end){
        this.ID = ID;
        this.path = path;
        this.begin = begin;
        this.end = end;
    }

    public String getName(){
        return ID;
    }
}
