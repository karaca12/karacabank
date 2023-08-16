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
    private final EmailService emailService;

    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository, EmailService emailService) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.emailService = emailService;
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

        accountEmailSender(account.getCustomer().getCustomerEmail(),
                "New Account",
                "Dear "+account.getCustomer().getCustomerName()+",\n\n"+"Enjoy your new "
                        +account.getCurrency()+" "+account.getAccountType()+" account.\n\n"
                        +"Account ID: "+account.getAccountId()+"\n\nKaracaBank Team");

        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    public ResponseEntity<Account> updateAccountById(Integer accountId, Account updatedAccount) {
        Optional<Account> optionalAccount=accountRepository.findById(accountId);
        if(optionalAccount.isEmpty()){
            throw new AccountIdNotFoundException("Account with Id:"+accountId+" not found.");
        }
        Account account=optionalAccount.get();
        account.setAccountBalance(updatedAccount.getAccountBalance());

        accountEmailSender(account.getCustomer().getCustomerEmail(),
                "Account Updated",
                "Dear "+account.getCustomer().getCustomerName()+",\n\n"+"Your account info has been updated."
                        +"If you are unaware of this change please contact us.\n\n"
                        +"Account ID: "+account.getAccountId()+"\n\nKaracaBank Team");

        return new ResponseEntity<>(account,HttpStatus.OK);
    }

    public void deleteAccountById(Integer accountId) {
        Optional<Account> optionalAccount=accountRepository.findById(accountId);
        if(optionalAccount.isEmpty()){
            throw new AccountIdNotFoundException("Account with Id:"+accountId+" not found.");
        }
        Account account=optionalAccount.get();
        accountEmailSender(account.getCustomer().getCustomerEmail(),
                "Account Deleted",
                "Dear "+account.getCustomer().getCustomerName()+",\n\n"
                        + "Your account with ID: "+accountId+" has been deleted."
                        +"If you are unaware of this change please contact us.\n\nKaracaBank Team");

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

    public void accountEmailSender(String customerEmail,String subject,String text){
        emailService.sendEmail(customerEmail,subject,text);
    }
}
