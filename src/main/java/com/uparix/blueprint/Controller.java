package com.uparix.blueprint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.unprocessableEntity;
import static org.springframework.util.DigestUtils.appendMd5DigestAsHex;

@RestController
public class Controller {

    @Autowired
    private AccountService service;

    @ResponseBody
    @RequestMapping(value = "/account/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Account> getAccount(@PathVariable int id) {
        ResponseEntity.BodyBuilder builder = ok();
        Account account = service.findBy(id);
        builder.header("If-Match", computeETag(account.getVersion()));
        builder.contentType(MediaType.APPLICATION_JSON);
        builder.eTag(computeETag(account.getVersion()));
;       return builder.body(account);
    }

    @ResponseBody
    @RequestMapping(value = "/account/{id}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
    public ResponseEntity<Account> putAccount(@RequestBody Account account, @RequestHeader("If-Match") String ifMatch,
                                              @PathVariable int id) {
        if (!matchETag(ifMatch, this.getAccount(id).getBody().getVersion())) {
            return unprocessableEntity().build();
        }
        service.update(account);
        return ok().contentType(MediaType.APPLICATION_JSON).body(account);
    }

    private boolean matchETag(String ifMatch, int version) {
        return ifMatch != null && !"*".equals(ifMatch) && ifMatch.trim().equals(computeETag(version));
    }

    private String computeETag(int version) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        appendMd5DigestAsHex(String.valueOf(version).getBytes(), sb);
        sb.append("\"");
        return sb.toString();
    }

}
