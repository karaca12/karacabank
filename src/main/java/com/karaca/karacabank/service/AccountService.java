package com.karaca.karacabank.service;

import com.karaca.karacabank.exception.AccountIdNotFoundException;
import com.karaca.karacabank.exception.CustomerIdNotFoundException;
import com.karaca.karacabank.model.Account;
import com.karaca.karacabank.model.Customer;
import com.karaca.karacabank.repo.AccountRepository;
import com.karaca.karacabank.repo.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    public ResponseEntity<Account> createAccountToCustomer(Integer customerId, Account account) {
        Optional<Customer> optionalCustomer=customerRepository.findById(customerId);
        if(optionalCustomer.isEmpty()){
            throw new CustomerIdNotFoundException("Customer with Id:"+customerId+" not found.");
        }
        Customer customer=optionalCustomer.get();
        account.setCustomer(customer);
        customer.getAccounts().add(accountRepository.save(account));
        customerRepository.save(customer);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    public ResponseEntity<Account> updateAccountById(Integer accountId, Account updatedAccount) {
        Optional<Account> optionalAccount=accountRepository.findById(accountId);
        if(optionalAccount.isEmpty()){
            throw new AccountIdNotFoundException("Account with Id:"+accountId+" not found.");
        }
        Account account=optionalAccount.get();
        account.setAccountBalance(updatedAccount.getAccountBalance());
        return new ResponseEntity<>(account,HttpStatus.OK);
    }

    public void deleteAccountById(Integer accountId) {
        accountRepository.deleteById(accountId);
    }

    public ResponseEntity<List<Account>> getAllAccounts() {
        return new ResponseEntity<>(accountRepository.findAll(),HttpStatus.OK);
    }

    public ResponseEntity<Account> getAccountById(Integer accountId) {
        Optional<Account> optionalAccount=accountRepository.findById(accountId);
        if(optionalAccount.isEmpty()){
            throw new AccountIdNotFoundException("Account with Id:"+accountId+" not found.");
        }
        Account account=optionalAccount.get();
        return new ResponseEntity<>(account,HttpStatus.OK);
    }
}
