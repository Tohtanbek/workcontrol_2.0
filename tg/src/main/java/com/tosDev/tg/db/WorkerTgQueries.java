package com.tosDev.tg.db;

import com.tosDev.web.jpa.entity.*;
import com.tosDev.web.jpa.repository.AddressRepository;
import com.tosDev.web.jpa.repository.ShiftRepository;
import com.tosDev.web.jpa.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.tosDev.tg.bot.enums.ShiftStatusEnum.AT_WORK;
import static com.tosDev.tg.bot.enums.ShiftStatusEnum.FINISHED;

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

    public Optional<Shift> loadFreshWorkerShift(String addressId,Integer workerId){

        Address chosenAddress = new Address();
        Worker worker = new Worker();
        try {
            chosenAddress = addressRepository.findById(Integer.valueOf(addressId)).orElseThrow();
            worker = workerRepository.findById(workerId).orElseThrow();
            //Проверка на наличие активной смены
            if (shiftRepository.existsByWorkerAndStatus(worker, AT_WORK.getDescription()))
            {
                return Optional.empty();
            }
        } catch (NoSuchElementException e) {
            log.error("Ошибка поиска работника или адреса при начале смены.");
            e.printStackTrace();
        }
        String shortInfo = String.format("""
                %s %s начал работу на %s
                """,worker.getJob().getName(),worker.getName(),chosenAddress.getShortName());

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

        //Инициализируем для последующей рассылки
        Hibernate.initialize(shift.getAddress().getBrigadierAddressList());

        return Optional.of(shift);
    }

    public Shift saveFinishedShift(Integer workerId,String callbackData){
        try {
            Worker worker = workerRepository.findById(workerId).orElseThrow();
            Shift shift = shiftRepository
                            .findByWorkerAndStatus(worker,AT_WORK.getDescription())
                            .orElseThrow();

            String shortInfo = String.format("""
                %s %s закончил работу на %s \n
                тип: %s
                """,worker.getJob().getName(),
                    worker.getName(),
                    shift.getAddress().getShortName(),
                    callbackData);

            shift.setShortInfo(shortInfo);
            shift.setEndDateTime(LocalDateTime.now());
            shift.setStatus(FINISHED.getDescription());
            shift.setType(callbackData);

            shiftRepository.save(shift);

            //Для последующей рассылки загружаем адреса
            Hibernate.initialize(shift.getAddress().getBrigadierAddressList());


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
