package ru.nurmukhametovalexey.crocodile.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.nurmukhametovalexey.crocodile.dao.UserDAO;
import ru.nurmukhametovalexey.crocodile.model.GameHistory;
import ru.nurmukhametovalexey.crocodile.model.User;
import ru.nurmukhametovalexey.crocodile.dao.ComplexDao;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserDAO userDAO;
    private final ComplexDao complexDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserDAO userDAO, ComplexDao complexDao, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.complexDao = complexDao;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping()
    public ModelAndView account(@ModelAttribute("success") Object success,
                                Principal principal, RedirectAttributes attributes) {
        log.info("account: {}", principal.getName());

        User user = userDAO.getUserByLogin(principal.getName());
        if (user == null) {
            attributes.addFlashAttribute("errorMessage", "Can`t find user with login: " + principal.getName());
            return new ModelAndView("redirect:/error");
        }
        user.setPassword(null);

        ModelAndView modelAndView = new ModelAndView("/user");
        modelAndView.addObject("user", user);
        modelAndView.addObject("success", success);
        return modelAndView;
    }

    @PostMapping()
    public ModelAndView accountUpdate(Principal principal, @ModelAttribute @Valid User user,
                                      BindingResult bindingResult, RedirectAttributes attributes) {
        log.info("account update: {}, binding result: {}", user, bindingResult.toString());

        User initialUser = userDAO.getUserByLogin(principal.getName());

        if (bindingResult.hasFieldErrors("email") || bindingResult.hasFieldErrors("password") &&
                user.getPassword() != null && !user.getPassword().isBlank()) {
            return new ModelAndView("/user");
        }

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            initialUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            initialUser.setEmail(user.getEmail());
        }

        userDAO.update(initialUser);
        attributes.addFlashAttribute("success", true);
        return new ModelAndView("redirect:/user");
    }

    @GetMapping("/history")
    public ModelAndView gameHistory(Principal principal) {
        log.info("game history: {}", principal.getName());

        List<GameHistory> gameHistoryList = complexDao.getGameHistoryByLogin(principal.getName());

        ModelAndView modelAndView = new ModelAndView("/gameHistory");
        modelAndView.addObject("user", principal.getName());
        modelAndView.addObject("userGames", gameHistoryList);
        return modelAndView;
    }
}
