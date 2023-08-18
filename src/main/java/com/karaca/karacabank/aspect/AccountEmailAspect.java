package com.karaca.karacabank.aspect;

import com.karaca.karacabank.exception.AccountIdNotFoundException;
import com.karaca.karacabank.model.Account;
import com.karaca.karacabank.repo.AccountRepository;
import com.karaca.karacabank.service.EmailService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Aspect
public class AccountEmailAspect {
    private final EmailService emailService;
    private final AccountRepository accountRepository;
    public AccountEmailAspect(EmailService emailService, AccountRepository accountRepository) {
        this.emailService = emailService;
        this.accountRepository = accountRepository;
    }

    @AfterReturning(value = "execution(* com.karaca.karacabank.service.AccountService.createAccountToCustomer(..))",returning = "responseEntity")
    public void sendEmailCreateAccountToCustomer(ResponseEntity<Account> responseEntity) {
        Account account=responseEntity.getBody();
        assert account!=null;
        emailService.sendEmail(account.getCustomer().getCustomerEmail(),
                "New Account",
                "Dear "+account.getCustomer().getCustomerName()+",\n\n"+"Enjoy your new "
                        +account.getCurrency()+" "+account.getAccountType()+" account.\n\n"
                        +"Account ID: "+account.getAccountId()+"\n\nKaracaBank Team");
    }
    @AfterReturning(value = "execution(* com.karaca.karacabank.service.AccountService.updateAccountById(..))",returning = "responseEntity")
    public void sendEmailUpdateAccountByID(ResponseEntity<Account> responseEntity) {
        Account account=responseEntity.getBody();
        assert account!=null;
        emailService.sendEmail(account.getCustomer().getCustomerEmail(),
                "Account Updated",
                "Dear "+account.getCustomer().getCustomerName()+",\n\n"+"Your account info has been updated."
                        +"If you are unaware of this change please contact us.\n\n"
                        +"Account ID: "+account.getAccountId()+"\n\nKaracaBank Team");
    }
    @Before(value = "execution(* com.karaca.karacabank.service.CustomerService.deleteCustomerById(..))&&args(accountId)")
    public void sendEmailDeleteAccountById(Integer accountId){
        Optional<Account> optionalAccount=accountRepository.findById(accountId);
        if(optionalAccount.isEmpty()){
            throw new AccountIdNotFoundException("Account with Id:"+accountId+" not found.");
        }
        Account account=optionalAccount.get();
        emailService.sendEmail(account.getCustomer().getCustomerEmail(),
                "Account Deleted",
                "Dear "+account.getCustomer().getCustomerName()+",\n\n"
                        + "Your account with ID: "+accountId+" has been deleted."
                        +"If you are unaware of this change please contact us.\n\nKaracaBank Team");
    }
}
