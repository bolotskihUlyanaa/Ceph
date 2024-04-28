package ulyana.Client;

import java.io.Serializable;

//объект долден быть сериализуемым чтобы сохранять в файл
public class Block implements Serializable {
    private static final long SerialVersionUID = 1998;
    final private String ID;//состоит из inode number и номера блока
    final private String path;
    //нужны номера строк или номера символов
    private int begin;//номер символа с которого начинается
    private byte[] data;

    public Block(String ID, String path, int begin, byte[] data){
        this.ID = ID;
        this.path = path;
        this.begin = begin;
        this.data = data;
    }

    public String getName(){
        return ID;
    }

    public byte[] getData(){
        return data;
    }

    public String toString(){
        return "{ID: ".concat(ID).concat(" ").concat("path: ").concat(path).concat(" ").concat("begin: ") + begin + "}";
    }
}
