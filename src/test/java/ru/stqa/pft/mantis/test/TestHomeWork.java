package ru.stqa.pft.mantis.test;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.AssertJUnit.assertTrue;

public class TestHomeWork {

    @Test
    public void test() throws IOException {
        login();
    }

    private static void login() throws IOException {
        String baseUrl = "http://localhost/mantisbt-1.2.19/";
        String username = "administrator";
        String password = "root";
        CookieStore cookieStore = new BasicCookieStore();
        try (CloseableHttpClient client = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build()) {

            // 1. POST: логин
            HttpPost post = new HttpPost(baseUrl + "login.php");
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("return", "index.php"));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("secure_session", "on"));
            post.setEntity(new UrlEncodedFormEntity(params));

            try (CloseableHttpResponse response = client.execute(post)) {
                assertThat(response.getStatusLine().getStatusCode(), equalTo(302));
            }

            // 2. GET: страница создания проекта
            HttpGet get = new HttpGet(baseUrl + "manage_proj_create_page.php");
            String token;
            try (CloseableHttpResponse response = client.execute(get)) {
                String body = EntityUtils.toString(response.getEntity());
                // парсим токен из HTML (например, Jsoup)
                org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(body);
                token = doc.select("input[name=manage_proj_create_token]").attr("value");
            }

            // 3. POST: создание проекта
            HttpPost postCreateProject = new HttpPost(baseUrl + "manage_proj_create.php");
            params = new ArrayList<>();
            params.add(new BasicNameValuePair("manage_proj_create_token", token));
            params.add(new BasicNameValuePair("name", "MyTestProject"));
            params.add(new BasicNameValuePair("status", "10"));
            params.add(new BasicNameValuePair("inherit_global", "on"));
            params.add(new BasicNameValuePair("view_state", "10"));
            params.add(new BasicNameValuePair("description", "Its my first using httpClient Apache"));
            postCreateProject.setEntity(new UrlEncodedFormEntity(params));

            try (CloseableHttpResponse response = client.execute(postCreateProject)) {
                // обычно MantisBT делает редирект (302) после успешного создания
                assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
            }

            // 4. GET: проверка, что проект появился
            HttpGet getCreateProject = new HttpGet(baseUrl + "manage_proj_page.php");
            try (CloseableHttpResponse response = client.execute(getCreateProject)) {
                assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
                String body = EntityUtils.toString(response.getEntity());
                System.out.println(body  );
                assertTrue(body.contains("MyTestProject"));
            }
        }

    }
}
