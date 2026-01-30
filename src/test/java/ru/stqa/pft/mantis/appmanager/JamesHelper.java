package ru.stqa.pft.mantis.appmanager;

import org.apache.commons.net.telnet.TelnetClient;

import javax.mail.Session;
import javax.mail.Store;
import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class JamesHelper {

    private ApplicationManager app;


    private TelnetClient telnetClient;
    private InputStream in;
    private PrintStream out;


    private Session mailSession;
    private Store store;
    private String mailServer;

    public JamesHelper(ApplicationManager app) {
        this.app = app;
        telnetClient = new TelnetClient();
        mailSession = Session.getDefaultInstance(System.getProperties());
    }


    public boolean doesUserExists(String name) throws IOException {
        initTelnetSession();
        write("verify" + name);
        String result = readUntil("exists");
        closeTelnetSession();
        return result.trim().equals("User " + name + " exist");
    }



    public void createUser(String name, String passwd) throws IOException {
        initTelnetSession();
        write("adduser " + name + " " + passwd);
        System.out.println(readUntil("User " + name + " added"));
        closeTelnetSession();
    }



    public void deleteUser(String name) throws IOException {
        initTelnetSession();
        write("deluser " + name);
        String result = readUntil("User " + name + " deleted");
        closeTelnetSession();
    }

    public List<String> listUsers() throws IOException {
        initTelnetSession();
        write("listusers");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String line;
        List<String> users = new ArrayList<>();
        int size = Integer.parseInt(bufferedReader.readLine().replaceAll("Existing accounts ", ""));
        for(int i=0; i<size; i++){
            line = bufferedReader.readLine();
            System.out.println("line: " + line);
            if(line.startsWith("Existing accounts")){
                System.out.println(line); // количество пользователей
            }

            if(line.startsWith("user: ")){
                String user = line.replaceFirst("user: ", "");
                users.add(user);
            }

            if(line.trim().isEmpty()){break;}
        }
        closeTelnetSession();
        return users;
    }


    public int counterUsers() throws IOException {
        return listUsers().size();
    }


    public boolean verifyUser(String name) throws IOException {
        initTelnetSession();
        write("verify " + name);
        if(containsPattern("User "+name+" exists")){
            System.out.println("User "+name+" exists");
            closeTelnetSession();
            return true;
        } else {
            closeTelnetSession();
            return false;
        }
    }

    private void initTelnetSession() throws IOException {
        mailServer = app.getProperty("mailserver.host");
        int port = Integer.parseInt(app.getProperty("mailserver.port"));
        String login = app.getProperty("mailserver.adminLogin");
        String password = app.getProperty("mailserver.adminPassword");

        try{
            telnetClient.connect(mailServer, port);
            in = telnetClient.getInputStream();
            out = new PrintStream(telnetClient.getOutputStream());
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        System.out.println(readUntil("Login id:"));
        write(login);
        System.out.println(readUntil("Password"));
        write(password);


        System.out.println(readUntil("Welcome " + login + ". HELP for a list of commands"));

    }


    private String readUntil(String pattern) throws IOException {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
        String line;
        StringBuilder sb = new StringBuilder();
        long start = System.currentTimeMillis();
        while((line = buffer.readLine()) != null){
            sb.append(line).append("\n");
            if(line.contains(pattern)){
            return line;
            }

            if(line.trim().isEmpty()){
                break;
            }
        }
        return sb.toString();
    }

    private boolean containsPattern(String pattern) throws IOException {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
        String line;
        while((line = buffer.readLine()) != null){
            if(line.contains(pattern)){
                return true;
            }

            if(line.trim().isEmpty()){
                break;
            }
        }
        return false;
    }


    private void write(String value){
        try{
            out.println(value);
            out.flush();
            System.out.println(value);
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    private void closeTelnetSession(){
        write("quit");
    }


}
