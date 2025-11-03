package com.digitalbank.customerservice.controller;

import com.digitalbank.customerservice.dto.CustomerCreatedResponse;
import com.digitalbank.customerservice.dto.CustomerRequest;
import com.digitalbank.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping("/customers")
    public ResponseEntity<CustomerCreatedResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {

        CustomerCreatedResponse body = service.create(request);
        URI loc = URI.create("/api/v1/customers/" + body.getExternalId());

        return ResponseEntity.created(loc).body(body);
    }
}
