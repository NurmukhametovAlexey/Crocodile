package ru.nurmukhametovalexey.crocodile.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.nurmukhametovalexey.crocodile.dao.UserDAO;
import ru.nurmukhametovalexey.crocodile.model.GameUser;
import ru.nurmukhametovalexey.crocodile.model.User;
import ru.nurmukhametovalexey.crocodile.service.DaoService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private DaoService daoService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(DaoService daoService, PasswordEncoder passwordEncoder) {
        this.daoService = daoService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping()
    public ModelAndView account(@ModelAttribute("success") Object success,
                                Principal principal, RedirectAttributes attributes) {
        log.info("account: {}", principal.getName());

        User user = daoService.getUserDAO().getUserByLogin(principal.getName());;
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
    public ModelAndView accountUpdate(Principal principal, @ModelAttribute User user, RedirectAttributes attributes) {
        log.info("account update: {}", user);

        User initialUser = daoService.getUserDAO().getUserByLogin(principal.getName());

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            initialUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            initialUser.setEmail(user.getEmail());
        }

        daoService.getUserDAO().update(initialUser);
        ModelAndView modelAndView = new ModelAndView("redirect:/user");
        attributes.addFlashAttribute("success", true);
        return modelAndView;
    }

    @GetMapping("/history")
    public ModelAndView gameHistory(Principal principal) {
        log.info("game history: {}", principal.getName());

        List<GameUser> userGames = daoService.getGameUserDAO().getGameUserByLogin(principal.getName());

        ModelAndView modelAndView = new ModelAndView("/gameHistory");
        modelAndView.addObject("user", principal.getName());
        modelAndView.addObject("userGames", userGames);
        return modelAndView;
    }
}
