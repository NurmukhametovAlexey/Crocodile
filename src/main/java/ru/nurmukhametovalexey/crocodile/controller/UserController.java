package ru.nurmukhametovalexey.crocodile.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.nurmukhametovalexey.crocodile.dao.UserDAO;

@Controller
@RequestMapping("/user")
public class UserController {
    private UserDAO userDAO;

    @Autowired
    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping()
    public String getAllUsers(Model model) {
        model.addAttribute("users", userDAO.getAll());
        return "user/all";
    }
}
