package ru.stqa.pft.mantis.homework;

import org.hamcrest.CoreMatchers;
import org.testng.annotations.Test;
import ru.stqa.pft.mantis.appmanager.ApplicationManager;
import ru.stqa.pft.mantis.appmanager.JamesHelper;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.AssertJUnit.assertTrue;

public class JamesTraining {
    private ApplicationManager app;

    @Test
    public void test() throws IOException {
        app = new ApplicationManager("chrome");
        app.init();


        long target = System.currentTimeMillis();
        String name = String.format("user%s", target - 1000000);
        String password = "root";
        String host = app.getProperty("mailserver.host");
        app.james().createUser(name, password);
        List<String> afterCreate = app.james().listUsers();
        assertTrue(app.james().verifyUser(name));
        app.james().deleteUser(name);
        List<String> afterDelete = app.james().listUsers();
        assertThat(afterCreate.size(), equalTo(afterDelete.size()+1));

    }

}
