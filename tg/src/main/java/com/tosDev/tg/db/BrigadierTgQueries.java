package com.tosDev.tg.db;

import com.pengrad.telegrambot.TelegramBot;
import com.tosDev.amqp.RabbitMQMessageProducer;
import com.tosDev.web.enums.ShiftEndTypeEnum;
import com.tosDev.web.enums.ShiftStatusEnum;
import com.tosDev.web.spring.jpa.entity.main_tables.*;
import com.tosDev.web.spring.jpa.repository.main_tables.*;
import com.tosDev.tg.bot_services.BrigadierWorkerCommonTgMethods;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Transactional
@Slf4j
public class BrigadierTgQueries extends BrigadierWorkerCommonTgMethods {

    private final BrigadierRepository brigadierRepository;
    private final AddressRepository addressRepository;
    private final ShiftRepository shiftRepository;
    private final JobRepository jobRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final DateTimeFormatter tgDateTimeFormatter;

    @Autowired
    public BrigadierTgQueries(TelegramBot bot, TgQueries tgQueries,
                              BrigadierRepository brigadierRepository,
                              AddressRepository addressRepository,
                              DateTimeFormatter tgDateTimeFormatter,
                              ShiftRepository shiftRepository,
                              JobRepository jobRepository,
                              ExpenseRepository expenseRepository,
                              IncomeRepository incomeRepository,
                              DateTimeFormatter tgDateTimeFormatter1,
                              AdminTgQueries adminTgQueries,
                              RabbitMQMessageProducer rabbitMQMessageProducer) {
        super(bot, tgQueries,tgDateTimeFormatter,adminTgQueries, rabbitMQMessageProducer);
        this.brigadierRepository = brigadierRepository;
        this.addressRepository = addressRepository;
        this.shiftRepository = shiftRepository;
        this.jobRepository = jobRepository;
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
        this.tgDateTimeFormatter = tgDateTimeFormatter1;
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


    public Optional<Shift> loadFreshBrigadierShift(String addressId, Integer brigadierId){

        Address chosenAddress = new Address();
        Brigadier brigadier = new Brigadier();
        try {
            chosenAddress = addressRepository.findById(Integer.valueOf(addressId)).orElseThrow();
            brigadier = brigadierRepository.findById(brigadierId).orElseThrow();
            //Проверка на наличие активной смены
            if (shiftRepository.existsByBrigadierAndStatus(brigadier, ShiftStatusEnum.AT_WORK))
            {
                return Optional.empty();
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
                .status(ShiftStatusEnum.AT_WORK)
                .firstPhotoSent(false)
                .startDateTime(LocalDateTime.now())
                .build());
        //Инициализируем, чтобы потом найти супервайзеров для переправки
        Hibernate.initialize(shift.getBrigadier().getResponsibleBrigadierList());
        log.info("Смена бригадира {} загружена в базу данных",shift);

        return Optional.of(shift);
    }

    public Shift saveFinishedShift(Integer brigadierId,String callbackData){
        try {
            Brigadier brigadier = brigadierRepository.findById(brigadierId).orElseThrow();
            Shift shift = shiftRepository
                            .findByBrigadierIdAndStatus(brigadierId, ShiftStatusEnum.AT_WORK)
                            .orElseThrow();

            String shortInfo = String.format("""
                Бригадир %s закончил работу на %s \n
                тип: %s
                """,brigadier.getName(),shift.getAddress().getShortName(),callbackData);

            ShiftEndTypeEnum shiftEndTypeEnum = Arrays.stream(ShiftEndTypeEnum.values())
                    .filter(value -> value.getDescription().equals(callbackData))
                            .findFirst().orElseThrow();
            shift.setShortInfo(shortInfo);
            shift.setEndDateTime(LocalDateTime.now());
            shift.setStatus(ShiftStatusEnum.FINISHED);
            shift.setType(shiftEndTypeEnum);
            String totalHours = countTotalHours(shift.getStartDateTime(), shift.getEndDateTime());
            shift.setTotalHours(Float.valueOf(totalHours));

            shiftRepository.save(shift);
            //Инициализируем, чтобы потом найти супервайзеров для переправки
            Hibernate.initialize(shift.getBrigadier().getResponsibleBrigadierList());
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
            if (!shift.getStatus().equals(ShiftStatusEnum.FINISHED)){
                log.warn("Смена {} уже подтверждена",shift.getShortInfo());
                return Optional.empty();
            }
            Brigadier brigadier = brigadierRepository.findById(brigadierId).orElseThrow();
            shift.setStatus(ShiftStatusEnum.APPROVED);
            shift.setBrigadier(brigadier);
            String totalHours = countTotalHours(shift.getStartDateTime(), shift.getEndDateTime());
            shift.setTotalHours(Float.valueOf(totalHours));

            shiftRepository.save(shift);
            log.info("Смена {}, подтвержденная бригадиром {}, обновлена в бд",
                    shift.getShortInfo(), brigadier.getName());
            //Инициализируем для последующей работы с сущностью
            Hibernate.initialize(shift.getAddress().getBrigadierAddressList());
            Hibernate.initialize(shift.getBrigadier().getResponsibleBrigadierList());
            return Optional.of(shift);
        } catch (NoSuchElementException e) {
            log.error("Не найдена смена {} или бригадир {} при подтверждении смены",
                    shiftId,brigadierId);
            return Optional.empty();
        }
    }

    public List<Job> loadJobsOfAddress(Integer shiftId, Integer brigadierId){
        try {
            Brigadier brigadier = brigadierRepository.findById(brigadierId).orElseThrow();
            Shift shift = shiftRepository.findById(shiftId).orElseThrow();
            log.info("Бригадир {} решил сменить профессию у смены {}",
                    brigadier.getName(),shift.getShortInfo());
            List<Job> jobsOfAddress = shift.getAddress().getAddressJobList()
                    .stream().map(AddressJob::getJob).toList();
            log.info("нашли следующие профессии {} для бригадира {} для смены на смене {}",
                    jobsOfAddress,brigadier.getName(),shift.getShortInfo());
            return jobsOfAddress;
        } catch (NoSuchElementException e) {
            log.error("Ошибка при поиске сущности во время " +
                    "загрузки списка профессий бригадира {}",brigadierId);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Optional<Shift> saveChangeOfJob(Integer chosenJobId,Integer chosenShiftId){
        try {
            Shift shift = shiftRepository.findById(chosenShiftId).orElseThrow();
            Job jobToSet = jobRepository.findById(chosenJobId).orElseThrow();
            shift.setJob(jobToSet);
            //Не забываем помимо job поменять shortInfo, а том там останется старая профессия
            String shortInfo = String.format("""
                %s %s закончил работу на %s \n
                тип: %s
                """,shift.getJob().getName(),
                    shift.getWorker().getName(),
                    shift.getAddress().getShortName(),
                    shift.getType());
            shift.setShortInfo(shortInfo);
            return Optional.of(shiftRepository.save(shift));
        } catch (NoSuchElementException e) {
            log.error("При смене профессии на смене {} на job {} не была найдена сущность по id",
                    chosenShiftId,chosenJobId);
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<String> countAndSaveExpense(Shift shift){
        Job jobOfShift = shift.getJob();
        Float totalHours = shift.getTotalHours();
        Optional<Float> expenseRate = Optional.ofNullable(jobOfShift.getWageRate());
        DecimalFormat df = new DecimalFormat("#.##",
                DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        if (expenseRate.isPresent()) {
            String expenseResult;
            if (jobOfShift.isHourly()) {
                 expenseResult = df.format(totalHours * expenseRate.get());
            }
            else {
                expenseResult = df.format(expenseRate.get());
            }
            Expense expense = Expense
                    .builder()
                    .address(shift.getAddress())
                    .status("Не оплачен")
                    .shift(shift)
                    .dateTime(shift.getEndDateTime())
                    .type("ЗП")
                    .worker(shift.getWorker())
                    .totalSum(Float.valueOf(expenseResult))
                    .build();
            expense.setShortInfo(generateExpenseShortInfo(expense,jobOfShift.isHourly()));
            expenseRepository.save(expense);
            log.info("Сохранили расход зарплаты {} с завершенной смены {}",
                    expense.getShortInfo(),shift.getShortInfo());
            return Optional.empty();
        }
        else {
            log.warn("Не удалось посчитать зарплату для смены {}, expenseRate {} == null ",
                    shift.getShortInfo(),jobOfShift.getName());
            return Optional.of("Ошибка при расчете зарплаты для смены "+
                    jobOfShift.getName() + " " + shift.getWorker().getName());
        }
    }

    public Optional<String> countAndSaveIncome(Shift shift){
        Job jobOfShift = shift.getJob();
        Float totalHours = shift.getTotalHours();
        Optional<Float> incomeRate = Optional.ofNullable(jobOfShift.getIncomeRate());
        DecimalFormat df = new DecimalFormat("#.##",
                DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        if (incomeRate.isPresent()) {
            String incomeResult;
            if (jobOfShift.isHourly()) {
                incomeResult = df.format(totalHours * incomeRate.get());
            }
            else {
                incomeResult = df.format(incomeRate.get());
            }
            Income income = Income
                    .builder()
                    .address(shift.getAddress())
                    .status("Не оплачен")
                    .shift(shift)
                    .type("Рабочий день")
                    .worker(shift.getWorker())
                    .totalSum(Float.valueOf(incomeResult))
                    .build();
            income.setShortInfo(generateIncomeShortInfo(income,jobOfShift.isHourly()));
            incomeRepository.save(income);
            log.info("Сохранили счет за работу {} с завершенной смены {}",
                    income.getShortInfo(),shift.getShortInfo());
            return Optional.empty();
        }
        else {
            log.warn("Не удалось посчитать счет для смены {}, incomeRate {} == null ",
                    shift.getShortInfo(),jobOfShift.getName());
            return Optional.of("Ошибка при расчете счета для смены "+
                    jobOfShift.getName() + " " + shift.getWorker().getName());
        }
    }

    public Optional<String> countAndSaveBrigExpense(Shift shift){
        Brigadier brigadier = shift.getBrigadier();
        Float totalHours = shift.getTotalHours();
        Optional<Float> expenseRate = Optional.ofNullable(brigadier.getWageRate());
        DecimalFormat df = new DecimalFormat("#.##",
                DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        if (expenseRate.isPresent()) {
            String expenseResult;
            if (brigadier.isHourly()) {
                expenseResult = df.format(totalHours * expenseRate.get());
            }
            else {
                expenseResult = df.format(expenseRate.get());
            }
            Expense expense = Expense
                    .builder()
                    .address(shift.getAddress())
                    .status("Не оплачен")
                    .shift(shift)
                    .dateTime(shift.getEndDateTime())
                    .type("ЗП")
                    .totalSum(Float.valueOf(expenseResult))
                    .build();
            expense.setShortInfo(generateBrigExpenseShortInfo(expense,brigadier.isHourly()));
            expenseRepository.save(expense);
            log.info("Сохранили расход зарплаты {} с завершенной смены бригадира {}",
                    expense.getShortInfo(),shift.getShortInfo());
            return Optional.empty();
        }
        else {
            log.warn("Не удалось посчитать зарплату для смены {}, expenseRate == null ",
                    shift.getShortInfo());
            return Optional.of("Ошибка при расчете зарплаты для бригадира "+
                    brigadier.getName());
        }
    }

    public Optional<String> countAndSaveBrigIncome(Shift shift){
        Brigadier brigadier = shift.getBrigadier();
        Float totalHours = shift.getTotalHours();
        Optional<Float> incomeRate = Optional.ofNullable(brigadier.getIncomeRate());
        DecimalFormat df = new DecimalFormat("#.##",
                DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        if (incomeRate.isPresent()) {
            String incomeResult;
            if (brigadier.isHourly()) {
                incomeResult = df.format(totalHours * incomeRate.get());
            }
            else {
                incomeResult = df.format(incomeRate.get());
            }
            Income income = Income
                    .builder()
                    .address(shift.getAddress())
                    .status("Не оплачен")
                    .shift(shift)
                    .type("Рабочий день")
                    .totalSum(Float.valueOf(incomeResult))
                    .build();
            income.setShortInfo(generateBrigIncomeShortInfo(income,brigadier.isHourly()));
            incomeRepository.save(income);
            log.info("Сохранили счет за работу {} с завершенной смены {}",
                    income.getShortInfo(),shift.getShortInfo());
            return Optional.empty();
        }
        else {
            log.warn("Не удалось посчитать счет для смены бригадира {}, incomeRate  == null ",
                    shift.getShortInfo());
            return Optional.of("Ошибка при расчете счета для смены бригадира: "
                    +brigadier.getName());
        }
    }

    public List<Worker> findLinkedWorkers(Integer brigadierId){
        try {
            //1 Получаем бригадира
            Brigadier brigadier = brigadierRepository.findById(brigadierId).orElseThrow();
            //2 Получаем его адреса
            List<Address> addressListOfBrig =
                    brigadier.getBrigadierAddressList()
                    .stream()
                    .map(BrigadierAddress::getAddress)
                    .toList();
            //3 С каждого адреса получаем всех работников и записываем в финальный list
            Set<Worker> workersOfBrig = new HashSet<>();
            for (Address address : addressListOfBrig){
                address.getWorkerAddressList().stream()
                        .map(WorkerAddress::getWorker)
                        .forEach(workersOfBrig::add);
            }
            return workersOfBrig.stream().toList();
        } catch (Exception e) {
            log.error("Ошибка при поиске в бд бригадира {} или его работников",brigadierId);
            return Collections.emptyList();
        }
    }
    public List<Worker> findLinkedWorkersWorking(Integer brigadierId){
        return findLinkedWorkers(brigadierId)
                .stream()
                .filter(worker -> worker.getShiftList()
                        .stream().anyMatch(shift -> shift.getStatus().equals(ShiftStatusEnum.AT_WORK)))
                .toList();
    }

    public List<Address> findAddressListOfBrig(Integer brigadierId){
        try {
            Brigadier brigadier = brigadierRepository.findById(brigadierId).orElseThrow();
            return brigadier.getBrigadierAddressList()
                    .stream()
                    .map(BrigadierAddress::getAddress).toList();
        } catch (NoSuchElementException e) {
            log.error("Не нашли бригадира {} или его адресов в бд по команде",
                    brigadierId);
            return Collections.emptyList();
        }
    }

    private String generateExpenseShortInfo(Expense expense, boolean isHourly){
        String dateTime = tgDateTimeFormatter.format(expense.getDateTime());
        return String.format("""
                Сумма: %f
                тип : %s
                Статус: %s
                тип расчета: %s
                Работник: %s
                Дата: %s
                Адрес: %s
                """,expense.getTotalSum(),expense.getType(),expense.getStatus(),
                isHourly?"Почасово":"Сдельная",expense.getWorker().getName(),
                dateTime,expense.getAddress().getShortName());

    }
    private String generateBrigExpenseShortInfo(Expense expense, boolean isHourly){
        String brigName = expense.getShift().getBrigadier().getName();
        String dateTime = tgDateTimeFormatter.format(expense.getDateTime());
        return String.format("""
                Сумма: %f
                тип : %s
                Статус: %s
                тип расчета: %s
                Работник: бригадир %s
                Дата: %s
                Адрес: %s
                """,expense.getTotalSum(),expense.getType(),expense.getStatus(),
                isHourly?"Почасово":"Сдельная",brigName,
                dateTime,expense.getAddress().getShortName());

    }

    private String generateBrigIncomeShortInfo(Income income, boolean isHourly){
        String brigName = income.getShift().getBrigadier().getName();
        return String.format("""
                Сумма: %f
                тип : %s
                Статус: %s
                тип расчета: %s
                Работник: бригадир %s
                Адрес: %s
                """,income.getTotalSum(),income.getType(),income.getStatus(),
                isHourly?"Почасово":"Сдельная",brigName,
                income.getAddress().getShortName());

    }
    private String generateIncomeShortInfo(Income income, boolean isHourly){
        return String.format("""
                Сумма: %f
                тип : %s
                Статус: %s
                тип расчета: %s
                Работник: %s
                Адрес: %s
                """,income.getTotalSum(),income.getType(),income.getStatus(),
                isHourly?"Почасово":"Сдельная",income.getWorker().getName(),
                income.getAddress().getShortName());

    }
}
