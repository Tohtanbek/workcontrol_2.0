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
        /*todo:нынешняя концепция расчета зп: работникам по entity Job,
            бригадирам - по entityJob с названием по имени бригадира.
            то есть при расчете зарплаты бригадиру ищем job идентичный имени бригадира
         */
        mainListener.activateListener();
        //Оставляем сервер работать
        while (true){

        }
    }
}
