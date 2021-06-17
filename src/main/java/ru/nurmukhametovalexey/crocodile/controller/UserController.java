package ru.nurmukhametovalexey.crocodile.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ru.nurmukhametovalexey.crocodile.dao.UserDAO;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    private UserDAO userDAO;

    @Autowired
    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping("/leaderboard")
    public ModelAndView getAllUsers() {
        log.info("getAllUsers called");
        ModelAndView modelAndView = new ModelAndView("user/leaderboard");
        modelAndView.addObject("users", userDAO.getAll());
        return modelAndView;
    }
}
