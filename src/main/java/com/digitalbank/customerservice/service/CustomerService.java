package com.digitalbank.customerservice.service;

import com.commons.dto.*;
import com.commons.exception.ConflictException;
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
        String fp = Fingerprints.customerCreate(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPhone(), request.getAddress());

        //Check: same externalId already present?
        Optional<Customer> byExt = repository.findByExternalId(externalId);
        if(byExt.isPresent()){
            Customer ex = byExt.get();
            if(fp.equals(ex.getRequestFingerprint())){
                return mapper.toCreateResponse(ex);
            }
            throw new ConflictException("Same externalId used with different data");
        }

        Customer entity = mapper.toEntity(request);
        entity.setActive(false);
        entity.setKycStatus(KycStatus.PENDING);
        entity.setRequestFingerprint(fp);
        Customer saved = repository.saveAndFlush(entity);

        return mapper.toCreateResponse(saved);
    }
}
