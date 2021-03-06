package ru.nurmukhametovalexey.crocodile.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.nurmukhametovalexey.crocodile.model.GameHistory;
import ru.nurmukhametovalexey.crocodile.model.User;
import ru.nurmukhametovalexey.crocodile.service.DaoService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    private final DaoService daoService;

    @Autowired
    public UserController(DaoService daoService) {
        this.daoService = daoService;
    }

    @GetMapping()
    public ModelAndView account(@ModelAttribute("success") Object success,
                                Principal principal, RedirectAttributes attributes) {
        log.info("account: {}", principal.getName());

        User user = daoService.getUserByLogin(principal.getName());
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

        if (bindingResult.hasFieldErrors("email") || bindingResult.hasFieldErrors("password") &&
                user.getPassword() != null && !user.getPassword().isBlank()) {
            return new ModelAndView("/user");
        }

        daoService.updateUser(principal.getName(), user.getPassword(), user.getEmail());

        attributes.addFlashAttribute("success", true);
        return new ModelAndView("redirect:/user");
    }

    @GetMapping("/history")
    public ModelAndView gameHistory(Principal principal) {
        log.info("game history: {}", principal.getName());

        List<GameHistory> gameHistoryList = daoService.getGameHistoryByLogin(principal.getName());

        ModelAndView modelAndView = new ModelAndView("/gameHistory");
        modelAndView.addObject("user", principal.getName());
        modelAndView.addObject("userGames", gameHistoryList);
        return modelAndView;
    }
}
