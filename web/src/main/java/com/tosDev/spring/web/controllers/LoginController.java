package com.tosDev.spring.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    String loginPage(){
        return "login";
    }

    //PostMapping не нужен, т.к его предоставляет springSecurity сам
}
