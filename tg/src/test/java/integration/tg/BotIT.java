package integration.tg;

import com.pengrad.telegrambot.TelegramBot;
import com.tosDev.tg.bot.MainListener;
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
    private final TelegramBot bot;

    @Test
    void runBot(){
        try {
            mainListener.activateListener();
            while (true){
            }
        }catch (Exception e){
            bot.removeGetUpdatesListener();
        }
    }
}
