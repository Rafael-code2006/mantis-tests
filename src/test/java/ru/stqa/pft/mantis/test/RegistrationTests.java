package ru.stqa.pft.mantis.test;

import org.testng.annotations.Test;

public class RegistrationTests extends  TestBase {

    @Test
    public void test(){
        app.registration().start("user1", "user1@localhost.localdomain");
    }
}
