package ulyana.Client;

import ulyana.OSD.*;
import ulyana.MDS.*;
import java.io.*;
import java.util.ArrayList;

public class Client {
    final private MDS mds;
    final private Crush crush;
    final private ProcessBuilder processBuilder;
    final private ArrayList<Bucket> map;
    final int sizeBlock = 4096;//размер блока для разделения файла

    public Client() throws Exception {
        mds = new MDS();
        processBuilder = new ProcessBuilder();
        crush = new Crush();
        map = new ArrayList<Bucket>();
        File osdsDirectory = new File("res/OSDs");
        if(!osdsDirectory.isDirectory()) if(!osdsDirectory.mkdirs()) throw new Exception("can't create directories for OSDs");
        for(int i = 0; i < 10; i++){
            map.add(new OSD("192.168.0." + i, 8000 + i, new DiskStorage("res/OSDs/OSD" + i + ".txt")));
        }
    }

    public void commandLine(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            System.out.print("%");
            try{
                String input = reader.readLine().trim();
                String[] command = input.split(" ");
                switch (command[0]) {
                    case ("touch"):
                        for (int i = 1; i < command.length; i++) {
                            if (command[i].startsWith("/ceph/")) {
                                command[i] = command[i].substring(6);
                                mds.addInodeFile(command[i]);
                            } else {//команда компьютеру
                                processBuilder.command("touch", command[i]);
                                Process process = processBuilder.start();
                                process.waitFor();
                                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                                String line;
                                while ((line = br.readLine()) != null) {
                                    System.out.println(line);
                                }
                            }
                        }
                        break;
                    case ("mkdir"):
                        for (int i = 1; i < command.length; i++) {
                            if (command[i].startsWith("/ceph/")) {
                                command[i] = command[i].substring(6);
                                mds.addInodeDirectory(command[i]);
                            } else {//команда компьютеру
                                processBuilder.command("mkdir", command[i]);
                                Process process = processBuilder.start();
                                process.waitFor();
                                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                                String line;
                                while ((line = br.readLine()) != null) {
                                    System.out.println(line);
                                }
                            }
                        }
                        break;
                    case("ls"):
                        if (command.length == 1) {//команда компьютеру
                            processBuilder.command("ls");
                            Process process = processBuilder.start();
                            process.waitFor();
                            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            String line;
                            while ((line = br.readLine()) != null) {
                                System.out.println(line);
                            }
                            break;
                        }
                        for(int i = 1; i < command.length; i++){
                            if(command[i].startsWith("/ceph/")) {
                                command[i] = command[i].substring(6);
                                System.out.println(mds.ls(command[i]));
                            }
                            else{//команда компьютеру
                                processBuilder.command("ls", command[i]);
                                Process process = processBuilder.start();
                                process.waitFor();
                                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                                String line;
                                while ((line = br.readLine()) != null) {
                                    System.out.println(line);
                                }
                                br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                                while ((line = br.readLine()) != null) {
                                    System.out.println(line);
                                }
                            }
                        }
                        break;
                    case("cd"):
                        if (command.length != 2) throw new Exception("cd: string not in pwd");
                        if (command[1].startsWith("/ceph/")) {
                            command[1] = command[1].substring(6);
                            mds.cd(command[1]);
                        }
                        else{//команда компьютеру
                            File f = new File(command[1]);
                            if(f.isDirectory()){
                                processBuilder.directory(f);
                            }
                            else throw new Exception("cd: not such directory");
                        }
                        break;
                    //пока что копирование только в файл
                    case("cp")://файл-источник файл-назначение
                        if (command.length != 3) throw new Exception("incorrect number of arguments");
                        if (command[1].startsWith("/ceph/")) {
                            command[1] = command[1].substring(6);
                            if (command[2].startsWith("/ceph/")) {//из ceph в ceph
                                command[2] = command[2].substring(6);
                                copyInodeFS(command[1], command[2]);
                            }
                            //из ceph в local
                            else{
                                copyInodeFromFS(command[1], processBuilder.directory(), command[2]);
                            }
                        }
                        else{
                            //из local в ceph
                            if (command[2].startsWith("/ceph/")) {
                                command[2] = command[2].substring(6);
                                copyInodeToFS(processBuilder.directory(), command[1], command[2]);
                            }
                            else{//из local в local
                                processBuilder.command(command);
                                Process process = processBuilder.start();
                                process.waitFor();
                                BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                                String line;
                                while ((line = br.readLine()) != null) {
                                    System.out.println(line);
                                }
                            }
                        }
                        break;
                    case("pwd"):
                        if (command.length > 1) {
                            if (command[1].startsWith("/ceph/"))
                                System.out.println(mds.pwd());
                        }
                        else{
                            processBuilder.command("pwd");
                            Process process = processBuilder.start();
                            process.waitFor();
                            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            String line;
                            while ((line = br.readLine()) != null) {
                                System.out.println(line);
                            }
                        }
                        break;
                    //удаление файла в ceph
                    case("rm"):
                        for(int i = 1; i < command.length; i++) {
                            if (command[i].startsWith("/ceph/")) {
                                command[i] = command[i].substring(6);
                                removeFile(command[i]);//получаем номер inode чтобы удалить его из osd
                            }
                        }
                        break;
                    //удаление директории
                    case("rmdir"):
                        for(int i = 1; i < command.length; i++) {
                            if (command[i].startsWith("/ceph/")) {
                                command[i] = command[i].substring(6);
                                removeDirectory(command[i]);
                            }
                        }
                        break;
                    case("find"):
                        if (command[1].startsWith("/ceph/")) {
                            System.out.println(mds.find(command[1]));
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
    }

    //копирование из локального компьютера в ceph
    public void copyInodeToFS(File directory, String nameFileCopy, String nameInode) throws Exception {//откуда копируем и куда
        //открываем файл источник, проверяем существует ли такой файл
        File file = new File(directory, nameFileCopy);
        if (!file.exists()) throw new Exception("such file doesn't exist: ".concat(nameFileCopy));
        InodeFile inode = mds.addInodeFile(nameInode);
        FileInputStream fileBytes = new FileInputStream(file);
        int size = fileBytes.available();//узнаем размер файла
        int countBlock = (size / sizeBlock) + 1;//вычисляем количество блоков, которые понадобятся для сохранения файла
        int inodeID = inode.getID();//узнаем ID файла, тк имя блока состоит из номера inode и (сейчас - номера блока)
        int countRead = sizeBlock;//сколько байт считать в блок
        inode.setSize(size);
        inode.setCountBlock(countBlock);
        //делим файл на блоки
        for (int i = 0; i < countBlock; i++) {
            if (fileBytes.available() < countRead) {
                countRead = fileBytes.available();
            }
            byte[] data = new byte[countRead];
            fileBytes.read(data);
            Block block = new Block(inodeID + "." + i, inode.getPath(), i * sizeBlock, data);
            //добавить вычисление с помощью crush в какой компьютер положить
            ArrayList<OSD> osds = crush.CRUSH(inodeID + "." + i, map);
            for(OSD osd:osds) osd.put(block);
        }
        fileBytes.close();
    }

    //копирование файла из ceph в локальный компьютер
    public void copyInodeFromFS(String nameInode, File directory, String nameFileCopy) throws Exception {//откуда и куда копируем
        InodeFile inode = mds.find(nameInode);//возвращает null если файла не существует в ceph
        //открываем файл источник, проверяем существует ли уже файл с таким именем
        File file = new File(directory, nameFileCopy);
        if(file.exists()){
            if(file.isDirectory()) file = new File(file, inode.toString());
            else throw new Exception("file with that name already exists: ".concat(nameFileCopy));
        }
        int inodeID = inode.getID();//узнаем номер inode, который нужно скопировать
        FileOutputStream fileBytes = new FileOutputStream(file);
        for (int i = 0; i < inode.getCountBlock(); i++) {//собираем все блоки
            //вычислить на каком osd и взять оттуда блок
            ArrayList<OSD> osds = crush.CRUSH(inodeID + "." + i, map);
            fileBytes.write(osds.get(0).get(inodeID + "." + i).getData());
        }
        fileBytes.close();
    }

    //копирование файла внутри файловой системы
    public void copyInodeFS(String nameInodeSource, String nameInodeDestination) throws Exception {
        InodeFile inodeSource = mds.find(nameInodeSource);
        InodeFile inodeDestination = mds.addInodeFile(nameInodeDestination);
        inodeDestination.setSize(inodeSource.size());
        inodeDestination.setCountBlock(inodeSource.getCountBlock());
        int IDSource = inodeSource.getID();//узнаем номер inode, который нужно скопировать
        int IDDestination = inodeDestination.getID();//узнаем номер inode, в который нужно скопировать
        for(int i = 0; i < inodeSource.getCountBlock(); i++){//собираем все блоки
            //вычислить на каком osd блок и взять его
            ArrayList<OSD> osdsSource = crush.CRUSH(IDSource + "." + i, map);
            byte [] data = osdsSource.get(0).get(IDSource + "." + i).getData();
            Block block = new Block(IDDestination + "." + i, inodeDestination.getPath(), i * sizeBlock, data);
            //вычислить на какие osd положить блок
            ArrayList<OSD> osdsDestination = crush.CRUSH(IDDestination + "." + i, map);
            for(OSD osd:osdsDestination) osd.put(block);
        }
    }

    //удалить директорию
    public void removeDirectory(String nameInode) throws Exception {
        ArrayList<InodeFile> inodes = mds.removeDirectory(nameInode);
        for(InodeFile inode:inodes)//удаляем все файлы
            removeInode(inode);
    }

    //удалить файл
    public void removeFile(String nameInode) throws Exception{
        removeInode(mds.removeFile(nameInode));
    }

    //удалить inode
    public void removeInode(InodeFile inode) {
        for(int i = 0; i < inode.getCountBlock(); i++) {//удаляем все блоки
            //вычислить на каких osd с помощью crush
            ArrayList<OSD> osds = crush.CRUSH(inode.getID() + "." + i, map);
            //удалить на каждом osd
            for(OSD osd:osds) osd.remove(inode.getID() + "." + i);
        }
    }
}
