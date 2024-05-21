package ulyana.Monitor;

import java.io.Serializable;
import java.util.ArrayList;

//класс разных типов сегментов (row, cabinet)
public class Bucket implements Serializable {
        final private String type;//тип сегмента
        final private String name;//имя конкретного сегмента
        final private ArrayList<Bucket> nodes;//ссылки на потомков

        public Bucket(String type, String name) {
                this.type = type;
                this.name = name;
                nodes = new ArrayList<Bucket>();
        }

        //добавить потомка
        public void add(Bucket bucket) {
                try {
                        if (nodes.isEmpty()) nodes.add(bucket);
                        else {
                                //на одном уровне должен быть одинаковый тип всех сегментов
                                if (!bucket.getType().equals(nodes.get(0).getType()))
                                        throw new Exception("must be the same type at the same level");
                                else {
                                        nodes.add(bucket);
                                }
                        }
                } catch(Exception ex) {
                        System.out.println(ex.getMessage());
                }
        }

        public String getType() {
                return type;
        }

        public String getName() {
                return name;
        }

        public ArrayList<Bucket> getMap() {
                return nodes;
        }

        public Bucket find(String bucketName) {
                for(Bucket i:nodes){
                        if(i.getName().equals(bucketName)) return i;
                }
                return null;
        }

        public String toString() {
                return name;
        }
}
