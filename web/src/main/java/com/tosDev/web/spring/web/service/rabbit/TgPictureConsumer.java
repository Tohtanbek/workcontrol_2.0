package com.tosDev.web.spring.web.service.rabbit;

import com.google.api.services.drive.model.File;
import com.tosDev.amqp.PhotoShiftIdRecord;
import com.tosDev.web.dto.tg.TgPhotoDto;
import com.tosDev.web.spring.jpa.entity.main_tables.Shift;
import com.tosDev.web.spring.jpa.repository.main_tables.ShiftRepository;
import com.tosDev.web.spring.web.service.drive.DriveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class TgPictureConsumer {
    private final DriveService driveService;
    private final ShiftRepository shiftRepository;

    private final TgPictureService tgPictureService;
    private final RestTemplate restTemplate;
    private final String TG_URI_PATH_REQUEST = "https://api.telegram.org/bot";
    private final String TG_URI_DOWNLOAD = "https://api.telegram.org/file/bot";

    /**
     * Принимаем здесь мапу фото и смены на сохранение в google drive
     * @param fileIdShiftIdRecord record с id фото в чате и id смены
     */
    @RabbitListener(queues = "${rabbitmq.queue.tg}",messageConverter = "Jackson2JsonMessageConverter")
    public void consumer(PhotoShiftIdRecord fileIdShiftIdRecord){
        log.info("Получили {} из rabbitMQ очереди",fileIdShiftIdRecord);
        //Находим shift
        Shift shift = shiftRepository.findById(fileIdShiftIdRecord.shiftId()).orElseThrow();
        //Для первой фото создаем папку сначала по id shift и началу shift
        if (fileIdShiftIdRecord.isFirstPhoto()){
            StringBuilder sb = new StringBuilder();
            String shiftStartDTStr = shift.getStartDateTime().toString();
            //Для бригадира в названии папки бригадир, для работника - работник
            String folderName = shift.getWorker()!=null?
                    sb.append(shiftStartDTStr).append(" ").append(shift.getWorker().getName()).toString()
                    :
                    sb.append(shiftStartDTStr).append(" ").append(shift.getBrigadier().getName()).toString();
            File folder = driveService.createFolder(folderName).orElseThrow();

            //Загружаем ссылку на папку в смену
            tgPictureService.saveFolderLink(folder.getWebViewLink(),shift.getId());
            //id папки сохраняем в shiftDao для сохранения последующих фото
            tgPictureService.saveFolderId(folder.getId(),shift.getId());

            //Получаем здесь byte array фотографии, загрузив из тг сервера по id фотки
            try {
                Optional<byte[]> photoOpt =
                        downloadPhotoFromTgServer(fileIdShiftIdRecord.token(), fileIdShiftIdRecord.photoId());
                //Загружаем в gDrive
                String fileName = shift.getShortInfo()+" "+fileIdShiftIdRecord.photoId();
                photoOpt.ifPresentOrElse(photoArr ->
                        driveService.uploadPicture(photoArr,fileName,folder.getId()),
                        () -> log.error("Не удалось загрузить фото в гугл, проблема загрузки фото из тг"));
            } catch (Exception e) {
                log.error("Не удалось скачать фото из тг для загрузки в gdrive");
            }
        }
        //Иначе смотрим в shift id папки и сохраняем в нее очередную фотку
        else {
            //Сначала получаем id папки из shiftDao
            Optional<String> folderIdOpt = Optional.ofNullable(shift.getFolderId());
            if (folderIdOpt.isPresent()){
                String folderId = folderIdOpt.get();
                try {
                    Optional<byte[]> photoOpt =
                            downloadPhotoFromTgServer(fileIdShiftIdRecord.token(), fileIdShiftIdRecord.photoId());
                    //Загружаем в gDrive
                    String fileName = shift.getShortInfo()+" "+fileIdShiftIdRecord.photoId();
                    photoOpt.ifPresentOrElse(photoArr ->
                                    driveService.uploadPicture(photoArr,fileName,folderId),
                            () -> log.error("Не удалось загрузить фото в гугл, проблема загрузки фото из тг"));
                } catch (Exception e) {
                    log.error("Не удалось скачать фото из тг для загрузки в gdrive",e);
                }
                log.info("Загрузили фото {}",fileIdShiftIdRecord);
            }
            else {
                throw new RuntimeException("Для загрузки фото не оказалось папки");
            }
        }
    }

    private Optional<byte[]> downloadPhotoFromTgServer(String token, String fileId) throws IOException {
        //Сначала получаем путь к файлу в тг
        StringBuilder sb = new StringBuilder()
                .append(TG_URI_PATH_REQUEST)
                .append(token)
                .append("/getFile?file_id=")
                .append(fileId);
        String uri = sb.toString();
        Optional<TgPhotoDto> firstAnswer = Optional.ofNullable(restTemplate.getForObject(uri, TgPhotoDto.class));
        TgPhotoDto photoObj = firstAnswer.orElseThrow();
        if (!photoObj.isOk()){
            throw new IOException("Телеграм не вернул путь к фото при запросе");
        }
        sb.setLength(0);
        //Затем скачиваем фото по полученному пути
        sb.append(TG_URI_DOWNLOAD).append(token).append("/").append(photoObj.getResult().getFile_path());
        String fullUri = sb.toString();

        return Optional.ofNullable(restTemplate.getForObject(fullUri, byte[].class));
    }
}
