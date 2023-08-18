package com.karaca.karacabank.aspect;

import com.karaca.karacabank.exception.CustomerIdNotFoundException;
import com.karaca.karacabank.model.Customer;
import com.karaca.karacabank.repo.CustomerRepository;
import com.karaca.karacabank.service.EmailService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Aspect
public class CustomerEmailAspect {
    private final EmailService emailService;
    private final CustomerRepository customerRepository;

    public CustomerEmailAspect(EmailService emailService, CustomerRepository customerRepository) {
        this.emailService = emailService;
        this.customerRepository = customerRepository;
    }

    @AfterReturning(value = "execution(* com.karaca.karacabank.service.CustomerService.createCustomer(..))",returning = "responseEntity")
    public void sendEmailCreateCustomer(ResponseEntity<Customer> responseEntity) {
        Customer customer=responseEntity.getBody();
        assert customer != null;
        emailService.sendEmail(customer.getCustomerEmail(),
                    "Welcome To Karacabank!",
                    "Dear " + customer.getCustomerName() + ",\n\n"
                            + "Welcome to KaracaBank! We're thrilled to have you on board.\n\n"
                            + "KaracaBank is here to offer you a seamless and hassle-free banking experience."
                            + "With a commitment to security, convenience,\n and your financial well-being,"
                            + "we're dedicated to meeting your banking needs every step of the way.\n\n"
                            + "Explore our wide range of financial solutions, personalized services, and a team that's always ready to assist you.\n\n"
                            + "Thank you for choosing KaracaBank. Let's make your banking experience remarkable together!\n\n"
                            + "Warm regards,\n\nKaracaBank Team"
            );

    }
    @AfterReturning(value = "execution(* com.karaca.karacabank.service.CustomerService.updateCustomer(..))",returning = "responseEntity")
    public void sendEmailUpdateCustomer(ResponseEntity<Customer> responseEntity) {
        Customer customer=responseEntity.getBody();
        assert customer != null;
        emailService.sendEmail(customer.getCustomerEmail(),
                "Customer Info Updated",
                "Dear " + customer.getCustomerName() + ",\n\n"
                        + "Your personal information has been updated."
                        + "If you are unaware of this change please contact us.\n\nKaracaBank Team");

    }
    @Before(value = "execution(* com.karaca.karacabank.service.CustomerService.deleteCustomerById(..))&&args(customerId)")
    public void sendEmailDeleteCustomer(Integer customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isEmpty()) {
            throw new CustomerIdNotFoundException("Customer with Id:" + customerId + " not found.");
        }
        Customer customer=optionalCustomer.get();
        emailService.sendEmail(customer.getCustomerEmail(),
                "Customer Deleted",
                "Dear "+customer.getCustomerName()+",\n\n"
                        + "We are sorry to see you go. We hope to see you again!\n\n"
                        +"If you are unaware of this change please contact us.\n\nKaracaBank Team");
    }
}
