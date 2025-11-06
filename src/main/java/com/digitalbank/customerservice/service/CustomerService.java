package com.digitalbank.customerservice.service;

import com.commons.exception.ConflictException;
import com.commons.exception.ResourceNotFoundException;
import com.digitalbank.customerservice.dto.CustomerCreatedResponse;
import com.digitalbank.customerservice.dto.CustomerRequest;
import com.digitalbank.customerservice.mapper.CustomerMapper;
import com.digitalbank.customerservice.model.Customer;
import com.digitalbank.customerservice.model.KycStatus;
import com.digitalbank.customerservice.repository.CustomerRepository;
import com.digitalbank.customerservice.util.Fingerprints;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    public CustomerCreatedResponse create(CustomerRequest request) {

        String externalId = request.getExternalId();
        String email = request.getEmail();
        String fp = Fingerprints.customerCreate(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPhone(), request.getAddress());

        //Check: same externalId already present?
        Optional<Customer> byExt = repository.findByExternalId(externalId);
        if(byExt.isPresent()){
            Customer customer = byExt.get();
            if(fp.equals(customer.getRequestFingerprint())){
                return mapper.toCreateResponse(customer);
            }
            throw new ConflictException("Same externalId used with different data");
        }

        //Check: same email already present?
        Optional<Customer> byEmail = repository.findByEmail(email);
        if(byEmail.isPresent()){
            Customer customer = byEmail.get();
            if(fp.equals(customer.getRequestFingerprint())){
                return mapper.toCreateResponse(customer);
            }
            throw new ConflictException("Same email used with different data");
        }

        Customer entity = mapper.toEntity(request);
        entity.setActive(false);
        entity.setKycStatus(KycStatus.PENDING);
        entity.setRequestFingerprint(fp);
        Customer saved = repository.saveAndFlush(entity);

        return mapper.toCreateResponse(saved);
    }

    public Integer updateKycStatus(String id, String kycStatus) {
        Customer c = repository.findByExternalId(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found with externalId: " + id));

        if ("VERIFIED".equalsIgnoreCase(kycStatus)) {
            c.setKycStatus(KycStatus.VERIFIED);
            c.setActive(true);
            repository.save(c);
        }
        return c.getVersion();
    }
}
