package integration;

import com.tosDev.jpa.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

@RealServerIT
@Sql({"/sql/equip.sql"})
@RequiredArgsConstructor
public class EquipControllerRealServerIT extends IntegrationTestBase {

    @Test
    void test() {
        //todo:добавить куки, чтобы хранить авторизацию, сделать logout по таймеру
        //todo:добавить zone в address
        //todo: вставить formatter money там, где это подходит, то же самое с link и т.п
        //todo: реализовать свободный выбор статусов и  типов и т.п с помощью самосборного списка
        //todo: Сделать расходы и переходить к телеге, чтобы не тронуться умом
        //Придумать, как реализовать zones.
        //Оставляем сервер работать
        while (true){}
    }
}
