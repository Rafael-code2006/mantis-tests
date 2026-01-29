package ru.stqa.pft.mantis.appmanager;

import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HelperBase {
    protected ApplicationManager app;

    public HelperBase(ApplicationManager app) {
        this.app = app;
    }


    protected void click(By locator) {
        waitFindElement(locator).click();
    }

    public boolean isAlertPresent() {
        try {
            app.getDriver().switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    protected void type(By locator, String text) {
        click(locator);
        waitFindElement(locator).clear();
        if(text != null) {
            waitFindElement(locator).sendKeys(text);
        }
    }

    protected void attach(By locator, File file) {
        if(file != null) {
            waitFindElement(locator).sendKeys(file.getAbsolutePath());
        }
    }
    protected boolean isElementPresent(By locator) {
            long currentTime = System.currentTimeMillis();
            while(System.currentTimeMillis() < currentTime + 3000){
                try {
                   WebElement element =  waitFindElement(locator);
                   if(element == null){
                       return false;
                   }
                    return true;
                }catch (NullPointerException ex) {
                    return false;
                }
            }
            return false;
    }

    public boolean isThereAGroup(String nameClass) {
        if(!isElementPresent(By.className(nameClass))){
            return false;
        } else {
            return true;
        }
    }

    protected List<WebElement> waitFindElements(By xpath) {
        long currentTime = System.currentTimeMillis();
        while(System.currentTimeMillis() < currentTime + 3000){
            List<WebElement> elements = new ArrayList<>();
            if(!app.getDriver().findElements(xpath).isEmpty()){
                elements = app.getDriver().findElements(xpath);
                if(!elements.isEmpty()) return elements;
            }
        }
        return new ArrayList<>();
    }

    public WebElement waitFindElement(By locator) {
        long currentTime = System.currentTimeMillis();
        while(System.currentTimeMillis() < currentTime + 500){
            try{
                return app.getDriver().findElement(locator);
            } catch (Exception ex){
                // Do nothing
            }
        }
        return null;
    }

}
