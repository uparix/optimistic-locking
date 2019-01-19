package com.uparix.blueprint;

import com.jayway.restassured.filter.log.ResponseLoggingFilter;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.text.IsEmptyString.isEmptyString;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerIT extends AbstractTestNGSpringContextTests {

    @LocalServerPort
    int port;

    String eTag;

    @Test
    public void getAccount() {
        eTag = given().port(port)
            .filter(new ResponseLoggingFilter())
            .get("/account")
            .getHeader("ETag");
    }

    @Test(dependsOnMethods = { "getAccount" })
    public void getAccount_returnsNotModified() {
        given()
            .header("If-None-Match", eTag)
            .port(port)
            .filter(new ResponseLoggingFilter())
            .get("/account")
            .then().statusCode(304)
            .and().header("ETag", not(isEmptyString()));

    }

    @Test(dependsOnMethods = { "getAccount" })
    public void putAccount_returnsOk() {
        Controller.Account account = new Controller.Account();
        account.setNumber("ACC999999");
        account.setVersion(2);

        given()
            .header("ETag", eTag)
            .header("Content-Type", "application/json")
            .body(account)
            .port(port)
            .filter(new ResponseLoggingFilter())
            .put("/account")
            .then().statusCode(200);
    }


    @Test(dependsOnMethods = { "getAccount" })
    public void putAccount_returnsUnprocessableEntity() {
        Controller.Account account = new Controller.Account();
        account.setNumber("ACC888888");
        account.setVersion(2);

        given()
            .header("ETag", eTag)
            .header("Content-Type", "application/json")
            .body(account)
            .port(port)
            .filter(new ResponseLoggingFilter())
            .put("/account")
            .then().statusCode(422);
    }

    @Test(dependsOnMethods = {"putAccount_returnsUnprocessableEntity"})
    public void updateGetAccountETag() {
        eTag = given().port(port)
            .filter(new ResponseLoggingFilter())
            .get("/account")
            .getHeader("ETag");

    }

    @Test(dependsOnMethods = { "updateGetAccountETag" })
    public void putAccount_returnsOkAgain() {
        Controller.Account account = new Controller.Account();
        account.setNumber("ACC999999");
        account.setVersion(3);

        given()
            .header("ETag", eTag)
            .header("Content-Type", "application/json")
            .body(account)
            .port(port)
            .filter(new ResponseLoggingFilter())
            .put("/account")
            .then().statusCode(200);
    }

    @Test(dependsOnMethods = { "putAccount_returnsOkAgain" })
    public void getAccount_returnsLatest() {
        given().port(port)
            .header("ETag", eTag)
            .filter(new ResponseLoggingFilter())
            .get("/account")
            .then().statusCode(200)
            .body("version", equalTo(3));
    }

}
