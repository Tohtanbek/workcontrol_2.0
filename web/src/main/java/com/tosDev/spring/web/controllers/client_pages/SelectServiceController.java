package com.tosDev.spring.web.controllers.client_pages;

import com.tosDev.dto.client_pages.ShortServiceDto;
import com.tosDev.enums.ServiceCategory;
import com.tosDev.spring.jpa.repository.client_orders.ServiceRepository;
import com.tosDev.spring.web.service.ServiceEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequiredArgsConstructor
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
}
