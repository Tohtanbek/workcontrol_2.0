package integration.tg;

import com.tosDev.jpa.repository.AdminRepository;
import com.tosDev.tg.MainListener;
import integration.bd_and_tg.IT;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@IT
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@RequiredArgsConstructor
public class BotIT {

    private final MainListener mainListener;


    @Test
    void runBot(){
        mainListener.activateListener();
        while (true){}
    }
}
