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
        //Оставляем сервер работать
        while (true){}
    }
}
