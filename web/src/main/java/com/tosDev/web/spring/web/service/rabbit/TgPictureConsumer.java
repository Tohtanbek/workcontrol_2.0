package com.tosDev.web.spring.web.service.rabbit;

import com.tosDev.amqp.PhotoShiftIdRecord;
import com.tosDev.web.spring.jpa.entity.main_tables.Shift;
import com.tosDev.web.spring.jpa.repository.main_tables.ShiftRepository;
import com.tosDev.web.spring.web.service.drive.DriveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneOffset;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TgPictureConsumer {
    private final DriveService driveService;
    private final ShiftRepository shiftRepository;

    /**
     * Принимаем здесь мапу фото и смены на сохранение в google drive
     * @param fileIdShiftIdRecord record с id фото в чате и id смены
     */
    @RabbitListener(queues = "${rabbitmq.queue.tg}")
    public void consumer(PhotoShiftIdRecord fileIdShiftIdRecord){
        log.info("Получили {} из rabbitMQ очереди",fileIdShiftIdRecord);
        //Находим shift
        try {
            Shift shift = shiftRepository.findById(fileIdShiftIdRecord.shiftId()).orElseThrow();
            //Для первой фото создаем папку сначала по id shift и началу shift
            if (fileIdShiftIdRecord.isFirstPhoto()){
                String folderName = shift.getStartDateTime().toString()+" "+shift.getWorker().getName();
                driveService.createFolder(folderName);
                //todo:Доделать
            }
        } catch (Exception e) {
            log.error("Ошибка при сохранении фото в гугл drive для {}",fileIdShiftIdRecord,e);
        }
    }
}
