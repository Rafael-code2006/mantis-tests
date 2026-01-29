package ru.stqa.pft.mantis.appmanager;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.BrowserType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class ApplicationManager {

    private final Properties properties;
    private WebDriver driver;
    private String browser;
    private HttpSession session;
    private RegistrationHelper registrationHelper;
    private FtpHelper ftp;
    private MailHelper mailHelper;

    public ApplicationManager(String browser) throws IOException {
        this.browser = browser;
        properties = new Properties();
    }

    public void init() throws IOException {
        String target = System.getProperty("target", "local");
        properties.load(new FileReader(new File(String.format("src/test/resources/%s.properties", target))));
   }

    public void stop() {
        if(driver != null){
           driver.quit();
        }
    }


    public boolean isElementPresent(By by) {
      try {
        driver.findElement(by);
        return true;
      } catch (NoSuchElementException e) {
        return false;
      }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }


    public HttpSession newSession(){
        return new HttpSession(this);
    }

    public RegistrationHelper registration(){
        if(registrationHelper == null){
            return registrationHelper = new RegistrationHelper(this);
        }
            return registrationHelper;
    }


    public FtpHelper ftp(){
        if(ftp == null) {
           ftp = new FtpHelper(this);
        }
        return ftp;
    }

    public MailHelper mail(){
        if(mailHelper == null){
            mailHelper = new MailHelper(this);
        }
        return mailHelper;
    }

    public WebDriver getDriver() {
        if(driver == null){
            if(Objects.equals(browser, BrowserType.CHROME)){
                System.setProperty("webdriver.chrome.driver", "C:\\Users\\ra_gimadeyev\\chromedriver.exe");
                driver = new ChromeDriver();
            } else if(Objects.equals(browser, BrowserType.FIREFOX)){
                System.setProperty("webdriver.gecko.driver", "C:\\Users\\ra_gimadeyev\\geckodriver.exe");
                driver = new FirefoxDriver();
            } else {
                System.setProperty("webdriver.edge.driver", "C:\\Users\\ra_gimadeyev\\msedgedriver.exe");
                driver = new EdgeDriver();
            }
        }
        return driver;
    }
}
