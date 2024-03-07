package com.tosDev.tg.db;

import com.tosDev.jpa.entity.Admin;
import com.tosDev.jpa.entity.Brigadier;
import com.tosDev.jpa.entity.Responsible;
import com.tosDev.jpa.entity.Worker;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HqlQueries {
    private final EntityManager entityManager;


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
}
