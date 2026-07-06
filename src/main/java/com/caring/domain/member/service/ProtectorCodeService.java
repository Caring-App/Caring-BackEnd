package com.caring.domain.member.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProtectorCodeService {
    public String generateCode(){
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }
}
