package com.tosDev.tg.db;

import com.tosDev.tg.bot.enums.ShiftStatusEnum;
import com.tosDev.web.jpa.entity.Address;
import com.tosDev.web.jpa.entity.Shift;
import com.tosDev.web.jpa.entity.Worker;
import com.tosDev.web.jpa.entity.WorkerAddress;
import com.tosDev.web.jpa.repository.AddressRepository;
import com.tosDev.web.jpa.repository.ShiftRepository;
import com.tosDev.web.jpa.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.tosDev.tg.bot.enums.ShiftStatusEnum.*;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WorkerTgQueries {

    private final WorkerRepository workerRepository;
    private final AddressRepository addressRepository;
    private final ShiftRepository shiftRepository;

    public Worker linkChatIdToExistingWorker(Long workerPhoneNumber, Long chatId){
        Worker worker =
                workerRepository.findByPhoneNumber(workerPhoneNumber).orElseThrow();
        worker.setChatId(chatId);
        workerRepository.save(worker);
        log.info("Привязали новый чат {} пользователя {} в роли работника",
                chatId,worker);
        return worker;
    }

    public List<Address> loadWorkerAvailableAddressList(Integer workerId) {
        List<Address> resultList = new ArrayList<>();
        String name = "";
        try {
            Worker worker = workerRepository.findById(workerId).orElseThrow();
            name = worker.getName();
            resultList = worker
                    .getWorkerAddressList()
                    .stream()
                    .map(WorkerAddress::getAddress)
                    .toList();
        } catch (NoSuchElementException e) {
            log.error("При загрузке объектов для работника id {} оказался некорректным",workerId);
        }
        log.info("Успешно загрузили для работника {} список адресов {} ",name,resultList);
        return resultList;
    }

    public boolean loadFreshWorkerShift(String addressId,Integer workerId){

        Address chosenAddress = new Address();
        Worker worker = new Worker();
        try {
            chosenAddress = addressRepository.findById(Integer.valueOf(addressId)).orElseThrow();
            worker = workerRepository.findById(workerId).orElseThrow();
            //Проверка на наличие активной смены
            if (shiftRepository.existsByWorkerAndStatus(worker, AT_WORK.getDescription()))
            {
                return false;
            }
        } catch (NoSuchElementException e) {
            log.error("Ошибка поиска работника или адреса при начале смены.");
            e.printStackTrace();
        }
        String shortInfo = String.format("""
                %s %s начал работу на %s
                """,worker.getJob(),worker.getName(),chosenAddress.getShortName());

        Shift shift = shiftRepository.save(Shift
                .builder()
                .shortInfo(shortInfo)
                .address(chosenAddress)
                .worker(worker)
                .job(worker.getJob())
                .status(AT_WORK.getDescription())
                .startDateTime(LocalDateTime.now())
                .build());
        log.info("Смена {} загружена в базу данных",shift);

        return true;
    }

    public Shift saveFinishedShift(Integer workerId,String callbackData){
        try {
            Worker worker = workerRepository.findById(workerId).orElseThrow();
            Shift shift = shiftRepository
                            .findByWorkerAndStatus(worker,AT_WORK.getDescription())
                            .orElseThrow();

            String shortInfo = String.format("""
                %s %s закончил работу на %s тип: %s
                """,worker.getJob(),worker.getName(),shift.getAddress().getShortName(),callbackData);

            shift.setShortInfo(shortInfo);
            shift.setEndDateTime(LocalDateTime.now());
            shift.setStatus(FINISHED.getDescription());
            shift.setType(callbackData);

            shiftRepository.save(shift);
            log.info("Успешно обновили смену {} после ее окончания работником {}",shift,worker);
            return shift;
        } catch (NoSuchElementException e) {
            log.error("При поиске единственной открытой смены у работника {} произошла ошибка" +
                            "или при поиске самого работника по id произошла ошибка",
                    workerId);
            e.printStackTrace();
            return null;
        }
    }
}
