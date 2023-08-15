package com.karaca.karacabank.repo;

import com.karaca.karacabank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
    @Modifying
    @Query("DELETE FROM Transaction t WHERE t.account IN " +
            "(SELECT a FROM Account a WHERE a.customer.customerId = :customerId)")
    void deleteTransactionsByCustomerId(Integer customerId);
}
