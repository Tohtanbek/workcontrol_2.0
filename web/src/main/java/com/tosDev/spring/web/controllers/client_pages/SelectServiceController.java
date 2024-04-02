package com.tosDev.spring.web.controllers.client_pages;

import com.tosDev.dto.client_pages.ChosenMainServiceDto;
import com.tosDev.dto.client_pages.ExtraServiceDateTimeDto;
import com.tosDev.dto.client_pages.ShortServiceDto;
import com.tosDev.enums.ServiceCategory;
import com.tosDev.spring.web.service.ServiceEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@SessionAttributes({"client","mainService"})
@RequestMapping("/form")
public class SelectServiceController {

    private final ServiceEntityService serviceEntityService;

    @GetMapping("/select_service")
    String getMainServicesPage(Model model){
        List<ShortServiceDto> dtoList =
                serviceEntityService.loadAndMapToShorServices(ServiceCategory.MAIN);
        if (dtoList.isEmpty()){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка при загрузке услуг");
        }
        model.addAttribute("serviceList",dtoList);
        return "/client_pages/select_service";
    }
    @PostMapping("/select_service")
    ResponseEntity<Void> submitMainService(@RequestBody ChosenMainServiceDto chosenService,
                                     Model model){
        model.addAttribute("mainService",chosenService);
        log.info("Пользователь {} выбрал основную услугу {}",
                model.getAttribute("client"),
                chosenService);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/select_extra_service")
    String getAdditionalServicePage(Model model){
        List<ShortServiceDto> dtoList =
                serviceEntityService.loadAndMapToShorServices(ServiceCategory.EXTRA);
        if (dtoList.isEmpty()){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка при загрузке дополнительных услуг");
        }
        model.addAttribute("extraServiceList",dtoList);
        return "/client_pages/select_extra_service";
    }

    @PostMapping("/select_extra_service")
    ResponseEntity<Void> submitExtraService(@RequestBody ExtraServiceDateTimeDto dto,
                                           Model model){
        model.addAttribute("extraServices",dto);
        log.info("Пользователь {} выбрал дополнительные услуги, время и дату: {}",
                model.getAttribute("client"),
                dto);
        return ResponseEntity.ok().build();
    }
}
