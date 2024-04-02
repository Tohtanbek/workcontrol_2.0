package integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.tableDto.AddressDto;
import com.tosDev.spring.jpa.entity.main_tables.Address;
import com.tosDev.spring.jpa.entity.main_tables.Responsible;
import com.tosDev.spring.jpa.repository.main_tables.AddressRepository;
import com.tosDev.spring.jpa.repository.main_tables.ResponsibleBrigadierRepository;
import com.tosDev.spring.jpa.repository.main_tables.ResponsibleRepository;
import integration.config.IntegrationTestBase;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Sql({"/sql/test.sql"})
public class DataBaseIT extends IntegrationTestBase {
    private final ResponsibleBrigadierRepository responsibleBrigadierRepository;
    private final ResponsibleRepository responsibleRepository;
    private final AddressRepository addressRepository;
    private final ObjectMapper objectMapper;
    private final EntityManager entityManager;
    @Test
    @Transactional
    public void testResponsibleBrigadierMapping() throws JsonProcessingException {
        var list = responsibleRepository.findAll();
        Map<Integer, List<String>> map = list.stream()
                .collect(Collectors.toMap(Responsible::getId,
                        responsible -> responsible.getResponsibleBrigadierList().stream()
                                .map(responsibleBrigadier -> responsibleBrigadier.getBrigadier().getName()).toList()));

        System.out.println(objectMapper.writeValueAsString(map));
    }
    @Test
    @Transactional
    public void testAddressToJsonMapping() throws JsonProcessingException {
        var addressList = addressRepository.findAll();
        List<AddressDto> dtoList = new ArrayList<>();
        for (Address dao : addressList){
            List<String> brigadierNames =
                    dao.getBrigadierAddressList()
                            .stream()
                            .map(entity -> entity.getBrigadier().getName())
                            .toList();
            List<String> workerNames =
                    dao.getWorkerAddressList()
                            .stream()
                            .map(entity -> entity.getWorker().getName())
                            .toList();
            dtoList.add(
                    AddressDto.builder()
                            .id(dao.getId())
                            .shortName(dao.getShortName())
                            .fullName(dao.getFullName())
                            .brigadiers(brigadierNames)
                            .workers(workerNames)
                            .build()
            );
        }
        System.out.println(objectMapper.writeValueAsString(dtoList));
    }
}
