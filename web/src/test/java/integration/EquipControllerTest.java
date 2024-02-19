package integration;

import com.tosDev.ApplicationRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

@IT
@ContextConfiguration(classes = ApplicationRunner.class)
public class EquipControllerTest extends IntegrationTestBase {
    @Test
    void test(){
        Assertions.assertTrue(true);
        System.out.println();
    }
}
