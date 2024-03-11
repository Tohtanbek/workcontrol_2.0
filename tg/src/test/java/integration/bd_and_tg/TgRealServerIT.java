package integration.bd_and_tg;

import com.tosDev.tg.bot.MainListener;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

@RealServerIT
@Sql({"/sql/test.sql"})
@RequiredArgsConstructor
public class TgRealServerIT extends IntegrationTestBase {

    private final MainListener mainListener;

    @Test
    void test() {
        /*todo:Делаем реализацию rabbitMQ, чтобы при отключении сайта бот мог продолжить работу и
        todo: сообщения могли просто копиться на отдельном сервисе.
         */
        mainListener.activateListener();
        //Оставляем сервер работать
        while (true){

        }
    }
}
