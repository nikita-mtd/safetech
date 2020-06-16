package signature;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import ru.safetech.signature.Application;
import ru.safetech.signature.domain.RandomData;

@SpringBootTest(classes = Application.class)
public class SignMongoDocumentTest {

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Test
    void test() {
        mongoTemplate.save(new RandomData()).subscribe();
    }

}
