package pl.taw.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.taw.infrastructure.security.RoleEntity;
import pl.taw.infrastructure.security.UserEntity;
import pl.taw.infrastructure.security.UserRepository;

import java.security.Principal;
import java.util.Collection;
import java.util.Set;

@Controller
@RequestMapping(HomeController.HOME)
@AllArgsConstructor
public class HomeController {

    public static final String HOME = "/";
    public static final String WELCOME = "/welcome";

    private final UserRepository userRepository;

    @GetMapping(HOME)
    public String homePage(Model model, Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean isAdmin = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
            boolean isDoctor = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("DOCTOR"));
            boolean isUser = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("USER"));
            String username = authentication.getName();
            model.addAttribute("username", username);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isUser", isUser);
            model.addAttribute("isDoctor", isDoctor);
        }

        return "home";
    }

//    @GetMapping(WELCOME)
//    public String welcomePage(Model model) {
//        return "welcome";
//    }

}