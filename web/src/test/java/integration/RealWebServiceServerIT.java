package integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

@RealServerIT
@Sql({"/sql/test.sql"})
@RequiredArgsConstructor
public class RealWebServiceServerIT extends IntegrationTestBase {

    @Test
    void test() {
        //todo:добавить куки, чтобы хранить авторизацию, сделать logout по таймеру
        //todo:добавить zone в address
        //todo: вставить formatter money там, где это подходит, то же самое с link и т.п
        //todo: реализовать свободный выбор статусов и  типов и т.п с помощью самосборного списка
        //Придумать, как реализовать zones.
        //Оставляем сервер работать
        while (true){}
    }
}
