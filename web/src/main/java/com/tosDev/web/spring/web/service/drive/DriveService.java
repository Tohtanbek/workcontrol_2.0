package com.tosDev.web.spring.web.service.drive;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.tosDev.web.dto.tg.TgPhotoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class DriveService{
    private final Drive drive;

    public void uploadPicture(byte[] bytes, String name,String folderId) {
        File fileMetaData = new File();
        fileMetaData.setName(name+".jpg");
        fileMetaData.setParents(Collections.singletonList(folderId));
        ByteArrayContent byteArrayContent = new ByteArrayContent("application/octet-stream", bytes);
        try {
            File file = drive.files().create(fileMetaData,byteArrayContent)
                    .setFields("id")
                    .execute();
        } catch (IOException e) {
            log.error("Ошибка при загрузке фото в гугл диск. Фото не загружено");
        }
    }
    public Optional<File> createFolder(String folderName){
        // Создание объекта File для папки
        File folderMetadata = new File();
        folderMetadata.setName(folderName);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");

        // Вызов метода для создания папки
        try {
            File folder = drive.files().create(folderMetadata)
                    .setFields("id, webViewLink")
                    .execute();
            log.info("Папка создана. ID: {} ", folder.getId());
            return Optional.of(folder);
        } catch (IOException e) {
            log.error("Ошибка при создании папки в гугл диске",e);
        }
        return Optional.empty();
    }

}
