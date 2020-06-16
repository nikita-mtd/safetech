package ru.safetech.datacrud.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class DataService {

    private static int BYTES_COUNT = 25_000;

    private final VerifyService verifyService;

    private Random random = new Random();

    public Mono<Object> getData() {
        var data = generateRandomBytes();

        return
    }

    private byte[] generateRandomBytes() {
        var container = new byte[BYTES_COUNT];
        random.nextBytes(container);
        return container;
    }
}
