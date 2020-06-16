package ru.safetech.signature.event;

import lombok.Value;
import ru.safetech.signature.domain.RandomData;

@Value
public class RedisChanged {
    RandomData randomData;
}
