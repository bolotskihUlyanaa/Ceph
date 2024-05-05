package ulyana.Client;

import ulyana.Monitor.*;
import ulyana.MDS.*;
import java.io.*;
import java.util.ArrayList;


//для работы с cephfs надо всегда добавлять приставку /ceph/
public class Client {
    final private MetaDataOperation mds;//взаимодействие с mds
    final private DataOperation osd;//взаимодействие с osd
    final private Monitor mon;//карта кластера
    final private Crush crush;
    final private ProcessBuilder processBuilder;//для выполнения команд в локальном компьютере
    final int sizeBlock = 4096;//размер блока для разделения файла

    public Client(MetaDataOperation mds, DataOperation osd) {
            this.mds = mds;
            this.osd = osd;
            mon = new Monitor();
            processBuilder = new ProcessBuilder();
            crush = new Crush();
    }

    public void commandLine(String input){
            try{
                String[] command = input.split(" ");
                switch (command[0]) {
                    case ("touch"):
                        for (int i = 1; i < command.length; i++) {
                            if (command[i].startsWith("/ceph/")) {
                                command[i] = command[i].substring(6);
                                try {
                                    if (command[i].equals(""))
                                        throw new RuntimeException("you can't create file without name");
                                    Object result = mds.addInodeFile(command[i], 0, 0);
                                    if (result instanceof String) {//если вернулась строка то значит там сообщение об ошибке
                                        throw new RuntimeException((String) result);
                                    }
                                }catch (Exception ex){
                                    System.out.println(ex.getMessage());
                                }
                            } else {//команда компьютеру
                                processBuilder.command("touch", command[i]);
                                processInput();
                            }
                        }
                        break;
                    case ("mkdir"):
                        for (int i = 1; i < command.length; i++) {
                            if (command[i].startsWith("/ceph/")) {
                                command[i] = command[i].substring(6);
                                try {
                                    if (command[i].equals(""))
                                        throw new RuntimeException("you can't create directory without name");
                                    Object result = mds.addInodeDirectory(command[i]);
                                    if (result instanceof String) {//если вернулась строка то значит там сообщение об ошибке
                                        throw new RuntimeException((String) result);
                                    }
                                }catch (Exception ex) {
                                    System.out.println(ex.getMessage());
                                }
                            } else {//команда компьютеру
                                processBuilder.command("mkdir", command[i]);
                                processInput();
                            }
                        }
                        break;
                    case("ls"):
                        if (command.length == 1) {//команда компьютеру
                            processBuilder.command("ls");
                            processInput();
                            break;
                        }
                        for(int i = 1; i < command.length; i++){
                            if(command[i].startsWith("/ceph/")) {
                                command[i] = command[i].substring(6);
                                try {
                                    String result = mds.ls(command[i]);
                                    System.out.println(result);//выведется или список inode или сообщение об ошибке
                                }catch (Exception ex) {
                                    System.out.println(ex.getMessage());
                                }
                            }
                            else{//команда компьютеру
                                processBuilder.command("ls", command[i]);
                                processInput();
                            }
                        }
                        break;
                    case("cd"):
                        if (command.length != 2) throw new RuntimeException("cd: string not in pwd");
                        if (command[1].startsWith("/ceph/")) {
                            command[1] = command[1].substring(6);
                            if (!mds.cd(command[1])) throw new RuntimeException("such directory doesn't exist");
                        }
                        else{//команда компьютеру
                            File f = new File(command[1]);
                            if(f.isDirectory()){
                                processBuilder.directory(f);
                            }
                            else throw new RuntimeException("cd: not such directory");
                        }
                        break;
                    case("cp")://файл-источник файл-назначение
                        if (command.length != 3) throw new RuntimeException("incorrect number of arguments");
                        if (command[1].startsWith("/ceph/")) {
                            command[1] = command[1].substring(6);
                            if (command[2].startsWith("/ceph/")) {//копирование из ceph в ceph
                                command[2] = command[2].substring(6);
                                copyInodeFS(command[1], command[2]);
                            }
                            //копирование из ceph в local
                            else{
                                copyInodeFromFS(command[1], processBuilder.directory(), command[2]);
                            }
                        }
                        else{
                            //копирование из local в ceph
                            if (command[2].startsWith("/ceph/")) {
                                command[2] = command[2].substring(6);
                                copyInodeToFS(processBuilder.directory(), command[1], command[2]);
                            }
                            else{//копирование из local в local
                                processBuilder.command(command);
                                processInput();
                            }
                        }
                        break;
                    case("pwd"):
                        if (command.length == 2 && command[1].startsWith("/ceph/")) {
                            System.out.println(mds.pwd());
                        }
                        if (command.length == 1) {
                            processBuilder.command("pwd");
                            processInput();
                        }
                        break;
                    //удаление файла в ceph
                    case("rm"):
                        //удаление директории в ceph
                        if(command[1].equals("-r")){
                            for(int i = 2; i < command.length; i++) {
                                try {
                                    if (command[i].startsWith("/ceph/")) {
                                        command[i] = command[i].substring(6);
                                        if (command[i].equals(""))
                                            throw new Exception("you can't remove directory without name");
                                        removeDirectory(command[i]);
                                    } else {
                                        throw new Exception("such directory doesn't exist: " + command[i]);
                                    }
                                }catch (Exception ex){
                                    System.out.println(ex.getMessage());
                                }
                            }
                        }
                        //удаление файла в ceph
                        else {
                            for (int i = 1; i < command.length; i++) {
                                try {
                                    if (command[i].startsWith("/ceph/")) {
                                        command[i] = command[i].substring(6);
                                        if (command[i].equals(""))
                                            throw new Exception("you can't remove file without name");
                                        removeFile(command[i]);//получаем номер inode чтобы удалить его из osd
                                    } else {
                                        throw new Exception("such directory doesn't exist: " + command[i]);
                                    }
                                }catch (Exception ex){
                                    System.out.println(ex.getMessage());
                                }
                            }
                        }
                        break;
                    case("find")://найти файл
                        if (command[1].startsWith("/ceph/")) {
                            InodeFile inode = mds.find(command[1]);
                            if(inode == null) throw new RuntimeException("such file doesn't exist");
                            System.out.println(inode);
                        }
                        break;
                    default:
                        System.out.println("command not found: ".concat(input));
                        break;
                }
            }
            catch(Exception ex){
                System.out.println(ex.getMessage());
            }
    }

    //копирование из локального компьютера в ceph
    //передается текущая директория, имя файла и имя файла куда сохранить
    public void copyInodeToFS(File directory, String nameFileCopy, String nameInode) throws Exception {//откуда копируем и куда
        //открываем файл источник, проверяем существует ли такой файл
        File file = new File(directory, nameFileCopy);
        if (!file.exists()) throw new RuntimeException("such file doesn't exist: ".concat(nameFileCopy));
        FileInputStream fileBytes = new FileInputStream(file);
        int size = fileBytes.available();//узнаем размер файла
        int countBlock = (size / sizeBlock) + 1;//вычисляем количество блоков, которые понадобятся для сохранения файла
        Object result = mds.addInodeFile(nameInode, size, countBlock);//узнаем ID файла, тк имя блока состоит из номера inode и номера блока
        if(result instanceof Integer) {//если вернулось число - это ID файла который создался в файловой системе
            int inodeID =(int) result;
            int countRead = sizeBlock;//сколько байт считать в блок
            //делим файл на блоки
            for (int i = 0; i < countBlock; i++) {
                if (fileBytes.available() < countRead) {//если количество байт меньше размера блока, то считываем только сколько доступно
                    countRead = fileBytes.available();
                }
                byte[] data = new byte[countRead];
                fileBytes.read(data);

                Block block = new Block(inodeID + "." + i, i * sizeBlock, data);
                ArrayList<Bucket> map = mon.getClusterMap();//получаем карту кластера хранения
                if (map != null) {
                    ArrayList<DiskBucket> osds = crush.CRUSH(inodeID + "." + i, map);//вычисление в какой компьютер сохранить
                    //отправлять на osd
                    if (!osd.put(osds.get(0), block)) {//мы сохраняем только в первичный OSD, по остальным раскидывает сам OSD
                        throw new Exception("save file failure");
                    }
                }
                //в дальнейшем когда карта будет тоже присылаться по сокетам,
                //могут возникнуть ситуации когда карта не доставится
                else throw new Exception("couldn't get a map");
            }
            fileBytes.close();
        }
        //когда возвращается строка - в ней записана причин почему не удалось добавить файл в файловую систему
        else{
            fileBytes.close();
            throw new Exception((String)result);
        }
    }

    //копирование файла из ceph в локальный компьютер
    //название файла который копируем, текущая директория и имя inode назначения
    public void copyInodeFromFS(String nameInode, File directory, String nameFileCopy) throws Exception {//откуда и куда копируем
        InodeFile inode = mds.find(nameInode);//возвращает null если файла не существует в ceph
        if(inode != null) {
            //открываем файл источник, проверяем существует ли уже файл с таким именем
            File file = new File(directory, nameFileCopy);
            if (file.exists()) {
                //если передали директорию для сохранения, то имя файла будет такое же как в ceph
                if (file.isDirectory()) file = new File(file, inode.toString());
                else throw new RuntimeException("file with that name already exists: ".concat(nameFileCopy));//если такой файл уже существует
            }
            int inodeID = inode.getID();//узнаем номер inode, который нужно скопировать
            FileOutputStream fileBytes = new FileOutputStream(file);
            for (int i = 0; i < inode.getCountBlock(); i++) {//собираем все блоки
                ArrayList<Bucket> map = mon.getClusterMap();//получаем карту кластера хранения
                if (map != null) {
                    ArrayList<DiskBucket> osds = crush.CRUSH(inodeID + "." + i, map);//вычислить на каких osd лежит блок
                    Block block = osd.get(osds.get(0), inodeID + "." + i);
                    if(block == null) throw new Exception("get file failure");
                    fileBytes.write(block.getData());
                }
                //в дальнейшем когда карта будет тоже присылаться по сокетам,
                //могут возникнуть ситуации когда карта не доставится
                else throw new Exception("couldn't get a map");
            }
            fileBytes.close();
        }
        else throw new Exception("such file or directory doesn't exist");
    }

    //копирование файла внутри файловой системы
    //имя копируемого файла и название файла который будет копией
    public void copyInodeFS(String nameInodeSource, String nameInodeDestination) throws Exception{
        InodeFile inodeSource = mds.find(nameInodeSource);//ищем inode-источник
        if(inodeSource != null) {//если inode-источник существует
            Object result = mds.addInodeFile(nameInodeDestination, inodeSource.size(), inodeSource.getCountBlock());
            if(result instanceof Integer) {//возвращает int это ID блока который создался
                //узнаем номер inode, в который нужно скопировать
                int IDDestination = (int)result;
                int IDSource = inodeSource.getID();//узнаем номер inode, копия которого будет создана
                ArrayList<Bucket> map = mon.getClusterMap();//получаем карту кластера хранения
                if (map != null) {
                    for (int i = 0; i < inodeSource.getCountBlock(); i++) {//собираем все блоки
                        ArrayList<DiskBucket> osdsSource = crush.CRUSH(IDSource + "." + i, map);//вычислить на каком osd блок
                        Block blockSource = osd.get(osdsSource.get(0), IDSource + "." + i);//обратиться к конкретному osd для получения блока
                        if(blockSource == null) throw new Exception("get file failure");
                        byte[] data = blockSource.getData();
                        Block block = new Block(IDDestination + "." + i, i * sizeBlock, data);
                        ArrayList<DiskBucket> osdsDestination = crush.CRUSH(IDDestination + "." + i, map);//вычислить на какие osd сохранить блок
                        if (!osd.put(osdsDestination.get(0), block)) {//мы сохраняем только в первичный OSD, по остальным раскидывает сам OSD
                            throw new Exception("save file failure");
                        }
                    }
                }
                //в дальнейшем когда карта будет тоже присылаться по сокетам,
                //могут возникнуть ситуации когда карта не доставится
                else throw new Exception("couldn't get a map");
            }
            else throw new Exception((String)result);
        }
        else throw new Exception("such file or directory doesn't exist");
    }

    //удалить директорию на MDS, а затем на OSD все блоки inode которые принадлежали файлам в этой директории
    public void removeDirectory(String nameInode) throws Exception {
        ArrayList<InodeFile> inodes = mds.removeDirectory(nameInode);//получаем список inodeFile который надо удалить из osd
        if (inodes != null){
            for (InodeFile inode : inodes)//удаляем все файлы
                removeInode(inode);
        }
        //если удалить директорию не удалось
        else throw new Exception("such directory doesn't exist");
    }

    //удалить файл в mds, а затем на OSD блоки которые принадлежали этому файлу
    public void removeFile(String nameInode) throws Exception{
        InodeFile inode = mds.removeFile(nameInode);// получаем InodeFile который нужно удалить с osd
        if(inode != null){
            removeInode(inode);
        }
        //если удаление inode в файловой системе закончилось неудачей
        else throw new Exception("such file doesn't exist");
    }

    //удалить inode на OSD
    public void removeInode(InodeFile inode) throws Exception {
        ArrayList<Bucket> map = mon.getClusterMap();//получаем карту кластера хранения
        if (map != null) {
            for (int i = 0; i < inode.getCountBlock(); i++) {//удаляем все блоки
                ArrayList<DiskBucket> osds = crush.CRUSH(inode.getID() + "." + i, map);//вычислить на каких osd лежит блок
                //удалить на главном OSD
                if (!osd.remove(osds.get(0), inode.getID() + "." + i)) {//если не получилось удалить
                    throw new Exception("remove inode failure");
                }
            }
        }
        //в дальнейшем когда карта будет тоже присылаться по сокетам,
        //могут возникнуть ситуации когда карта не доставится
        else throw new Exception("couldn't get a map");
    }

    //чтобы вывести результаты вызова процесса компьютера, а конкретнее поток ввода и поток ошибок
    public void processInput() throws Exception {
        Process process = processBuilder.start();
        process.waitFor();
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        BufferedReader inError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((line = inError.readLine()) != null) {
            System.out.println(line);
        }
    }
}