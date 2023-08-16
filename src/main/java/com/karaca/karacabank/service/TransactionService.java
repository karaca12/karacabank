package com.karaca.karacabank.service;

import com.karaca.karacabank.dto.AmountOfTransaction;
import com.karaca.karacabank.exception.SameIdsException;
import com.karaca.karacabank.constants.TransactionType;
import com.karaca.karacabank.exception.AccountIdNotFoundException;
import com.karaca.karacabank.exception.InsufficientBalanceException;
import com.karaca.karacabank.model.Account;
import com.karaca.karacabank.model.Transaction;
import com.karaca.karacabank.repo.AccountRepository;
import com.karaca.karacabank.repo.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CurrencyConversionService currencyConversionService;
    private final EmailService emailService;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, CurrencyConversionService currencyConversionService, EmailService emailService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.currencyConversionService = currencyConversionService;
        this.emailService = emailService;
    }

    public ResponseEntity<Transaction> cashWithdrawalByAccountId(Integer accountId, AmountOfTransaction amountOfTransaction) {
        Optional<Account> optionalAccount=accountRepository.findById(accountId);
        if(optionalAccount.isEmpty()){
            throw new AccountIdNotFoundException("Account with Id:"+accountId+" not found.");
        }
        double amount=amountOfTransaction.getAmountOfTransaction();
        Account account=optionalAccount.get();
        if(account.getAccountBalance() < amount){
            throw new InsufficientBalanceException("Insufficient balance for withdrawal.");
        }
        double updatedBalance=account.getAccountBalance()-amount;
        account.setAccountBalance(updatedBalance);

        Transaction transaction=new Transaction();
        transaction=saveTransaction(transaction,account,TransactionType.CASH_WITHDRAWAL,amount);

        transactionEmailSender(account,TransactionType.CASH_WITHDRAWAL,amount);

        return new ResponseEntity<>(transaction, HttpStatus.OK);

    }

    public ResponseEntity<Transaction> cashDepositByAccountId(Integer accountId, AmountOfTransaction amountOfTransaction) {
        Optional<Account> optionalAccount=accountRepository.findById(accountId);
        if(optionalAccount.isEmpty()){
            throw new AccountIdNotFoundException("Account with Id:"+accountId+" not found.");
        }
        double amount=amountOfTransaction.getAmountOfTransaction();
        Account account=optionalAccount.get();

        double updatedBalance=account.getAccountBalance()+amount;
        account.setAccountBalance(updatedBalance);

        Transaction transaction=new Transaction();
        transaction=saveTransaction(transaction,account,TransactionType.CASH_DEPOSIT,amount);

        transactionEmailSender(account,TransactionType.CASH_DEPOSIT,amount);

        return new ResponseEntity<>(transaction, HttpStatus.OK);

    }

    public ResponseEntity<Map<String,Transaction>> cashTransferByAccountIds(Integer accountId1, Integer accountId2, AmountOfTransaction amountOfTransaction) {
        Optional<Account> optionalAccount1=accountRepository.findById(accountId1);
        Optional<Account> optionalAccount2=accountRepository.findById(accountId2);
        if(optionalAccount1.isEmpty()) {
            throw new AccountIdNotFoundException("Account with Id:" + accountId1 + " not found.");
        }
        if(optionalAccount2.isEmpty()){
            throw new AccountIdNotFoundException("Account with Id:"+accountId2+" not found.");
        }
        if(optionalAccount1.equals(optionalAccount2)){
            throw new SameIdsException("Account Id's are the same. Transaction canceled.");
        }
        double amount=amountOfTransaction.getAmountOfTransaction();
        Account account1=optionalAccount1.get();
        Account account2=optionalAccount2.get();
        double convertedAmount=currencyConversionService.convertCurrency(amount,account1.getCurrency(),account2.getCurrency());
        double updatedBalance1=account1.getAccountBalance()-amount;
        double updatedBalance2=account2.getAccountBalance()+convertedAmount;
        account1.setAccountBalance(updatedBalance1);
        account2.setAccountBalance(updatedBalance2);
        accountRepository.save(account1);
        accountRepository.save(account2);

        Transaction transaction1=new Transaction();
        transaction1=saveTransaction(transaction1,account1,TransactionType.CASH_TRANSFER_SENT,amount);

        Transaction transaction2=new Transaction();
        transaction2=saveTransaction(transaction2,account2,TransactionType.CASH_TRANSFER_RECEIVED,convertedAmount);

        Map<String, Transaction> transactionMap = new HashMap<>();
        transactionMap.put("transaction1", transaction1);
        transactionMap.put("transaction2", transaction2);

        transactionEmailSender(account1,TransactionType.CASH_TRANSFER_SENT,amount);
        transactionEmailSender(account2,TransactionType.CASH_TRANSFER_RECEIVED,convertedAmount);

        return new ResponseEntity<>(transactionMap,HttpStatus.OK);
    }
    public Transaction saveTransaction(Transaction transaction,Account account,TransactionType transactionType,double amount){
        transaction.setAccount(account);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionAmount(amount);
        transaction.setTransactionDate(new Date());
        return transactionRepository.save(transaction);
    }
    public void transactionEmailSender(Account account,TransactionType transactionType,double amount){
        DecimalFormat decimalFormat=new DecimalFormat("#.00");
        String formattedAmount= decimalFormat.format(amount);
        String customerEmail=account.getCustomer().getCustomerEmail();
        String subject="Transaction Done";
        String text="Dear "+account.getCustomer().getCustomerName()+",\n\n"+"The transaction of "+formattedAmount
                +" "+account.getCurrency()+" is done.\n\n"+transactionType+"\n\nAccount ID: "+account.getAccountId()
                +"\n\nKaracaBank Team";
        emailService.sendEmail(customerEmail,subject,text);
    }
}

