package ulyana;

public abstract class Inode{
    //пока что только неизменяемые поля
    protected int number;//номер inode
    protected int size;//размер файла
    protected String name;//имя
    protected String layout;//расположение или путь до папки в которой она лежит
    protected int type;//тип 0 - файл, 1 - директория

    public String toString(){
        return name;
    }

    public String getPath(){
        return layout.concat("/").concat(name);
    }

    public String getLayout(){
        return layout;
    }

    public abstract int size();

    public int getType(){
        return type;
    }

    public int getID(){
        return number;
    }
}