package com.karaca.karacabank.controller;

import com.karaca.karacabank.model.Account;
import com.karaca.karacabank.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    @PostMapping("createAccountTo/{customerId}")
    public ResponseEntity<Account> createAccountToCustomer(@PathVariable Integer customerId, @RequestBody Account account){
        return accountService.createAccountToCustomer(customerId,account);
    }
    @PutMapping("updateAccount/{accountId}")
    public ResponseEntity<Account> updateAccountById(@PathVariable Integer accountId,@RequestBody Account account){
        return accountService.updateAccountById(accountId,account);
    }
    @DeleteMapping("deleteAccount/{accountId}")
    public void deleteAccountById(@PathVariable Integer accountId){
        accountService.deleteAccountById(accountId);
    }
    @GetMapping("allAccounts")
    public ResponseEntity<List<Account>> getAllAccounts(){
        return accountService.getAllAccounts();
    }
    @GetMapping("account/{accountId}")
    public ResponseEntity<Account> getAccountById(@PathVariable Integer accountId){
        return accountService.getAccountById(accountId);
    }
}
