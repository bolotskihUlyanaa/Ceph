package ulyana.OSD;

import java.io.Serializable;
import java.util.Date;

public class Block implements Serializable {
    final private String ID;//состоит из inode number и номера блока
    private int begin;//номер байта с которого начинается блок
    private byte[] data;//данные которые хранятся в блоке
    private Date date;

    public Block(String ID, int begin, byte[] data, Date date) {
        this.ID = ID;
        this.begin = begin;
        this.data = data;
        this.date = date;
    }

    public Date getDate(){
        return date;
    }

    public String getName() {
        return ID;
    }

    public byte[] getData() {
        return data;
    }

    public String toString() {
        return "{ID: ".concat(ID).concat(" ").concat("path: ").concat(" ").concat("begin: ") + begin
                + " size" + data.length + " date: " + date + " }";
    }
}
