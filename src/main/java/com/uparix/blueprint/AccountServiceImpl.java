package com.uparix.blueprint;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private List<Account> accounts = new ArrayList<>();

    public AccountServiceImpl() {
        Account account = new Account();
        account.setId(0);
        account.setNumber("AA123456");
        account.setVersion(1);
        accounts.add(account);
    }

    @Override
    public Account findBy(int id) {
        return accounts.stream().filter(account -> account.getId() == id).findFirst().orElse(null);
    }

    @Override
    public void update(Account account) {
        if ( findBy(account.getId()) != null) {
            Account a = findBy(account.getId());
            accounts.remove(a);
            account.setVersion(a.getVersion()+1);
            accounts.add(account);
            return;
        }
        account.setVersion(0);
        accounts.add(account);
    }

}
