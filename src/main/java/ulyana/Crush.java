import java.util.ArrayList;
import java.util.List;

public class Crush {
    private List<List> list;

    //placement rules
    //алгоритм вычисления расположения объекта для этого нужны идентификатор объекта и карта кластера
    //*было бы неплохо где-то хранить placement rules и брать порядок действий из него
    public List CRUSH(String x, ArrayList map){
        list = new ArrayList<List>();
        list.add(map);//поместить сегмент map в список list
        select(x, 6);
        return list;
    }

    private void select(String x, int n){
        ArrayList<Object> O = new ArrayList<Object>();
        for(List i:list) {
            for(int r = 1; r <= n; r++){//найти n реплик
                int fr = 0;//количество "падений" в этой реплике
                Object o = null;
                boolean retry_descent = false;
                while(!retry_descent){//если устройство вышло из строя или перегружено и нужно перераспределить элементы по кластеру хранения
                    ArrayList b = new ArrayList<>(i);
                    boolean retry_bucket = false;
                    while(!retry_bucket){//для случая коллизии чтобы провести локальный поиск
                        //rNew для следующих случаев: 1)элемент уже выбран(коллизия) 2)сегмент перегружен 3) сегмент вышел из строя
                        int rNew = r + fr * n;
                        o = c(b, rNew, x);//выбираем сегмент из b
                        //в данном случае сюда никогда не попадем
                        if (!(o instanceof String)){//если не тот тип сегманта который мы ищем
                            //b = new ArrayList<>(o);
                            retry_bucket = true;//кажется что тут ошибка
                        }
                        else{
                            if (O.contains(o)){
                                fr++;
                            }
                            if (O.contains(o) && fr < 3){
                                retry_bucket = true;
                            }
                            else{
                                retry_bucket = true;//в псевдокоде этого нет но мне кажется что нужно
                                retry_descent = true;
                            }
                        }
                    }
                }
               O.add(o);
            }
        }
        list = new ArrayList<List>();
        list.add(O);
    }

    private Object c(ArrayList b, int r, String x){
        int m = b.size();
        int p = getPrimeNumber(m);//случайно выбранное постоянное число больше m
        int number = ((x.hashCode() + r * p) % m);//функция для однородных сегментов
        return b.get(number);
    }

    //получить простое число больше n
    private int getPrimeNumber(int  n){
        for(int i = n + 1;; i++){
            if(isPrime(i)) return i;
        }
    }

    //проверка на простоту
    private boolean isPrime(int n){
        for(int i = 2; Math.pow(i, 2) <= n; i++){
            if (n % i == 0) return false;
        }
        return true;
    }
}