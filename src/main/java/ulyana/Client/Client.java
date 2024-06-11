package ulyana.Client;

import ulyana.Monitor.*;
import ulyana.MDS.*;
import ulyana.OSD.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

//для работы с CephFS надо всегда добавлять приставку /ceph/
public class Client {
    final private MetaDataOperation mds;//взаимодействие с mds
    final private DataOperation osd;//взаимодействие с osd
    final private MonitorOperation monitor;//взаимодействие с монитором
    final private ProcessBuilder processBuilder;//для выполнения команд в локальном компьютере
    final int sizeBlock = 4096;//размер блока для разделения файла
    private String name;

    public Client(MetaDataOperation mds, DataOperation osd, MonitorOperation monitor) {
        this.mds = mds;
        this.osd = osd;
        this.monitor = monitor;
        processBuilder = new ProcessBuilder();
        this.name = "noname";
    }

    public Client(String name, MetaDataOperation mds, DataOperation osd, MonitorOperation monitor) {
        this.mds = mds;
        this.osd = osd;
        this.monitor = monitor;
        processBuilder = new ProcessBuilder();
        this.name = name;
    }


    public void commandLine(String input) {
        try {
            String[] command = input.split(" ");
            switch (command[0]) {
                case ("touch"):
                    for (int i = 1; i < command.length; i++) {
                        if (command[i].startsWith("/ceph/")) {
                            command[i] = command[i].substring(6);
                            try {
                                if (command[i].equals(""))
                                    throw new RuntimeException("you can't create file without name");
                                Object result = mds.addInodeFile(command[i], 0, 0, new Date());
                                if (result instanceof String) //если вернулась строка то значит там сообщение об ошибке
                                    throw new RuntimeException((String) result);
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                        } else {
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
                                if (result instanceof String) //если вернулась строка то значит там сообщение об ошибке
                                    throw new RuntimeException((String) result);
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                        } else {
                            processBuilder.command("mkdir", command[i]);
                            processInput();
                        }
                    }
                    break;
                case ("ls"):
                    if (command.length == 1) {
                        processBuilder.command("ls");
                        processInput();
                        break;
                    }
                    for (int i = 1; i < command.length; i++) {
                        if (command[i].startsWith("/ceph/")) {
                            command[i] = command[i].substring(6);
                            try {
                                String result = mds.ls(command[i]);
                                System.out.println(result);//выведется или список inode или сообщение об ошибке
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                        } else {
                            processBuilder.command("ls", command[i]);
                            processInput();
                        }
                    }
                    break;
                case ("cd"):
                    if (command.length != 2)
                        throw new RuntimeException("cd: string not in pwd");
                    if (command[1].startsWith("/ceph/")) {
                        command[1] = command[1].substring(6);
                        if (!mds.cd(command[1]))
                            throw new RuntimeException("such directory doesn't exist");
                    } else {
                        File f = new File(command[1]);
                        if (f.isDirectory()) {
                            processBuilder.directory(f);
                        } else
                            throw new RuntimeException("cd: not such directory");
                    }
                    break;
                case ("cp"):
                    if (command.length == 4 && command[2].startsWith("/ceph/") && command[3].equals("--force")) {
                        command[2] = command[2].substring(6);
                        if (command[1].startsWith("/ceph/")) {
                            command[1] = command[1].substring(6);
                            copyInodeFS(command[1], command[2]);
                        } else {
                            copyInodeToFS(processBuilder.directory(), command[1], command[2], true);
                        }
                        break;
                    }
                    if (command.length != 3)
                        throw new RuntimeException("incorrect number of arguments");
                    if (command[1].startsWith("/ceph/")) {
                        command[1] = command[1].substring(6);
                        if (command[2].startsWith("/ceph/")) {//копирование из ceph в ceph
                            command[2] = command[2].substring(6);
                            copyInodeFS(command[1], command[2]);
                        } else
                            copyInodeFromFS(command[1], processBuilder.directory(), command[2]);//копирование из ceph в local
                    } else {
                        if (command[2].startsWith("/ceph/")) {
                            command[2] = command[2].substring(6);
                            copyInodeToFS(processBuilder.directory(), command[1], command[2], false);//копирование из local в ceph
                        } else {
                            processBuilder.command(command);//копирование из local в local
                            processInput();
                        }
                    }
                    break;
                case ("pwd"):
                    if (command.length == 2 && command[1].equals("/ceph/"))
                        System.out.println(mds.pwd());
                    if (command.length == 1) {
                        processBuilder.command("pwd");
                        processInput();
                    }
                    break;
                case ("rm"):
                    if (command[1].equals("-r")) {
                        for (int i = 2; i < command.length; i++) {
                            try {
                                if (command[i].startsWith("/ceph/")) {
                                    command[i] = command[i].substring(6);
                                    if (command[i].equals(""))
                                        throw new RuntimeException("you can't remove directory without name");
                                    removeDirectory(command[i]);
                                } else
                                    throw new RuntimeException("such directory doesn't exist: " + command[i]);
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                    }
                    else {
                        for (int i = 1; i < command.length; i++) {
                            try {
                                if (command[i].startsWith("/ceph/")) {
                                    command[i] = command[i].substring(6);
                                    if (command[i].equals(""))
                                        throw new RuntimeException("you can't remove file without name");
                                    removeFile(command[i]);
                                } else
                                    throw new RuntimeException("such directory doesn't exist: " + command[i]);
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                    }
                    break;
                case ("find"):
                    if (command[1].startsWith("/ceph/")) {
                        command[1] = command[1].substring(6);
                        InodeFile inode = mds.find(command[1]);
                        if (inode == null)
                            throw new RuntimeException("such file doesn't exist");
                        System.out.println(inode.getMetadata());
                    }
                    break;
                case("block"):
                    for (int i = 1; i < command.length; i++) {
                        if (command[i].startsWith("/ceph/")) {
                            command[i] = command[i].substring(6);
                            try {
                                if (command[i].equals(""))
                                    throw new RuntimeException("you can't block file without name");
                                Object result = mds.block(name, command[i], new Date());
                                if (result instanceof String) //если вернулась строка то значит там сообщение об ошибке
                                    throw new RuntimeException((String) result);
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                    }
                    break;
                case("unblock"):
                    for (int i = 1; i < command.length; i++) {
                        if (command[i].startsWith("/ceph/")) {
                            command[i] = command[i].substring(6);
                            try {
                                if (command[i].equals(""))
                                    throw new RuntimeException("you can't unblock file without name");
                                Object result = mds.unblock(name, command[i], new Date());
                                if (result instanceof String) //если вернулась строка то значит там сообщение об ошибке
                                    throw new RuntimeException((String) result);
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                    }
                    break;
                default:
                    System.out.println("command not found: ".concat(input));
                    break;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //копирование из локального компьютера в ceph
    //передается текущая директория, имя файла и имя файла куда сохранить
    public void copyInodeToFS(File directory, String nameFileCopy, String nameInode, boolean force) throws Exception {
        Date date = new Date();
        File file = new File(directory, nameFileCopy);
        if (!file.exists())
            throw new RuntimeException("such file doesn't exist: ".concat(nameFileCopy));
        FileInputStream fileBytes = new FileInputStream(file);
        int size = fileBytes.available();
        int countBlock = (size / sizeBlock) + 1;
        Object result = null;
        InodeFile searchInode = mds.find(nameInode);
        if (searchInode != null) {//если такой файл уже есть
            if (force) {
                Object resultUpdate = mds.update(name, nameInode, size, countBlock, date);
                if (resultUpdate instanceof String) throw new RuntimeException((String) resultUpdate);
                removeInode(searchInode);
                result = searchInode.getID();
            }
        }
        else {//если такого файла нет то создаем
            result = mds.addInodeFile(nameInode, size, countBlock, date);
        }
        if (result instanceof Integer) {//если вернулось число - это ID inode который создался в файловой системе
            int inodeID = (int) result;
            int countRead = sizeBlock;//сколько байт считать в блок
            CalculateOSD calculateOSD = new CalculateOSD(monitor);
            for (int i = 0; i < countBlock; i++) { //делим файл на блоки
                if (fileBytes.available() < countRead)
                    countRead = fileBytes.available();
                byte[] data = new byte[countRead];
                fileBytes.read(data);
                String idBlock = inodeID + "." + i;
                ArrayList<DiskBucket> osds = calculateOSD.getOSDs(idBlock);
                Block block = new Block(idBlock, i * sizeBlock, data, date);
                for (DiskBucket disk : osds) {
                    osd.put(disk, block);
                    //if (!osd.put(disk, block))
                    //    throw new Exception("save file failure");
                }
            }
            fileBytes.close();
        }
        //когда возвращается строка - в ней записана причин почему не удалось добавить файл в файловую систему
        else {
            fileBytes.close();
            if (result != null) throw new Exception((String)result);
        }
    }

    //копирование файла из ceph в локальный компьютер
    //название файла который копируем, текущая директория и имя inode назначения
    public void copyInodeFromFS(String nameInode, File directory, String nameFileCopy) throws Exception {
        InodeFile inode = mds.find(nameInode);
        if (inode != null) {
            File file = new File(directory, nameFileCopy);
            if (file.exists()) {
                if (file.isDirectory())//если передали директорию для сохранения, то имя файла будет такое же как в ceph
                    file = new File(file, inode.toString());
                else
                    throw new RuntimeException("file with that name already exists: ".concat(nameFileCopy));//если такой файл уже существует
            }
            int inodeID = inode.getID();//узнаем номер inode, который нужно скопировать
            FileOutputStream fileBytes = new FileOutputStream(file);
            CalculateOSD calculateOSD =  new CalculateOSD(monitor);
            for (int i = 0; i < inode.getCountBlock(); i++) {//собираем все блоки
                String idBlock = inodeID + "." + i;
                ArrayList<DiskBucket> osds = calculateOSD.getOSDs(idBlock);
                Block block = null;
                for(DiskBucket disk:osds) {
                    block = osd.get(disk, idBlock);
                    if (block != null) break;
                }
                if (block == null)
                    throw new Exception("get file failure");
                fileBytes.write(block.getData());
            }
            fileBytes.close();
        }
        else throw new RuntimeException("such file or directory doesn't exist");
    }

    //копирование файла внутри файловой системы ceph
    public void copyInodeFS(String nameInodeSource, String nameInodeDestination) throws Exception{
        Date date = new Date();
        InodeFile inodeSource = mds.find(nameInodeSource);
        if (inodeSource != null) {
            Object result = mds.addInodeFile(nameInodeDestination, inodeSource.size(), inodeSource.getCountBlock(), date);
            if (result instanceof Integer) {//возвращает int это ID inode который создался
                int idDestination = (int) result;
                int idSource = inodeSource.getID();//узнаем номер inode, копия которого будет создана
                CalculateOSD calculateOSD =  new CalculateOSD(monitor);
                for (int i = 0; i < inodeSource.getCountBlock(); i++) {//собираем все блоки
                    String idBlockSource = idSource + "." + i;
                    ArrayList<DiskBucket> osdsSource = calculateOSD.getOSDs(idBlockSource);
                    Block blockSource = null;
                    for(DiskBucket disk:osdsSource) {
                        blockSource = osd.get(disk, idBlockSource);
                        if (blockSource != null) break;
                    }
                    if (blockSource == null)
                        throw new Exception("get file failure");
                    byte[] data = blockSource.getData();
                    String idBlockDestination = idDestination + "." + i;
                    ArrayList<DiskBucket> osdsDestination = calculateOSD.getOSDs(idBlockDestination);
                    Block block = new Block(idBlockDestination, i * sizeBlock, data, date);
                    for (DiskBucket disk : osdsDestination) {
                        osd.put(disk, block);
                        //if (!osd.put(disk, block))
                        //    throw new Exception("save file failure");
                    }
                }
            }
            else
                throw new Exception((String)result);
        }
        else
            throw new RuntimeException("such file or directory doesn't exist");
    }

    //удалить директорию на MDS, а затем удалить на OSD все блоки inode которые принадлежали файлам в этой директории
    public void removeDirectory(String nameInode) throws Exception {
        Object result = mds.removeDirectory(name, nameInode);//получаем список inodeFile который надо удалить из osd
        if (result instanceof String) throw new Exception((String) result);
        for (InodeFile inode : (ArrayList<InodeFile>) result)//удаляем все файлы
            removeInode(inode);
    }

    //удалить файл в mds, а затем на osds удалить блоки которые принадлежали этому файлу
    public void removeFile(String nameInode) throws Exception{
        Object result = mds.removeFile(name, nameInode);// получаем InodeFile который нужно удалить с osd
        if (result instanceof String) throw new Exception((String) result);
        removeInode((InodeFile)result);
    }

    //удалить inode на OSDs
    public void removeInode(InodeFile inode) throws Exception {
        CalculateOSD calculateOSD =  new CalculateOSD(monitor);
        for (int i = 0; i < inode.getCountBlock(); i++) {//удаляем все блоки
            String idBlock = inode.getID() + "." + i;
            ArrayList<DiskBucket> osds = calculateOSD.getOSDs(idBlock);
            for (DiskBucket disk : osds) {
                osd.remove(disk, idBlock);
                //if (!osd.remove(disk, idBlock))
                    //throw new Exception("remove inode failure");
            }
        }
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