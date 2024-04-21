package ulyana;

import java.io.*;

public class Client {
    final private MDS fileSystem;
    final private ProcessBuilder processBuilder;

    public Client(){
        fileSystem = new MDS();
        processBuilder = new ProcessBuilder();
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
                                fileSystem.addInodeFile(command[i]);
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
                                fileSystem.addInodeDirectory(command[i]);
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
                                System.out.println(fileSystem.ls(command[i]));
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
                            fileSystem.cd(command[1]);
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
                                fileSystem.copyInodeFS(command[1], command[2]);
                            }
                            else{//из ceph в local
                                fileSystem.copyInodeFromFS(command[1], processBuilder.directory(), command[2]);
                            }
                        }
                        else{
                            if (command[2].startsWith("/ceph/")) {//из local в ceph
                                command[2] = command[2].substring(6);
                                fileSystem.copyInodeToFS(processBuilder.directory(), command[1], command[2]);
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
                                System.out.println(fileSystem.pwd());
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
}
