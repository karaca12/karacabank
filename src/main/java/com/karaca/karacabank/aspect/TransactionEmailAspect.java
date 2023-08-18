package com.karaca.karacabank.aspect;

import com.karaca.karacabank.model.Transaction;
import com.karaca.karacabank.service.EmailService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Map;

@Component
@Aspect
public class TransactionEmailAspect {
    private final EmailService emailService;

    public TransactionEmailAspect(EmailService emailService) {
        this.emailService = emailService;
    }


    @AfterReturning(value ="execution(* com.karaca.karacabank.service.TransactionService.cashTransferByAccountIds(..))" ,returning ="responseEntity")
    public void sendEmailCashTransfer(ResponseEntity<Map<String,Transaction>> responseEntity){
        Map<String, Transaction> transactionMap=responseEntity.getBody();
        assert transactionMap != null;
        Transaction transaction1=transactionMap.get("transaction1");
        Transaction transaction2=transactionMap.get("transaction2");
        transactionEmailSender(transaction1);
        transactionEmailSender(transaction2);
    }
    @AfterReturning(value ="execution(* com.karaca.karacabank.service.TransactionService.cashDepositByAccountId(..)) || "
            +"execution(* com.karaca.karacabank.service.TransactionService.cashWithdrawalByAccountId(..))" ,returning ="responseEntity")
    public void sendEmailCashDepositAndWithdrawal(ResponseEntity<Transaction> responseEntity){
        Transaction transaction=responseEntity.getBody();
        assert transaction != null;
        transactionEmailSender(transaction);
    }
    public void transactionEmailSender(Transaction transaction){
        DecimalFormat decimalFormat=new DecimalFormat("#.00");
        String formattedAmount= decimalFormat.format(transaction.getTransactionAmount());
        emailService.sendEmail(transaction.getAccount().getCustomer().getCustomerEmail(),
                "Transaction Done",
                "Dear "+transaction.getAccount().getCustomer().getCustomerName()+",\n\n"+"The transaction of "+formattedAmount
                        +" "+transaction.getAccount().getCurrency()+" is done.\n\n"+transaction.getTransactionType()
                        +"\n\nAccount ID: "+transaction.getAccount().getAccountId()
                        +"\n\nKaracaBank Team");
    }

}
