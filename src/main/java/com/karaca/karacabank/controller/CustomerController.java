package com.karaca.karacabank.controller;

import com.karaca.karacabank.model.Customer;
import com.karaca.karacabank.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("customers")
public class CustomerController {
    final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    @PostMapping("createCustomer")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer){
        return customerService.createCustomer(customer);
    }
    @PutMapping("updateCustomer/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Integer customerId,@RequestBody Customer customer){
        return customerService.updateCustomer(customerId,customer);
    }
    @GetMapping("allCustomers")
    public ResponseEntity<List<Customer>> getAllCustomers(){
        return customerService.getAllCustomers();
    }
    @GetMapping("customer/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Integer customerId){
        return customerService.getCustomerById(customerId);
    }
    @DeleteMapping("deleteCustomer/{customerId}")
    public void deleteCustomerById(@PathVariable Integer customerId){
        customerService.deleteCustomerById(customerId);
    }
}
