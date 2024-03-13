package com.tosDev.tg.db;

import com.pengrad.telegrambot.TelegramBot;
import com.tosDev.tg.bot_services.BrigadierWorkerCommonTgMethods;
import com.tosDev.web.jpa.entity.*;
import com.tosDev.web.jpa.repository.AddressRepository;
import com.tosDev.web.jpa.repository.BrigadierRepository;
import com.tosDev.web.jpa.repository.ShiftRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.tosDev.tg.bot.enums.ShiftStatusEnum.*;

@Component
@Transactional
@Slf4j
public class BrigadierTgQueries extends BrigadierWorkerCommonTgMethods {

    private final BrigadierRepository brigadierRepository;
    private final AddressRepository addressRepository;
    private final ShiftRepository shiftRepository;

    @Autowired
    public BrigadierTgQueries(TelegramBot bot, TgQueries tgQueries,
                              BrigadierRepository brigadierRepository,
                              AddressRepository addressRepository,
                              ShiftRepository shiftRepository) {
        super(bot, tgQueries);
        this.brigadierRepository = brigadierRepository;
        this.addressRepository = addressRepository;
        this.shiftRepository = shiftRepository;
    }

    public Brigadier linkChatIdToExistingBrigadier(Long brigadierPhoneNumber, Long chatId){
        Brigadier brigadier =
                brigadierRepository.findByPhoneNumber(brigadierPhoneNumber).orElseThrow();
        brigadier.setChatId(chatId);
        brigadierRepository.save(brigadier);
        log.info("Привязали новый чат {} пользователя {} в роли бригадира",
                chatId,brigadierPhoneNumber);
        return brigadier;
    }

    public List<Address> loadBrigadierAvailableAddressList(Integer brigadierId) {
        List<Address> resultList = new ArrayList<>();
        String name = "";
        try {
            Brigadier brigadier = brigadierRepository.findById(brigadierId).orElseThrow();
            name = brigadier.getName();
            resultList = brigadier
                    .getBrigadierAddressList()
                    .stream()
                    .map(BrigadierAddress::getAddress)
                    .toList();
        } catch (NoSuchElementException e) {
            log.error("При загрузке объектов для бригадира id {} оказался некорректным",brigadierId);
        }
        log.info("Успешно загрузили для бригадира {} список адресов {} ",name,resultList);
        return resultList;
    }


    public boolean loadFreshBrigadierShift(String addressId,Integer brigadierId){

        Address chosenAddress = new Address();
        Brigadier brigadier = new Brigadier();
        try {
            chosenAddress = addressRepository.findById(Integer.valueOf(addressId)).orElseThrow();
            brigadier = brigadierRepository.findById(brigadierId).orElseThrow();
            //Проверка на наличие активной смены
            if (shiftRepository.existsByBrigadierAndStatus(brigadier, AT_WORK.getDescription()))
            {
                return false;
            }
        } catch (NoSuchElementException e) {
            log.error("Ошибка поиска бригадира или адреса при начале смены бригадира.");
            e.printStackTrace();
        }
        String shortInfo = String.format("""
                Бригадир %s начал работу на %s
                """,brigadier.getName(),chosenAddress.getShortName());

        Shift shift = shiftRepository.save(Shift
                .builder()
                .brigadier(brigadier)
                .shortInfo(shortInfo)
                .address(chosenAddress)
                .status(AT_WORK.getDescription())
                .startDateTime(LocalDateTime.now())
                .build());
        log.info("Смена бригадира {} загружена в базу данных",shift);

        return true;
    }

    public Shift saveFinishedShift(Integer brigadierId,String callbackData){
        try {
            Brigadier brigadier = brigadierRepository.findById(brigadierId).orElseThrow();
            Shift shift = shiftRepository
                            .findByBrigadierAndStatus(brigadier,AT_WORK.getDescription())
                            .orElseThrow();

            String shortInfo = String.format("""
                Бригадир %s закончил работу на %s тип: %s
                """,brigadier.getName(),shift.getAddress().getShortName(),callbackData);

            shift.setShortInfo(shortInfo);
            shift.setEndDateTime(LocalDateTime.now());
            shift.setStatus(FINISHED.getDescription());
            shift.setType(callbackData);

            shiftRepository.save(shift);
            log.info("Успешно обновили смену {} после ее окончания бригадиром {}",shift,brigadier);
            return shift;
        } catch (NoSuchElementException e) {
            log.error("При поиске единственной открытой смены у бригадира {} произошла ошибка" +
                            "или при поиске самого бригадира по id произошла ошибка",
                    brigadierId);
            e.printStackTrace();
            return null;
        }
    }

    public Optional<Shift> approveShift(Integer brigadierId, Integer shiftId) {
        try {
            Shift shift = shiftRepository.findById(shiftId).orElseThrow();
            //Если смену уже подтвердили или она по какой-то причине не окончена
            if (!shift.getStatus().equals(FINISHED.getDescription())){
                log.warn("Смена {} уже подтверждена",shift.getShortInfo());
                return Optional.empty();
            }
            Brigadier brigadier = brigadierRepository.findById(brigadierId).orElseThrow();
            shift.setStatus(APPROVED.getDescription());
            shift.setBrigadier(brigadier);
            String totalHours = countTotalHours(shift.getStartDateTime(), shift.getEndDateTime());
            shift.setTotalHours(Float.valueOf(totalHours));
            //todo: посчитать зп

            shiftRepository.save(shift);
            log.info("Смена {}, подтвержденная бригадиром {}, обновлена в бд",
                    shift.getShortInfo(), brigadier.getName());
            return Optional.of(shift);
        } catch (NoSuchElementException e) {
            log.error("Не найдена смена {} или бригадир {} при подтверждении смены",
                    shiftId,brigadierId);
            return Optional.empty();
        }
    }

    public Optional<Shift> handleChangeOfJob(Integer shiftId,Integer brigadierId){
        try {
            Brigadier brigadier = brigadierRepository.findById(brigadierId).orElseThrow();
            Shift shift = shiftRepository.findById(shiftId).orElseThrow();
            log.info("Бригадир {} решил сменить профессию у смены {}",
                    brigadier.getName(),shift.getShortInfo());
            //todo:Сделать таблицу AddressJob и оттуда подгружать профессии, доступные на смене
        }
    }
}
