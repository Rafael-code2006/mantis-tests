package ru.stqa.pft.mantis.test;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.lanwen.verbalregex.VerbalExpression;
import ru.stqa.pft.mantis.model.MailMessage;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

import static org.testng.AssertJUnit.assertTrue;

public class RegistrationTests extends  TestBase {

    //@BeforeMethod
    public void before(){
        app.mail().start();
    }

    @Test
    public void test() throws IOException, MessagingException, InterruptedException {
        long now = System.currentTimeMillis();
        String password = "password";
        String user = String.format("user%s", now);
        String email = String.format("user%s@localhost.localdomain", now);
        app.james().createUser(user, password);
        app.registration().start(user, email);
        //List<MailMessage> mailMessages = app.mail().waitForMail(2, 10000);
        //List<MailMessage> messages = app.james().waitForMail(user, password, 60000);
       // String confirmation = findConfirmationLink(messages, email);
        //app.registration().finish(confirmation, password);
        assertTrue(app.newSession().login(user, password));
    }

    private String findConfirmationLink(List<MailMessage> mailMessages, String email) {
        MailMessage mailMessage = mailMessages.stream().filter((m) -> m.to.equals(email)).findFirst().get();
        VerbalExpression regex = VerbalExpression.regex().find("http://").nonSpace().oneOrMore().build();
        return regex.getText(mailMessage.text);
    }

    //@AfterMethod(alwaysRun = true)
    public void after(){
        app.mail().stop();
    }
}
