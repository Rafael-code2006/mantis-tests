package ru.stqa.pft.mantis.homework;

import org.apache.commons.net.ftp.FTPClient;
import org.testng.ReporterConfig;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.*;
import java.util.Properties;

public class FtpTraining {
    private static Properties properties;
    private static FTPClient ftpClient;

    @BeforeMethod
    public void before() throws IOException {
        String server = properties.getProperty("ftp.server");
        int port = Integer.parseInt(properties.getProperty("ftp.port"));
        String username = properties.getProperty("ftp.username");
        String password = properties.getProperty("ftp.password");
        properties = new Properties();
        properties.load(new FileReader(new File("D:\\Java\\mantis-tests\\src\\test\\resources\\local_test.properties")));
        ftpClient = new FTPClient();
        ftpClient.connect(server);
        ftpClient.login(username, password);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
    }


    @AfterMethod
    public void after() throws IOException {
        ftpClient.logout();
        ftpClient.disconnect();
    }


    @Test
    public void test() throws IOException {


        try{
            File file = new File("src/test/resources/test.txt");
            storeFile(file);


            rename(file, "rename-test.txt");


            for(String x : listName()){
                System.out.println("До: " + x);
            }

            deleteFile(file);

            for(String x : listName()){
                System.out.println("После: " + x);
            }

            makeDirectory("NewFolder");

            workingDirectory("/newFolder");

            storeFile(file);

            workingDirectory("..");


            removeDirectory("NewFolder");

            System.out.println("Успешное подключение к ftp серверу");


        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void removeDirectory(String title) throws IOException {
        ftpClient.removeDirectory(title);
        System.out.println("Удалил директорию");
    }

    private static void workingDirectory(String pathName) throws IOException {
        ftpClient.changeWorkingDirectory(pathName);
    }

    private static void makeDirectory(String title) throws IOException {
        ftpClient.makeDirectory(title);
        System.out.println("Создал директорию");
    }

    private static void deleteFile(File file) throws IOException {
        ftpClient.deleteFile(file.getName());
        System.out.println("Удалил файл");
    }

    private static String[] listName() throws IOException {
        String[] beforeFile = ftpClient.listNames();
        for(String x : beforeFile){
            System.out.println("Файл на сервере: " + x);
        }
        return beforeFile;
    }

    private static void storeFile(File file) throws IOException {
        try(FileInputStream fil = new FileInputStream(file.getPath())){
            boolean success = ftpClient.storeFile(file.getName(), fil);
            System.out.println("Файл создан на сервере: " + success);
        }
    }

    private static void rename(File file, String newFile) throws IOException {
        ftpClient.rename(file.getName(), newFile);
        System.out.println("переименовал");
    }
}
