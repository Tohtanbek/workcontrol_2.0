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
        //todo: страница  - довести до ума, сделать все красиво
        //todo: вставить formatter money там, где это подходит, то же самое с link и т.п
        //Оставляем сервер работать
        while (true){}
    }
}
