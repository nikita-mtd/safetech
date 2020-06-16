package ru.safetech.datacrud.domain;

import lombok.Data;

@Data
public class RandomData {
    private byte[] message;
    private byte[] signature;
}
