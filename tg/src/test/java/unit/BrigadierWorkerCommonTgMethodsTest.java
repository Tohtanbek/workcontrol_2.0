package unit;

import com.pengrad.telegrambot.TelegramBot;
import com.tosDev.tg.bot_services.BrigadierWorkerCommonTgMethods;
import com.tosDev.tg.db.TgQueries;
import integration.bd_and_tg.IT;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class BrigadierWorkerCommonTgMethodsTest {

    @Test
    void countTotalHours() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BrigadierWorkerCommonTgMethods mock
                = new BrigadierWorkerCommonTgMethods(mock(TelegramBot.class), mock(TgQueries.class));
        Method method =
                mock.getClass()
                        .getMethod("countTotalHours",LocalDateTime.class,LocalDateTime.class);
        var start= LocalDateTime.of(2024,3,13,12,0);
        var end= LocalDateTime.of(2024,3,13,15,30);
        String result = (String) method.invoke(mock,start,end);
        assertEquals("3.5",result);
    }
}