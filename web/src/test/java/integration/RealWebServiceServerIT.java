package integration;

import com.google.api.services.drive.Drive;
import com.tosDev.web.spring.config.DriveConfig;
import com.tosDev.web.spring.config.RabbitConfig;
import com.tosDev.web.spring.web.service.rabbit.TgPictureConsumer;
import integration.config.IntegrationTestBase;
import integration.config.RealServerIT;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@RealServerIT
@Sql({"/sql/test.sql"})
@TestPropertySource(properties = {"spring.liquibase.enabled=false"})
@RequiredArgsConstructor
public class RealWebServiceServerIT extends IntegrationTestBase {

    @MockBean
    private DriveConfig driveConfig;
    @MockBean
    private Drive drive;
    @MockBean
    private TgPictureConsumer tgPictureConsumer;
    @MockBean
    private RabbitConfig rabbitConfig;

    @Test
    void test() {
        //Оставляем сервер работать
        while (true){}
    }
}
