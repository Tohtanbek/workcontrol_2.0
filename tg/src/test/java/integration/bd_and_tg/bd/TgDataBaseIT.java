package integration.bd_and_tg.bd;

import com.tosDev.tg.db.TgQueries;
import com.tosDev.web.spring.jpa.entity.main_tables.Admin;
import com.tosDev.web.spring.jpa.entity.main_tables.Brigadier;
import com.tosDev.web.spring.jpa.entity.main_tables.Responsible;
import com.tosDev.web.spring.jpa.entity.main_tables.Worker;
import com.tosDev.web.spring.jpa.repository.main_tables.AdminRepository;
import com.tosDev.web.spring.jpa.repository.main_tables.BrigadierRepository;
import com.tosDev.web.spring.jpa.repository.main_tables.ResponsibleRepository;
import com.tosDev.web.spring.jpa.repository.main_tables.WorkerRepository;
import integration.bd_and_tg.IntegrationTestBase;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Sql({"/sql/test.sql"})
public class TgDataBaseIT extends IntegrationTestBase {

    private final EntityManager entityManager;
    private final AdminRepository adminRepository;
    private final WorkerRepository workerRepository;
    private final BrigadierRepository brigadierRepository;
    private final ResponsibleRepository responsibleRepository;
    private final TgQueries tgQueries;
    @Test
    @Transactional
    void checkByChatIdTestAdmin(){
        Admin admin = adminRepository.findById(1).get();
        admin.setChatId(12345L);
        adminRepository.saveAndFlush(admin);
        var startMillis = System.currentTimeMillis();
        var result = tgQueries.findByChatId(12345L);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("com.tosDev.jpa.entity.Admin",result.get().getClass().getName());

        if (result.isPresent()) {
            String className = result.get().getClass().getName();
            switch (className) {
                case ("com.tosDev.jpa.entity.Admin"): {
                    log.info("update от админа");
                    break;
                }
                case ("com.tosDev.jpa.entity.Worker"): {
                    log.info("update от работника");
                    break;
                }
                case ("com.tosDev.jpa.entity.Brigadier"): {
                    log.info("update от бригадира");
                    break;
                }
                case ("com.tosDev.jpa.entity.Supervisor"): {
                    log.info("update от супервайзера");
                    break;
                }
                default: {
                    log.error("update от неправильного класса");
                    throw new RuntimeException();
                }
            }
        }
        var endTimeMillis = System.currentTimeMillis();
        log.info("Время выполнения: {}",endTimeMillis-startMillis);
    }
    @Test
    @Transactional
    void checkByChatIdTestWorker(){
        Worker worker = workerRepository.findById(1).get();
        worker.setChatId(12345L);
        workerRepository.saveAndFlush(worker);
        var startMillis = System.currentTimeMillis();
        var result = tgQueries.findByChatId(12345L);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("com.tosDev.jpa.entity.Worker",result.get().getClass().getName());

        if (result.isPresent()) {
            String className = result.get().getClass().getName();
            switch (className) {
                case ("com.tosDev.jpa.entity.Admin"): {
                    log.info("update от админа");
                    break;
                }
                case ("com.tosDev.jpa.entity.Worker"): {
                    log.info("update от работника");
                    break;
                }
                case ("com.tosDev.jpa.entity.Brigadier"): {
                    log.info("update от бригадира");
                    break;
                }
                case ("com.tosDev.jpa.entity.Supervisor"): {
                    log.info("update от супервайзера");
                    break;
                }
                default: {
                    log.error("update от неправильного класса");
                    throw new RuntimeException();
                }
            }
        }
        var endTimeMillis = System.currentTimeMillis();
        log.info("Время выполнения: {}",endTimeMillis-startMillis);
    }

    @Test
    @Transactional
    void checkByChatIdTestBrigadier(){
        Brigadier brigadier = brigadierRepository.findById(1).get();
        brigadier.setChatId(12345L);
        brigadierRepository.saveAndFlush(brigadier);
        var startMillis = System.currentTimeMillis();
        var result = tgQueries.findByChatId(12345L);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("com.tosDev.jpa.entity.Brigadier",result.get().getClass().getName());

        if (result.isPresent()) {
            String className = result.get().getClass().getName();
            switch (className) {
                case ("com.tosDev.jpa.entity.Admin"): {
                    log.info("update от админа");
                    break;
                }
                case ("com.tosDev.jpa.entity.Worker"): {
                    log.info("update от работника");
                    break;
                }
                case ("com.tosDev.jpa.entity.Brigadier"): {
                    log.info("update от бригадира");
                    break;
                }
                case ("com.tosDev.jpa.entity.Supervisor"): {
                    log.info("update от супервайзера");
                    break;
                }
                default: {
                    log.error("update от неправильного класса");
                    throw new RuntimeException();
                }
            }
        }
        var endTimeMillis = System.currentTimeMillis();
        log.info("Время выполнения: {}",endTimeMillis-startMillis);
    }
    @Test
    @Transactional
    void checkByChatIdTestResponsible(){
        Responsible responsible = responsibleRepository.findById(1).get();
        responsible.setChatId(12345L);
        responsibleRepository.saveAndFlush(responsible);
        var startMillis = System.currentTimeMillis();
        var result = tgQueries.findByChatId(12345L);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("com.tosDev.jpa.entity.Responsible",result.get().getClass().getName());

        if (result.isPresent()) {
            String className = result.get().getClass().getName();
            switch (className) {
                case ("com.tosDev.jpa.entity.Admin"): {
                    log.info("update от админа");
                    break;
                }
                case ("com.tosDev.jpa.entity.Worker"): {
                    log.info("update от работника");
                    break;
                }
                case ("com.tosDev.jpa.entity.Brigadier"): {
                    log.info("update от бригадира");
                    break;
                }
                case ("com.tosDev.jpa.entity.Responsible"): {
                    log.info("update от супервайзера");
                    break;
                }
                default: {
                    log.error("update от неправильного класса");
                    throw new RuntimeException();
                }
            }
        }
        var endTimeMillis = System.currentTimeMillis();
        log.info("Время выполнения: {}",endTimeMillis-startMillis);
    }
    @Test
    @Transactional
    void checkByChatIdTestNull(){
        var startMillis = System.currentTimeMillis();
        var result = tgQueries.findByChatId(12345L);
        Assertions.assertFalse(result.isPresent());
                    if (result.isPresent()) {
                        String className = result.get().getClass().getName();
                        switch (className) {
                            case ("com.tosDev.jpa.entity.Admin"): {
                                log.info("update от админа");
                                break;
                            }
                            case ("com.tosDev.jpa.entity.Worker"): {
                                log.info("update от работника");
                                break;
                            }
                            case ("com.tosDev.jpa.entity.Brigadier"): {
                                log.info("update от бригадира");
                                break;
                            }
                            case ("com.tosDev.jpa.entity.Supervisor"): {
                                log.info("update от супервайзера");
                                break;
                            }
                            default: {
                                log.error("update от неправильного класса");
                                throw new RuntimeException();
                            }
                        }
                    }
        var endTimeMillis = System.currentTimeMillis();
        log.info("Время выполнения: {}",endTimeMillis-startMillis);

    }

}
