package com.digitalbank.customerservice.service;

import com.digitalbank.customerservice.dto.CustomerCreatedResponse;
import com.digitalbank.customerservice.dto.CustomerRequest;
import com.digitalbank.customerservice.mapper.CustomerMapper;
import com.digitalbank.customerservice.model.Customer;
import com.digitalbank.customerservice.model.KycStatus;
import com.digitalbank.customerservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    public CustomerCreatedResponse create(CustomerRequest request) {

        Customer entity = mapper.toEntity(request);
        entity.setActive(false);
        entity.setKycStatus(KycStatus.PENDING);
        entity.setRequestFingerprint("abc");
        Customer saved = repository.saveAndFlush(entity);

        return mapper.toCreateResponse(saved);
    }
}
