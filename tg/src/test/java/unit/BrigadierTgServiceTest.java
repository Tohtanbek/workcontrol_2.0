package unit;

import com.pengrad.telegrambot.TelegramBot;
import com.tosDev.amqp.RabbitMQMessageProducer;
import com.tosDev.tg.bot_services.BrigadierTgService;
import com.tosDev.tg.db.AdminTgQueries;
import com.tosDev.tg.db.BrigadierTgQueries;
import com.tosDev.tg.db.TgQueries;
import com.tosDev.web.spring.jpa.entity.main_tables.Brigadier;
import com.tosDev.web.spring.jpa.entity.main_tables.Shift;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.*;

@Slf4j
class SendOutOtherBrigsThatShiftApprovedTest {

    private BrigadierTgService brigadierTgServiceMock;

    private TelegramBot botMock;

    @Mock
    private BrigadierTgQueries brigadierTgQueriesMock;
    @Mock
    private DateTimeFormatter dateTimeFormatterMock;
    @Mock
    private AdminTgQueries adminTgQueriesMock;
    @Mock
    RabbitMQMessageProducer rabbitMQMessageProducerMock;

    private Brigadier brigMock1;
    private Brigadier brigMock2;
    private Brigadier brigMock3;

    private Method method;
    private Shift approvedShiftMock;

    @BeforeEach
    void loadClass() throws NoSuchMethodException {

        botMock = mock(TelegramBot.class);
        when(botMock.execute(any()))
                .thenAnswer(invocation -> {
                    log.info("Отправили {}",invocation.getArguments());
                    return null;
                });

        brigMock1 = mock(Brigadier.class);
        when(brigMock1.getChatId()).thenReturn(1L);
        when(brigMock1.getName()).thenReturn("Первый");

        brigMock2 = mock(Brigadier.class);
        when(brigMock2.getChatId()).thenReturn(2L);
        when(brigMock2.getName()).thenReturn("Второй");

        brigMock3 = mock(Brigadier.class);
        when(brigMock3.getChatId()).thenReturn(3L);
        when(brigMock3.getName()).thenReturn("Третий");

        approvedShiftMock = mock(Shift.class);
        when(approvedShiftMock.getShortInfo()).thenReturn("Шортинфотест");
        when(approvedShiftMock.getBrigadier()).thenReturn(brigMock1);

        TgQueries tgQueriesMock = mock(TgQueries.class);
        when(tgQueriesMock.findBrigsWithChatIdOnShiftAddress(any()))
                .thenReturn(List.of(brigMock1,brigMock2,brigMock3));

        brigadierTgServiceMock = new BrigadierTgService(
                botMock,
                brigadierTgQueriesMock,
                tgQueriesMock,
                dateTimeFormatterMock,
                adminTgQueriesMock,
                rabbitMQMessageProducerMock);
        method = brigadierTgServiceMock
                .getClass()
                .getDeclaredMethod("sendOutOtherBrigsThatShiftApproved",Shift.class,Long.class);
        method.setAccessible(true);
    }


    @Test
    void checkCountOfInvocations() throws InvocationTargetException, IllegalAccessException {
        //вызываем на первом бригадире метод по рассылке остальным уведомления о смене
        method.invoke(brigadierTgServiceMock,approvedShiftMock,1L);

        //Проверяем, что лишь двум из трех должны отправить уведомление
        verify(botMock,times(2)).execute(any());

    }
    @Test
    void checkLackOfInvocation() throws InvocationTargetException, IllegalAccessException {
        String msg = String.format("Рабочий день %s подтвержден другим бригадиром",
                approvedShiftMock.getShortInfo());
        //вызываем на первом бригадире метод по рассылке остальным уведомления о смене
        method.invoke(brigadierTgServiceMock,approvedShiftMock,1L);
        //Проверяем, что первый не был отправлен
        verify(botMock,never())
                .execute(argThat(x->x.getParameters().containsValue(1L)));

    }
    @Test
    void checkInvocation() throws InvocationTargetException, IllegalAccessException {
        String msg = String.format("Рабочий день %s подтвержден другим бригадиром",
                approvedShiftMock.getShortInfo());
        //вызываем на первом бригадире метод по рассылке остальным уведомления о смене
        method.invoke(brigadierTgServiceMock,approvedShiftMock,1L);
        //Проверяем, что второй и третий отправлены
        verify(botMock,times(1))
                .execute(argThat(x->x.getParameters().containsValue(2L)));
        verify(botMock,times(1))
                .execute(argThat(x->x.getParameters().containsValue(3L)));
    }
}