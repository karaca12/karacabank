package com.karaca.karacabank.service;

import com.karaca.karacabank.exception.CustomerIdNotFoundException;
import com.karaca.karacabank.model.Customer;
import com.karaca.karacabank.repo.AccountRepository;
import com.karaca.karacabank.repo.CustomerRepository;
import com.karaca.karacabank.repo.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    final CustomerRepository customerRepository;
    final AccountRepository accountRepository;
    final TransactionRepository transactionRepository;

    public CustomerService(CustomerRepository customerRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public ResponseEntity<Customer> createCustomer(Customer customer) {
        return new ResponseEntity<>(customerRepository.save(customer), HttpStatus.CREATED);
    }

    public ResponseEntity<Customer> updateCustomer(Integer customerId,Customer updatedCustomer) {
        Optional<Customer> optionalCustomer=customerRepository.findById(customerId);
        if(optionalCustomer.isEmpty()){
            throw new CustomerIdNotFoundException("Customer with Id:"+customerId+" not found.");
        }
        Customer customer=optionalCustomer.get();
        customer.setCustomerName(updatedCustomer.getCustomerName());
        customer.setCustomerBirthDate(updatedCustomer.getCustomerBirthDate());
        customer.setCustomerAddress(updatedCustomer.getCustomerAddress());
        customer.setCustomerEmail(updatedCustomer.getCustomerEmail());
        return new ResponseEntity<>(customerRepository.save(customer),HttpStatus.OK);
    }

    public ResponseEntity<List<Customer>> getAllCustomers() {
        return new ResponseEntity<>(customerRepository.findAll(),HttpStatus.OK);
    }

    public ResponseEntity<Customer> getCustomerById(Integer customerId) {
        Optional<Customer> optionalCustomer=customerRepository.findById(customerId);
        if(optionalCustomer.isEmpty()){
            throw new CustomerIdNotFoundException("Customer with Id:"+customerId+" not found.");
        }
        Customer customer=optionalCustomer.get();
        return new ResponseEntity<>(customer,HttpStatus.OK);
    }
    @Transactional
    public void deleteCustomerById(Integer customerId) {
        Optional<Customer> optionalCustomer=customerRepository.findById(customerId);
        if(optionalCustomer.isEmpty()){
            throw new CustomerIdNotFoundException("Customer with Id:"+customerId+" not found.");
        }
        transactionRepository.deleteTransactionsByCustomerId(customerId);
        accountRepository.deleteAccountsByCustomerId(customerId);
        customerRepository.deleteById(customerId);
    }
}
