package ru.stqa.pft.mantis.appmanager;

import org.openqa.selenium.By;

public class RegistrationHelper extends HelperBase{
    private ApplicationManager app;

    public RegistrationHelper(ApplicationManager app) {
        super(app);
        this.app = app;
    }

    public void start(String username, String email){
        app.getDriver().get(app.getProperty("web.baseUrl") + "signup_page.php");
        type(By.name("username"), username);
        type(By.name("email"), email);
        click(By.xpath("//*[@value='Signup']"));
    }

    public void finish(String confirmation, String password) throws InterruptedException {
        app.getDriver().get(confirmation);
        type(By.name("password"), password);
        type(By.name("password_confirm"), password);
        click(By.cssSelector("input[value='Update User']"));
        Thread.sleep(3000);

    }
}
