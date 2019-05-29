package com.test.links.controller;


import com.test.links.domain.User;
import com.test.links.repo.LinkRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

@Controller
@RequestMapping("/")
public class MainController {
    private final LinkRepo linkRepo;

    @Autowired
    public MainController(LinkRepo linkRepo) {
        this.linkRepo = linkRepo;
    }

    @GetMapping
    public String main(Model model, @AuthenticationPrincipal User user) {
        HashMap<Object, Object> data = new HashMap<>();

        data.put("profile", user);
        try {
                data.put("messages", linkRepo.findByUserId(user.getId()));

        } catch (NullPointerException e) {
            data.put("messages", linkRepo.findAll());
        }

        model.addAttribute("frontendData", data);

        return "index";
    }
}
