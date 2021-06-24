package ru.nurmukhametovalexey.crocodile.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.nurmukhametovalexey.crocodile.controller.dto.ConnectRequest;
import ru.nurmukhametovalexey.crocodile.controller.dto.StartRequest;
import ru.nurmukhametovalexey.crocodile.dao.UserDAO;
import ru.nurmukhametovalexey.crocodile.model.User;

import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@RestController
public class HomeController {
    private UserDAO userDAO;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public HomeController(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping()
    public ModelAndView homePage(@ModelAttribute("startRequest") StartRequest startRequest,
                                 @ModelAttribute("connectRequest") ConnectRequest connectRequest, Principal principal) {
        ModelAndView modelAndView = new ModelAndView("index");
        if (principal != null) {
            modelAndView.addObject("user",principal.getName());
        } else {
            modelAndView.addObject("user","Unknown");
        };
        return modelAndView;
    }

    @GetMapping("/error")
    public ModelAndView errorPage() {
        ModelAndView modelAndView = new ModelAndView("error");
        return modelAndView;
    }

    @RequestMapping("/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("login");
        return  modelAndView;
    }

    @RequestMapping("/login-error")
    public ModelAndView loginError() {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("loginError", true);
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView newPerson(@ModelAttribute("user") User user) {
        if (user == null) {
            log.info("register with user null");
        } else {
            log.info("register with user {}", user.toString());
        }

        ModelAndView modelAndView = new ModelAndView("/register");
        return modelAndView;
    }

    @PostMapping("/register-submit")
    public ModelAndView create(@ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult) {
        if (user == null) {
            log.info("register-submit with user null");
        } else {
            log.info("register-submit with user {} and BindingResult {}", user.toString(), bindingResult.toString());
        }

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("/register");
            return modelAndView;
        }
        else if(userDAO.getUserByLogin(user.getLogin()) != null) {
            log.info("register-submit with user that already exists");
            ModelAndView modelAndView = new ModelAndView("/register");
            modelAndView.addObject("loginUsed", true);
            return modelAndView;
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole("User");
            user.setScore(0);
            user.setEnabled(true);
            userDAO.save(user);
            ModelAndView modelAndView = new ModelAndView("/login");
            modelAndView.addObject("freshlyCreated", true);
            return modelAndView;
        }
    }

    @GetMapping("/leaderboard")
    public ModelAndView getAllUsers() {
        log.info("getAllUsers called");
        ModelAndView modelAndView = new ModelAndView("leaderboard");
        modelAndView.addObject("users", userDAO.getAll());
        return modelAndView;
    }
}
