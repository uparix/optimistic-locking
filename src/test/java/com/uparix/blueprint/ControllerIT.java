package com.uparix.blueprint;

import com.jayway.restassured.filter.log.ResponseLoggingFilter;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerIT extends AbstractTestNGSpringContextTests {

    @LocalServerPort
    int port;

    String eTag;

    String path = "/account/{id}";

    @Test
    public void getAccount() {
        eTag = given().port(port)
            .filter(new ResponseLoggingFilter())
            .log().headers()
            .pathParam("id", 0)
            .get(path)
            .getHeader("ETag");
    }

    @Test(dependsOnMethods = { "getAccount" })
    public void getAccount_returnsNotModified() {
        given()
            .header("If-None-Match", eTag)
            .port(port)
            .filter(new ResponseLoggingFilter())
            .log().headers()
            .pathParam("id", 0)
            .get(path)
            .then().statusCode(304);

    }

    @Test(dependsOnMethods = { "getAccount" })
    public void putAccount_returnsOk() {
        Account account = new Account();
        account.setNumber("ACC999999");

        given()
            .header("If-Match", eTag)
            .header("Content-Type", "application/json")
            .body(account)
            .port(port)
            .filter(new ResponseLoggingFilter())
            .log().headers()
            .pathParam("id", 0)
            .put(path)
            .then().statusCode(200);
    }


    @Test(dependsOnMethods = { "getAccount" })
    public void putAccount_returnsUnprocessableEntity() {
        Account account = new Account();
        account.setId(0);
        account.setNumber("ACC888888");

        given()
            .header("If-Match", eTag)
            .header("Content-Type", "application/json")
            .body(account)
            .port(port)
            .filter(new ResponseLoggingFilter())
            .log().headers()
            .pathParam("id", 0)
            .put(path)
            .then().statusCode(422);
    }

    @Test(dependsOnMethods = {"putAccount_returnsUnprocessableEntity"})
    public void updateGetAccountETag() {
        eTag = given().port(port)
            .filter(new ResponseLoggingFilter())
            .log().headers()
            .pathParam("id", 0)
            .get(path)
            .getHeader("ETag");

    }

    @Test(dependsOnMethods = { "updateGetAccountETag" })
    public void putAccount_returnsOkAgain() {
        Account account = new Account();
        account.setNumber("ACC999999");

        given()
            .header("If-Match", eTag)
            .header("Content-Type", "application/json")
            .body(account)
            .port(port)
            .filter(new ResponseLoggingFilter())
            .log().headers()
            .pathParam("id", 0)
            .put(path)
            .then().statusCode(200);
    }

    @Test(dependsOnMethods = { "putAccount_returnsOkAgain" })
    public void getAccount_returnsLatest() {
        given().port(port)
            .filter(new ResponseLoggingFilter())
            .log().headers()
            .pathParam("id", 0)
            .get(path)
            .then().statusCode(200)
            .body("version", equalTo(3));
    }

}
