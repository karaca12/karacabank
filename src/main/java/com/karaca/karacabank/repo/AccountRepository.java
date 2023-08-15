package com.karaca.karacabank.repo;

import com.karaca.karacabank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account,Integer> {
    @Modifying
    @Query("DELETE from Account a where a.customer.customerId=:customerId")
    void deleteAccountsByCustomerId(Integer customerId);
}
