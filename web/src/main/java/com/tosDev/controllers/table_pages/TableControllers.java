package com.tosDev.controllers.table_pages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Controller
@RequestMapping("/tables")
@RequiredArgsConstructor
public class TableControllers {

    @GetMapping("/equipments")
    String showEquipTable(Model model){
        return "equipment_tab";
    }

    @GetMapping("/api/v1/test_equip_json")
    @ResponseBody
    String getTestEquipJson(){
        String path = "C:\\Docs\\Java Projects\\crm-tg-web-service\\main_web_module\\src\\main\\resources\\data_new.json";
        String equipJson;
        try {
            equipJson = new String(Files.readAllBytes(Path.of(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return equipJson;
    }
}
