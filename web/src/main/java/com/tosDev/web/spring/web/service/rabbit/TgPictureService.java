package com.tosDev.web.spring.web.service.rabbit;

import com.tosDev.web.spring.jpa.repository.main_tables.ShiftRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TgPictureService {
    private final ShiftRepository shiftRepository;

    @Transactional
    void saveFolderLink(String folderLink,Integer shiftId){
        //Загружаем ссылку на папку в смену
        try {
            shiftRepository.updateFolderLink(shiftId,folderLink);
            log.info("Загрузили в сущность смены ссылку на папку в gDrive");
        } catch (Exception e) {
            log.error("Не удалось загрузить в сущность смены ссылку на папку с фото в gDrive",e);
        }
    }
    @Transactional
    void saveFolderId(String folderId, Integer shiftId){
        try {
            shiftRepository.updateFolderId(shiftId,folderId);
        } catch (Exception e) {
            log.error("Не удалось создать папку для фотографий смены {}",shiftId);
        }
    }
}
