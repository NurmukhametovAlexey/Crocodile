package ru.nurmukhametovalexey.crocodile.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.nurmukhametovalexey.crocodile.dao.UserDAO;
import ru.nurmukhametovalexey.crocodile.model.User;

import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private UserDAO userDAO;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping()
    public ModelAndView account(Principal principal) {
        User user;

        if (principal == null) {
            ModelAndView modelAndView = new ModelAndView("/login");
            return modelAndView;
        } else {
            user = userDAO.getUserByLogin(principal.getName());
        }

        if (user == null) {
            ModelAndView modelAndView = new ModelAndView("/error");
            return modelAndView;
        }

        user.setPassword(null);

        ModelAndView modelAndView = new ModelAndView("/user");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PostMapping()
    public ModelAndView accountUpdate(Principal principal, @ModelAttribute User user) {
        log.info("accountUpdate. {}", user);

        if (principal == null) {
            ModelAndView modelAndView = new ModelAndView("/login");
            return modelAndView;
        }

        User initialUser = userDAO.getUserByLogin(principal.getName());

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            initialUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            initialUser.setEmail(user.getEmail());
        }

        userDAO.update(initialUser);
        ModelAndView modelAndView = new ModelAndView("/user");
        modelAndView.addObject("success", true);
        return modelAndView;
    }
}
