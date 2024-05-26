package ulyana.Monitor;

import java.util.ArrayList;

//алгоритм вычисления расположения блока
//для этого нужны идентификатор объекта и карта кластера
public class Crush {
    private ArrayList<ArrayList<Bucket>> list;

    public Crush() {
        list = new ArrayList<>();
    }

    public ArrayList crush(String x, ArrayList<Bucket> map, int countOfReplicas) {
        list = new ArrayList<ArrayList<Bucket>>();
        list.add(map);
        /* //пример с многоуровневневой иерархией
        select(x, 2, "row");
        ArrayList<ArrayList<Bucket>> listNew = new ArrayList<>();
        for(int i = 0; i < list.get(0).size(); i++){
            listNew.add(list.get(0).get(i).getMap());
        }
        list = listNew;
         */
        select(x, countOfReplicas, "OSD");
        list.get(0).removeIf(Bucket::isFailure);
        return list.get(0);
    }

    //x - название блока, n - количество сегментов, t - тип сегмента
    public void select(String x, int n, String t) {
        ArrayList<Bucket> O = new ArrayList<Bucket>();
        for (ArrayList<Bucket> i:list) {
            for (int r = 1; r <= n; r++) {//найти n реплик
                int fr = 0;//количество "падений" в этой реплике
                Bucket o = null;
                ArrayList<Bucket> b = new ArrayList<>(i);
                boolean retryBucket = false;
                while (!retryBucket) {//для случая коллизии чтобы провести локальный поиск
                    //rNew для следующих случаев: 1)элемент уже выбран(коллизия) 2)сегмент перегружен 3) сегмент вышел из строя
                    //пока что есть только проверка на коллизию
                    int rNew = r + fr;// * n;//r + fr так как вторичная реплика стоновится первичной
                    o = c(b, rNew, x);//выбираем сегмент из b
                    if (O.contains(o) && o.getClass().getSimpleName().equals(t)) {//проверка на коллизию и на тип
                        fr++;
                    } else {
                        retryBucket = true;
                    }
                }
                O.add(o);
            }
        }
        list = new ArrayList<ArrayList<Bucket>>();
        list.add(O);
    }

    public Bucket c(ArrayList<Bucket> b, int r, String x) {
        int m = b.size();
        int p = getPrimeNumber(m);//случайно выбранное постоянное число больше m
        int number = ((x.hashCode() + r * p) % m);//функция для однородных сегментов
        return b.get(number);
    }

    //получить простое число больше n
    public int getPrimeNumber(int  n) {
        for (int i = n + 1;; i++) {
            if (isPrime(i))
                return i;
        }
    }

    //проверка на простоту
    public boolean isPrime(int n) {
        if (n == 1)
            return false;
        for (int i = 2; Math.pow(i, 2) <= n; i++) {
            if (n % i == 0)
                return false;
        }
        return true;
    }

    public ArrayList<ArrayList<Bucket>> get() {
        return list;
    }

    public void setMap(ArrayList<Bucket> map) {
        list.add(map);
    }
}