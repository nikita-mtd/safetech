package ru.safetech.signature.domain;

import lombok.Data;

@Data
public class RandomData {
    private String id;
    private byte[] message;
    private byte[] signature;
}
