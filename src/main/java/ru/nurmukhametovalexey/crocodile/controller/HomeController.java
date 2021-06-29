package ru.nurmukhametovalexey.crocodile.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.nurmukhametovalexey.crocodile.controller.dto.ConnectRequest;
import ru.nurmukhametovalexey.crocodile.controller.dto.StartRequest;
import ru.nurmukhametovalexey.crocodile.model.User;
import ru.nurmukhametovalexey.crocodile.service.DaoService;

import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@RestController
public class HomeController {
    private final DaoService daoService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public HomeController(DaoService daoService, PasswordEncoder passwordEncoder) {
        this.daoService = daoService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping()
    public ModelAndView homePage(@ModelAttribute("startRequest") StartRequest startRequest,
                                 @ModelAttribute("connectRequest") ConnectRequest connectRequest,
                                 Principal principal) {
        ModelAndView modelAndView = new ModelAndView("/index");
        if (principal != null) {
            String user = principal.getName();
            String gameUUID = daoService.getActiveGameUuidByLogin(principal.getName());
            modelAndView.addObject("user",user);
            modelAndView.addObject("gameUUID", gameUUID);
            log.info("added user = {} and gameUUID = {}", user, gameUUID);
        }

        return modelAndView;
    }

    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("/login");
    }

    @RequestMapping("/login-error")
    public ModelAndView loginError() {
        ModelAndView modelAndView = new ModelAndView("/login");
        modelAndView.addObject("loginError", true);
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView newPerson(@ModelAttribute("user") User user) {
        if (user == null) {
            log.info("register with user null");
        } else {
            log.info("register with user {}", user);
        }

        return new ModelAndView("/register");
    }

    @PostMapping("/register-submit")
    public ModelAndView create(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        if (user == null) {
            log.info("register-submit with user null");
        } else {
            log.info("register-submit with user {} and BindingResult {}", user, bindingResult.toString());
        }

        if (bindingResult.hasErrors()) {
            return new ModelAndView("/register");
        }
        else if(daoService.getUserDAO().getUserByLogin(user.getLogin()) != null) {
            log.info("register-submit with user that already exists");
            ModelAndView modelAndView = new ModelAndView("/register");
            modelAndView.addObject("loginUsed", true);
            return modelAndView;
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole("User");
            user.setScore(0);
            user.setEnabled(true);
            daoService.getUserDAO().save(user);
            ModelAndView modelAndView = new ModelAndView("/login");
            modelAndView.addObject("freshlyCreated", true);
            return modelAndView;
        }
    }

    @GetMapping("/leaderboard")
    public ModelAndView getAllUsers(Principal principal) {
        log.info("getAllUsers called");
        ModelAndView modelAndView = new ModelAndView("leaderboard");
        modelAndView.addObject("users", daoService.getUserDAO().getAllScoresDesc());
        if (principal != null) {
            modelAndView.addObject("user", principal.getName());
        }
        return modelAndView;
    }

    @GetMapping("/error")
    public ModelAndView errorPage(@ModelAttribute("errorMessage") Object errorMessage) {
        ModelAndView modelAndView = new ModelAndView("/error");
        modelAndView.addObject("errorMessage", errorMessage);
        return modelAndView;
    }
}
