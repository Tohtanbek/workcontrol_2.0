package com.tosDev.tg.db;

import com.tosDev.web.enums.ShiftStatusEnum;
import com.tosDev.web.spring.jpa.entity.main_tables.*;
import com.tosDev.web.spring.jpa.repository.main_tables.AddressRepository;
import com.tosDev.web.spring.jpa.repository.main_tables.BrigadierRepository;
import com.tosDev.web.spring.jpa.repository.main_tables.ShiftRepository;
import com.tosDev.web.spring.jpa.repository.main_tables.WorkerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.tosDev.web.enums.ShiftStatusEnum.*;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TgQueries {
    private final EntityManager entityManager;
    private final AddressRepository addressRepository;
    private final BrigadierRepository brigadierRepository;
    private final WorkerRepository workerRepository;
    private final ShiftRepository shiftRepository;


    /**
     * Метод проверяет авторизован ли данный пользователь в tg по chatId
     * @param chatId chatId полученного Update
     * @return возвращает сущность авторизованного пользователя или пустой Optional
     */
    public Optional<Object> findByChatId(Long chatId){
        Query query = entityManager.createQuery(
             "SELECT a FROM Admin a " +
                     "WHERE a.chatId = :chatId"
        ).setParameter("chatId",chatId);
        try {
            Admin admin = (Admin) query.getSingleResult();
            return Optional.of(admin);
        } catch (NoResultException e) {
            log.info("{} - не админ, ищем в работниках",chatId);
        }
        query = entityManager.createQuery(
                "SELECT w FROM Worker w "+
                        "WHERE w.chatId = :chatId"
                ).setParameter("chatId",chatId);
        try {
            Worker worker = (Worker) query.getSingleResult();
            return Optional.of(worker);
        } catch (NoResultException e) {
            log.info("{} - не работник, ищем в бригадирах",chatId);
        }
        query = entityManager.createQuery(
                "SELECT b FROM Brigadier b "+
                        "WHERE b.chatId = :chatId"
        ).setParameter("chatId",chatId);
        try {
            Brigadier brigadier = (Brigadier) query.getSingleResult();
            return Optional.of(brigadier);
        } catch (NoResultException e) {
            log.info("{} - не бригадир, ищем в супервайзерах",chatId);
        }
        query = entityManager.createQuery(
                "SELECT r FROM Responsible r "+
                        "WHERE r.chatId = :chatId"
        ).setParameter("chatId",chatId);
        try {
            Responsible responsible = (Responsible) query.getSingleResult();
            return Optional.of(responsible);
        } catch (NoResultException e) {
            log.info("{} - не ответственный, такой chatId не авторизован",chatId);
        }
        return Optional.empty();
    }
    /**
     * Метод проверяет, существует ли данный номер телефона в базе данных
     * @param phoneNumber  телефон полученный из запроса на авторизацию
     * @return возвращает сущность авторизованного пользователя или пустой Optional
     */
    public Optional<Object> findByPhoneNumber(Long phoneNumber){
        Query query = entityManager.createQuery(
                "SELECT a FROM Admin a " +
                        "WHERE a.phoneNumber = :phoneNumber"
        ).setParameter("phoneNumber",phoneNumber);
        try {
            Admin admin = (Admin) query.getSingleResult();
            return Optional.of(admin);
        } catch (NoResultException e) {
            log.info("{} - не админ, ищем в работниках",phoneNumber);
        }
        query = entityManager.createQuery(
                "SELECT w FROM Worker w "+
                        "WHERE w.phoneNumber = :phoneNumber"
        ).setParameter("phoneNumber",phoneNumber);
        try {
            Worker worker = (Worker) query.getSingleResult();
            return Optional.of(worker);
        } catch (NoResultException e) {
            log.info("{} - не работник, ищем в бригадирах",phoneNumber);
        }
        query = entityManager.createQuery(
                "SELECT b FROM Brigadier b "+
                        "WHERE b.phoneNumber = :phoneNumber"
        ).setParameter("phoneNumber",phoneNumber);
        try {
            Brigadier brigadier = (Brigadier) query.getSingleResult();
            return Optional.of(brigadier);
        } catch (NoResultException e) {
            log.info("{} - не бригадир, ищем в супервайзерах",phoneNumber);
        }
        query = entityManager.createQuery(
                "SELECT r FROM Responsible r "+
                        "WHERE r.phoneNumber = :phoneNumber"
        ).setParameter("phoneNumber",phoneNumber);
        try {
            Responsible responsible = (Responsible) query.getSingleResult();
            return Optional.of(responsible);
        } catch (NoResultException e) {
            log.info("{} - не ответственный, такой phoneNumber не существует в бд",phoneNumber);
        }
        return Optional.empty();
    }

    public String checkAddressNameById(String id){
        String shortName = "";
        try {
            shortName = addressRepository.findById(Integer.valueOf(id)).orElseThrow().getShortName();
        } catch (NoSuchElementException e) {
            log.error("Не найден адрес по этому id {}",id);
        }
        return shortName;
    }

    public List<Brigadier> findBrigsWithChatIdOnShiftAddress(Shift shift) {

        return shift
                .getAddress()
                .getBrigadierAddressList()
                .stream()
                .map(BrigadierAddress::getBrigadier)
                .filter(brigadier -> brigadier.getChatId()!=null)
                .toList();
    }
    public List<Responsible> findAllAuthorizedResponsibleOfShift(Shift shift) {

        List<Integer> brigadierIds = shift
                .getAddress()
                .getBrigadierAddressList()
                .stream()
                .map(BrigadierAddress::getBrigadier)
                .filter(brigadier -> brigadier.getChatId()!=null)
                .map(Brigadier::getId)
                .toList();

        List<Responsible> responsibleList = new ArrayList<>();

        for (Brigadier brigadier : brigadierRepository.findAllById(brigadierIds)){
           responsibleList.addAll(
                   brigadier.getResponsibleBrigadierList()
                    .stream()
                    .map(ResponsibleBrigadier::getResponsible)
                    .filter(responsible -> responsible.getChatId()!=null)
                    .toList());
        }
        return responsibleList;
    }

    public void setEntityReadyToSendPhoto(Class<?> clazz, Integer id){
        if (clazz.equals(Worker.class)){
            Worker worker = workerRepository.findById(id).orElseThrow();
            worker.setReadyToSendPhoto(true);
            workerRepository.save(worker);
            log.info("Чат открыл флаг, чтобы принять фото работника {}",worker);
        }
        else if (clazz.equals(Brigadier.class)){
            Brigadier brigadier = brigadierRepository.findById(id).orElseThrow();
            brigadier.setReadyToSendPhoto(true);
            brigadierRepository.save(brigadier);
            log.info("Чат открыл флаг, чтобы принять фото бригадира {}",brigadier);
        }
    }

    public boolean checkEntityReadyForPhoto(Class<?> clazz, Integer id){
        boolean isEntityReadyToSendPhoto = false;
        if (clazz.equals(Worker.class)){
            Worker worker = workerRepository.findById(id).orElseThrow();
            isEntityReadyToSendPhoto =  worker.isReadyToSendPhoto();
        }
        else if (clazz.equals(Brigadier.class)){
            Brigadier brigadier = brigadierRepository.findById(id).orElseThrow();
            isEntityReadyToSendPhoto =  brigadier.isReadyToSendPhoto();
        }
        return isEntityReadyToSendPhoto;
    }

    //Если это первое фото в очереди, то возвращаем true и ставим флаг в бд на смену.
    public boolean setShiftFirstPhotoReceived(Class<?> clazz, Integer id){
        boolean isItFirstPhoto = true;
        if (clazz.equals(Worker.class)){
            Shift shift = shiftRepository
                    .findByWorkerIdAndStatus(id, AT_WORK).orElseThrow();
            if (!shift.isFirstPhotoSent()) {
                shift.setFirstPhotoSent(true);
                shiftRepository.save(shift);
            }
            else {
                isItFirstPhoto = false;
            }
        }
        else if (clazz.equals(Brigadier.class)){
            Shift shift = shiftRepository
                    .findByBrigadierIdAndStatus(id, AT_WORK).orElseThrow();
            if (!shift.isFirstPhotoSent()) {
                shift.setFirstPhotoSent(true);
                shiftRepository.save(shift);
            }
            else {
                isItFirstPhoto = false;
            }
        }
        return isItFirstPhoto;
    }

    public Shift findShiftByEntityId(Integer id, Class<?> clazz){
        if (clazz.equals(Worker.class)){
            return shiftRepository.findByWorkerIdAndStatus(id, AT_WORK).orElseThrow();
        }
        else if (clazz.equals(Brigadier.class)){
            return shiftRepository.findByBrigadierIdAndStatus(id, AT_WORK).orElseThrow();
        }
        else {
            throw new RuntimeException("Ошибка загрузки смены из бд при сохранении фото");
        }
    }

}
