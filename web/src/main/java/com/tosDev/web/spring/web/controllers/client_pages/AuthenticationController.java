package com.tosDev.web.spring.web.controllers.client_pages;

import com.tosDev.web.dto.client_pages.ClientDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequestMapping("/form")
@SessionAttributes({"client"})
public class AuthenticationController {

    @GetMapping("/authentication")
    String getAuthenticationPage(){
        return "/client_pages/authentication";
    }

    @PostMapping("/authentication")
    String authenticateCustomer(@Validated ClientDto client,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("errors",
                    bindingResult.getAllErrors());
            return "redirect:authentication";
        }
        else {
            model.addAttribute("client",client);
            log.info("Пользователь {} оставил свои данные и начал выбор услуг",client);
            return "redirect:select_service";
        }
    }
}
