package ru.stqa.pft.mantis.appmanager;

import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

public class RegistrationHelper {
    private ApplicationManager app;
    private WebDriver driver;

    public RegistrationHelper(ApplicationManager app) {
        this.app = app;
        driver = app.getDriver();
    }

    public void start(String username, String email){
        driver.get(app.getProperty("web.baseUrl") + "signup_page.php");
    }

}
