package ulyana.Client;

import java.io.Serializable;

//объект долден быть сериализуемым чтобы сохранять в файл
public class Block implements Serializable {
    private static final long SerialVersionUID = 1998;//для корректной сериализации
    final private String ID;//состоит из inode number и номера блока
    private int begin;//номер символа с которого начинается блок
    private byte[] data;//данные которые хранятся в блоке

    public Block(String ID, int begin, byte[] data){
        this.ID = ID;
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
        return "{ID: ".concat(ID).concat(" ").concat("path: ").concat(" ").concat("begin: ") + begin + "}";
    }
}
