package integration;

import com.tosDev.jpa.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

@RealServerIT
@Sql({"/sql/equip.sql"})
@RequiredArgsConstructor
public class EquipControllerRealServerIT extends IntegrationTestBase {

    private final EquipmentRepository equipmentRepository;
    @Test
    void test() throws InterruptedException {
        //todo:добавить куки, чтобы хранить авторизацию, сделать logout по таймеру
        //todo: страница адресов  - довести до ума, сделать все красиво
        //todo: вставить крестик в меню назначения бригадиров и работников в адресах
        //todo: Реализовать смену бригадиров и работников на адресах с помощью меню и перетскивания рядов в отдельных таблицах
        //todo: вставить formatter money там, где это подходит, то же самое с link и т.п
        //Придумать, как реализовать zones.
        //Оставляем сервер работать
        while (true){}
    }
}
