package com.company.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;


@RestController
@RequestMapping("v1/home")
@RequiredArgsConstructor
public class HomeController {
    private final MessageSource messageSource;

    @GetMapping
    public String getHomePage(Model model, @RequestHeader(name = "lang") String lang) {
        String greeting = messageSource.getMessage("greetings", null, Locale.of(lang));
        model.addAttribute("name", "nicat");
        model.addAttribute("greetings",greeting);

        return greeting;
    }

}
