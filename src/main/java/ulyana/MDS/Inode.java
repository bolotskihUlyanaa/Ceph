package ulyana.MDS;

import java.io.Serializable;
import java.util.Objects;

public class Inode implements Serializable {
    //пока что только неизменяемые поля
    private int number;//номер inode
    private String name;//имя
    private String layout;//расположение или путь до папки в которой inode лежит
    private int type;//тип 0 - файл, 1 - директория

    public Inode(String name, String layout, int number, int type) {
        this.number = number;
        this.name = name;
        this.layout = layout;
        this.type = type;
    }

    public String toString() {
        return name;
    }

    public String getPath() {
        return layout.concat("/").concat(name);
    }

    public String getLayout() {
        return layout;
    }

    public int getType() {
        return type;
    }

    public int getID() {
        return number;
    }

    public String getName(){
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inode inode = (Inode) o;
        return number == inode.number && type == inode.type && name.equals(inode.name) && layout.equals(inode.layout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, name, layout, type);
    }
}