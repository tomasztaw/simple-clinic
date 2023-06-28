package pl.taw.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(HomeController.HOME)
@AllArgsConstructor
public class HomeController {

    public static final String HOME = "/";

    @GetMapping(HOME)
    public String homePage(Model model) {
        return "home";
    }

}