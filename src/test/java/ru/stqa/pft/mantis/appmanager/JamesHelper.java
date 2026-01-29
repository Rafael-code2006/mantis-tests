package ru.stqa.pft.mantis.appmanager;

import org.apache.commons.net.telnet.TelnetClient;

import javax.mail.Session;
import javax.mail.Store;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.SocketException;

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


    public boolean doesUserExists(String name){
        initTelnetSession();
        write("verify" + name);
        String result = readUntil("exists");
        closeTelnetSession();
        return result.trim().equals("User " + name + " exist");
    }



    public void createUser(String name, String passwd){
        initTelnetSession();
        write("adduser " + name + " " + passwd);
        String result = readUntil("User " + name + " added");
        closeTelnetSession();
    }



    public void deleteUser(String name){
        initTelnetSession();
        write("deluser " + name);
        String result = readUntil("User " + name + " deleted");
        closeTelnetSession();
    }

    private void initTelnetSession(){
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


        readUntil("Logged id:");
        write("");
        readUntil("Password:");
        write("");


        readUntil("Login id:");
        write(login);
        readUntil("Password");
        write(password);


        readUntil("Welcome " + login + ". HELP for a list of commands");

    }


    private String readUntil(String pattern){
        try{
            char lastChar = pattern.charAt(pattern.length()-1);
            StringBuffer sb = new StringBuffer();
            char ch = (char) in.read();
            while(true){
                System.out.println(ch);
                sb.append(ch);
                if(ch == lastChar){
                    if(sb.toString().endsWith(pattern)){
                        return sb.toString();
                    }
                }
                ch = (char) in.read();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
