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

    public ApplicationManager(String browser) throws IOException {
        this.browser = browser;
        properties = new Properties();
    }

    public void init() throws IOException {
        String target = System.getProperty("target", "local");
        System.out.println("System.getProperty(\"browser\"): " + System.getProperty("browser"));
        properties.load(new FileReader(new File(String.format("src/test/resources/%s.properties", target))));

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

        driver.get(properties.getProperty("web.baseUrl"));
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
}
