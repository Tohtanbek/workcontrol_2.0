package com.tosDev.web.spring.jpa.repository.main_tables;

import com.tosDev.web.spring.jpa.entity.main_tables.Brigadier;
import com.tosDev.web.spring.jpa.entity.main_tables.Shift;
import com.tosDev.web.enums.ShiftStatusEnum;
import com.tosDev.web.spring.jpa.entity.main_tables.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Integer> {

    boolean existsByWorkerAndStatus(Worker worker, ShiftStatusEnum status);

    Optional<Shift> findByWorkerIdAndStatus(Integer workerId,ShiftStatusEnum status);

    boolean existsByBrigadierAndStatus(Brigadier brigadier, ShiftStatusEnum status);

    Optional<Shift> findByBrigadierIdAndStatus(Integer brigadierId, ShiftStatusEnum status);

    @Query("SELECT s FROM Shift s " +
            "WHERE s.worker.id=:workerId " +
            "AND s.startDateTime>=:sdt " +
            "AND s.endDateTime<:edt")
    List<Shift> findAllByWorkerIdAndDataRange(@Param("workerId") Integer workerId,
                                              @Param("sdt") LocalDateTime startDateTime,
                                              @Param("edt") LocalDateTime endDateTime);

    @Modifying
    @Query("UPDATE Shift s " +
            "SET s.folderId=:folderId " +
            "WHERE s.id=:shiftId")
    void updateFolderName(@Param("shiftId") Integer shiftId,
                          @Param("folderId") String folderId);
}
