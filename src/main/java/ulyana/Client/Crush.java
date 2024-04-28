package ulyana.Client;

import java.util.ArrayList;

public class Crush {
    private ArrayList<ArrayList<Bucket>> list;

    public Crush(){
        list = new ArrayList<>();
    }

    //placement rules
    //алгоритм вычисления расположения объекта для этого нужны идентификатор объекта и карта кластера
    public ArrayList CRUSH(String x, ArrayList<Bucket> map){
        list = new ArrayList<ArrayList<Bucket>>();
        list.add(map);//поместить сегмент map в список list
        select(x, 1, "OSD");//если случай не с OSD, то надо брать ArrayList у каждого элемента
        return list.get(0);
    }

    public void select(String x, int n, String t){//t это тип
        ArrayList<Bucket> O = new ArrayList<Bucket>();
        for(ArrayList<Bucket> i:list) {
            for(int r = 1; r <= n; r++){//найти n реплик
                int fr = 0;//количество "падений" в этой реплике
                Bucket o = null;
                ArrayList b = new ArrayList<>(i);
                boolean retryBucket = false;
                while(!retryBucket){//для случая коллизии чтобы провести локальный поиск
                    //rNew для следующих случаев: 1)элемент уже выбран(коллизия) 2)сегмент перегружен 3) сегмент вышел из строя
                    int rNew = r + fr * n;
                    o = c(b, rNew, x);//выбираем сегмент из b
                    if (O.contains(o) && o.getClass().getSimpleName().equals(t)){//проверка на коллизию и на тип
                        fr++;
                    }
                    else {
                        retryBucket = true;
                    }
                }
               O.add(o);
            }
        }
        list = new ArrayList<ArrayList<Bucket>>();
        list.add(O);
    }

    public Bucket c(ArrayList<Bucket> b, int r, String x){
        int m = b.size();
        int p = getPrimeNumber(m);//случайно выбранное постоянное число больше m
        int number = ((x.hashCode() + r * p) % m);//функция для однородных сегментов
        return b.get(number);
    }

    //получить простое число больше n
    public int getPrimeNumber(int  n){
        for(int i = n + 1;; i++){
            if(isPrime(i)) return i;
        }
    }

    //проверка на простоту
    public boolean isPrime(int n){
        if(n == 1) return false;
        for(int i = 2; Math.pow(i, 2) <= n; i++){
            if (n % i == 0) return false;
        }
        return true;
    }

    public ArrayList<ArrayList<Bucket>> get() {
        return list;
    }

    public void setMap(ArrayList map){
        list.add(map);
    }
}