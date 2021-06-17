package ru.nurmukhametovalexey.crocodile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    @GetMapping()
    public String testMain() {
        return "index";
    }


}
