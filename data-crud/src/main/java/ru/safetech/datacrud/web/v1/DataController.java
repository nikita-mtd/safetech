package ru.safetech.datacrud.web.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.safetech.datacrud.service.DataService;

@RequestMapping("v1/data")
@RestController
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;

    @GetMapping("/check")
    public Mono<Object> generateAndVerify() {

    }
}
