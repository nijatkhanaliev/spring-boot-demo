package com.company.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;


@Controller
@RequestMapping("v1/home")
@RequiredArgsConstructor
public class HomeController {
    private final MessageSource messageSource;

    @GetMapping
    public String getHomePage(Model model, Locale locale){
        model.addAttribute("name","nicat");

        System.out.println(messageSource.getMessage("greetings", null, locale));

        return "home";
    }

}
