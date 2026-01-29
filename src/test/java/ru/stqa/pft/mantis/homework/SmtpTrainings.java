package ru.stqa.pft.mantis.homework;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;
import ru.stqa.pft.mantis.model.MailMessage;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SmtpTrainings {
    private static FTPClient ftp;
    private static Properties properties;
    private static WebDriver driver;
    public static void main(String[] args) throws Exception {

        properties = new Properties();
        properties.load(new FileInputStream("src/test/resources/local.properties"));
        ftp = new FTPClient();
        String originalFile = "config_inc.php";
        String copyFile = "config_inc.php.bak";
        ftpConnection();
        type();
        renameFile(originalFile, copyFile);
        addFile();
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\ra_gimadeyev\\chromedriver.exe");
        driver = new ChromeDriver();
        long currentMills = System.currentTimeMillis();
        String urlHost = properties.getProperty("web.baseUrl");
        String username = String.format("userBOB%s", currentMills);
        String password = "rooter123";
        driver.get(urlHost + "signup_page.php");
        Wiser wiser = new Wiser();
        wiser.start();
        registrationAccount(username, currentMills);
        returnOldFile(originalFile, copyFile);

        String link = wiserMessage(wiser);

        createPassword(link, password);

        Thread.sleep(2000);

        CloseableHttpClient client = HttpClients.createDefault();

        String newUsername = properties.getProperty("web.");
        HttpPost post = new HttpPost(properties.get("web.baseUrl") + "login.php");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("return", "index.php"));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("secure_session", "on"));
        post.setEntity(new UrlEncodedFormEntity(params));

        try(CloseableHttpResponse response = client.execute(post)){
            int current = response.getStatusLine().getStatusCode();
            if(current==302){
                System.out.println("Success");
            }
        }

        wiser.stop();

      }

    private static void createPassword(String link, String password) {
        driver.get(link);
        driver.findElement(By.xpath("//input[@name=\"password\"]")).sendKeys(password);
        driver.findElement(By.xpath("//input[@name=\"password_confirm\"]")).sendKeys(password);
        driver.findElement(By.xpath("//input[@value=\"Update User\"]")).click();
    }

    private static String wiserMessage(Wiser wiser) throws MessagingException, IOException {
        String link = null;

        for (WiserMessage x : wiser.getMessages()) {
            MimeMessage mm = x.getMimeMessage();
            String content = (String) mm.getContent(); // тело письма
            Pattern pattern = Pattern.compile("http://\\S+");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                link = matcher.group();
                System.out.println("Ссылка: " + link);
                break;
            }
        }
        return link;
    }

    private static void type() throws IOException {
        ftp.enterLocalPassiveMode();
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
    }

    private static void returnOldFile(String originalFile, String copyFile) throws InterruptedException, IOException {
        Thread.sleep(3000);
        ftp.deleteFile(originalFile);
        update(copyFile, originalFile);
        ftp.logout();
        ftp.disconnect();
    }

    private static void registrationAccount(String username, long currentMills) {
        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("email")).sendKeys(String.format("user%s@localhost.localdomain", currentMills));
        driver.findElement(By.xpath("//*[@value='Signup']")).click();
    }

    private static void addFile() throws IOException {
        File file = new File("src/test/resources/config_inc.php");
        try(FileInputStream fill = new FileInputStream(file)){
           boolean checkAdded =  ftp.storeFile(file.getName(), fill);
           System.out.println("Добавление файла: " + checkAdded);
        }
    }

    private static void renameFile(String originalFile, String copyFile) throws IOException {
        boolean checkecRename = ftp.rename(originalFile, copyFile);
        if(checkecRename){
            System.out.println("Success");
        }
    }

    private static void ftpConnection() throws IOException {
        ftp.connect(properties.getProperty("ftp.host"));
        ftp.login(properties.getProperty("ftp.login"), properties.getProperty("ftp.password"));
    }


    private static MailMessage toModelMail(WiserMessage m) throws MessagingException, IOException {
          MimeMessage mm = m.getMimeMessage();
          String address = mm.getAllRecipients()[0].toString();
          String content = (String) mm.getContent();
          return new MailMessage(address, content);
      }



    private static void update(String oldFile, String newFile) throws IOException {
        List<String> before = Arrays.asList(ftp.listNames());
        if(before.contains(oldFile)){
            System.out.println("Success");
            ftp.rename(oldFile, newFile);
        } else {
            System.out.println("Не нашел файл");
        }
    }
}
