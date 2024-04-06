package com.tosDev.web.spring.web.service.drive;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Service
public class DriveService{
    private final Drive drive;


    public void uploadPicture(byte[] bytes, String name,String folderId) throws IOException {
        File fileMetaData = new File();
        fileMetaData.setName(name+".jpg");
        fileMetaData.setParents(Collections.singletonList(folderId));
        ByteArrayContent byteArrayContent = new ByteArrayContent("application/octet-stream", bytes);
        File file = drive.files().create(fileMetaData,byteArrayContent)
                .setFields("id")
                .execute();
    }
    public File createFolder(String folderName) throws IOException {
        // Создание объекта File для папки
        File folderMetadata = new File();
        folderMetadata.setName(folderName);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");

        // Вызов метода для создания папки
        File folder = drive.files().create(folderMetadata)
                .setFields("id")
                .execute();
        log.info("Папка создана. ID: {} ",folder.getId());

        return folder;
    }
}
