package com.uparix.blueprint;

public interface AccountService {

    Account findBy(int id);

    void update(Account account);

}
