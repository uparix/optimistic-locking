package com.uparix.blueprint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {Application.class})
@ComponentScan("com.uparix")
public class AccountServiceTest {

    @Autowired
    private AccountService service;

    @Test
    public void findBy() {
        Account account = service.findBy(0);
        assertNotNull("Account not found", account);
        assertEquals("", 1, account.getVersion());
   }

    @Test
    public void update() {
        Account account = new Account();
        account.setNumber("AB123456");
        account.setVersion(1);
        account.setId(1);
        service.update(account);

        Account storedAccount = service.findBy(1);
        assertNotNull("Account not found", storedAccount);
        assertEquals("", 1, account.getVersion());
    }

}
