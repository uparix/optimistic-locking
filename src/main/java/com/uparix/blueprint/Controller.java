package com.uparix.blueprint;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.DigestUtils.appendMd5DigestAsHex;

@RestController
public class Controller {

    private Account account;

    public Controller() {
        account = new Account();
        account.setVersion(1);
        account.setNumber("ACC0123456");
    }

    @ResponseBody
    @RequestMapping(value = "/account", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Account> getAccount() {
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
        builder.eTag(createETag(account.getVersion()));
        builder.contentType(MediaType.APPLICATION_JSON);
        return builder.body(account);
    }

    @ResponseBody
    @RequestMapping(value = "/account", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
    public ResponseEntity<Account> putAccount(@RequestBody Account account, @RequestHeader("ETag") String eTag) {
        if ( !eTag.equals("\"" + createETag(this.account.getVersion()) + "\"") ) {
            ResponseEntity.BodyBuilder builder = ResponseEntity.unprocessableEntity();
            builder.contentType(MediaType.APPLICATION_JSON);
            builder.eTag(createETag(this.account.getVersion()));
            return builder.build();
        }

        this.account = account;
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
        builder.eTag(createETag(account.getVersion()));
        builder.contentType(MediaType.APPLICATION_JSON);
        return builder.body(account);
    }

    private String createETag(int version) {
        StringBuilder sb = new StringBuilder();
        appendMd5DigestAsHex(String.valueOf(version).getBytes(), sb);
        return sb.toString();
    }

    static class Account {

        private int version;

        private String number;

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

    }

}
