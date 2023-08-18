package com.karaca.karacabank.service;

import com.karaca.karacabank.exception.CustomerIdNotFoundException;
import com.karaca.karacabank.exception.InvalidEmailException;
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
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final EmailValidationService emailValidationService;

    public CustomerService(CustomerRepository customerRepository, AccountRepository accountRepository, TransactionRepository transactionRepository,EmailValidationService emailValidationService) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.emailValidationService = emailValidationService;
    }

    public ResponseEntity<Customer> createCustomer(Customer customer) {
        String eMail=customer.getCustomerEmail();
        boolean isValid=emailValidationService.validateEmail(eMail);
        if(isValid){
            customerRepository.save(customer);
            return new ResponseEntity<>(customer, HttpStatus.CREATED);
        }else {
            throw new InvalidEmailException("Customer email not accepted.");
        }
    }

    public ResponseEntity<Customer> updateCustomer(Integer customerId, Customer updatedCustomer) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isEmpty()) {
            throw new CustomerIdNotFoundException("Customer with Id:" + customerId + " not found.");
        }
        Customer customer = optionalCustomer.get();
        customer=customerUpdater(customer,updatedCustomer);
        String eMail=customer.getCustomerEmail();
        boolean isValid=emailValidationService.validateEmail(eMail);
        if(isValid) {
            return new ResponseEntity<>(customer, HttpStatus.OK);
        }else {
            throw new InvalidEmailException("Customer email not accepted.");
        }
    }

    public ResponseEntity<List<Customer>> getAllCustomers() {
        return new ResponseEntity<>(customerRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Customer> getCustomerById(Integer customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isEmpty()) {
            throw new CustomerIdNotFoundException("Customer with Id:" + customerId + " not found.");
        }
        Customer customer = optionalCustomer.get();
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @Transactional
    public void deleteCustomerById(Integer customerId) {
        transactionRepository.deleteTransactionsByCustomerId(customerId);
        accountRepository.deleteAccountsByCustomerId(customerId);
        customerRepository.deleteById(customerId);
    }

    public Customer customerUpdater(Customer customer,Customer updatedCustomer){
        customer.setCustomerName(updatedCustomer.getCustomerName());
        customer.setCustomerBirthDate(updatedCustomer.getCustomerBirthDate());
        customer.setCustomerAddress(updatedCustomer.getCustomerAddress());
        customer.setCustomerEmail(updatedCustomer.getCustomerEmail());
        customer.setCustomerPhone(updatedCustomer.getCustomerPhone());
        return customerRepository.save(customer);
    }
}
