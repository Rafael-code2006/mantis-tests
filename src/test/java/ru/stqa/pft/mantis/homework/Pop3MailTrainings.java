package ru.stqa.pft.mantis.homework;

import org.testng.annotations.Test;
import ru.stqa.pft.mantis.appmanager.ApplicationManager;
import ru.stqa.pft.mantis.model.MailMessage;

import javax.mail.*;
import java.io.IOException;
import java.util.List;

public class Pop3MailTrainings {
    private static ApplicationManager app;
    private static Session mailSession;
    private static Store store;
    private static String mailServer;
    @Test
    public void test(){

    }


    private static Folder openFolder(String hos, String username, String password) throws MessagingException {
        store = mailSession.getStore("pop3");
        store.connect(mailServer, username, password);
        Folder folder = store.getDefaultFolder().getFolder("INBOX");
        folder.open(Folder.READ_WRITE);
        return folder;
    }


    private static MailMessage toModelMail(Message m){
        try {
            return new MailMessage(m.getAllRecipients()[0].toString(), (String) m.getContent());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void closeFolder(Folder folder) throws MessagingException {
        folder.close(true);
        store.close();
    }


    private static List<MailMessage> waitForMail(String username, String password, int timeout){
        long start = System.currentTimeMillis();
        while(System.currentTimeMillis() - start < timeout){
            List<MailMessage> messages = getAllMail(username, password);
            if(!messages.isEmpty()) return messages;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        throw new Error("No mail :(");

    }

    private static List<MailMessage> getAllMail(String username, String password) {

    }


}
