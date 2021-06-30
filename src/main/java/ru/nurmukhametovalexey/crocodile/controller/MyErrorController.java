package ru.nurmukhametovalexey.crocodile.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Slf4j
@RestController
public class MyErrorController implements ErrorController {
    @GetMapping("/error")
    public ModelAndView errorPage(@ModelAttribute("errorMessage") Object errorMessage, Principal principal) {
        log.info("errorPage. Message: {}. Principal: {}", errorMessage, principal.getName());
        ModelAndView modelAndView = new ModelAndView("/error");
        modelAndView.addObject("errorMessage", errorMessage);
        if (principal != null) {
            modelAndView.addObject("user", principal.getName());
        }
        return modelAndView;
    }
}
