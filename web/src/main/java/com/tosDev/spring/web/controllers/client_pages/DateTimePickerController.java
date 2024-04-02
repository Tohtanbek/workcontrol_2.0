package com.tosDev.spring.web.controllers.client_pages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/form")
public class DateTimePickerController {
    @GetMapping("/select_date_time")
    String getDateTimePage(){
        return "/client_pages/select_date_time";
    }
}
