package com.karaca.karacabank.controller;

import com.karaca.karacabank.dto.AmountOfTransaction;
import com.karaca.karacabank.model.Transaction;
import com.karaca.karacabank.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("transactions")
public class TransactionController {
    final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("cashWithdrawal/{accountId}")
    public ResponseEntity<Transaction> cashWithdrawalByAccountId(@PathVariable Integer accountId, @RequestBody AmountOfTransaction amountOfTransaction){
        return transactionService.cashWithdrawalByAccountId(accountId,amountOfTransaction);
    }
    @PostMapping("cashDeposit/{accountId}")
    public ResponseEntity<Transaction> cashDepositByAccountId(@PathVariable Integer accountId, @RequestBody AmountOfTransaction amountOfTransaction){
        return transactionService.cashDepositByAccountId(accountId,amountOfTransaction);
    }
    @PostMapping("cashTransfer/from{accountId1}/to{accountId2}")
    public ResponseEntity<Map<String,Transaction>> cashTransferByAccountIds
            (@PathVariable Integer accountId1,@PathVariable Integer accountId2,@RequestBody AmountOfTransaction amountOfTransaction){
        return transactionService.cashTransferByAccountIds(accountId1,accountId2,amountOfTransaction);
    }
}
