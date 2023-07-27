package pl.taw.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.taw.api.dto.PatientDTO;
import pl.taw.business.dao.PatientDAO;
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

    private final PatientDAO patientDAO;
    private final UserRepository userRepository;

    @GetMapping(HOME)
    public String homePage(Model model, Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean isAdmin = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
            boolean isDoctor = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("DOCTOR"));
            boolean isUser = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("USER"));
            String username = authentication.getName();
            UserEntity user = userRepository.findByUserName(username);
            model.addAttribute("username", username);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isUser", isUser);
            model.addAttribute("isDoctor", isDoctor);
            if (isUser) {
                PatientDTO patient = patientDAO.findByEmail(user.getEmail());
                model.addAttribute("patient", patient);
            }
        }

        return "home";
    }

}