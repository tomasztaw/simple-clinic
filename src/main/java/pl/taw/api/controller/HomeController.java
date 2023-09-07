package pl.taw.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.taw.api.dto.PatientDTO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.infrastructure.security.UserEntity;
import pl.taw.infrastructure.security.UserRepository;
import pl.taw.proby.jakoscpowietrza.AirQualityService;

import java.util.Collection;

@Controller
@RequestMapping(HomeController.HOME)
@AllArgsConstructor
public class HomeController {

    public static final String HOME = "/";

    private final PatientDAO patientDAO;
    private final UserRepository userRepository;

    // air
    private final AirQualityService airQualityService;

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

            // air
            String airQuality = airQualityService.getIndexLevelName().replace("y", "a");
            String backgroundColor = getBackgroundColor(airQuality);

            model.addAttribute("airQuality", airQuality);
            model.addAttribute("backgroundColor", backgroundColor);

        }

        return "home";
    }

    private String getBackgroundColor(String airQuantity) {
        switch (airQuantity) {
            case "Bardzo dobra" -> { return "rgba(87, 177, 8, 0.3)"; }
            case "Dobra" -> { return "rgba(176, 221, 16, 0.3)"; }
            case "Umiarkowana" -> { return "rgba(255, 217, 17, 0.3)"; }
            case "Zła" -> { return "rgba(229, 0, 0, 0.3)"; }
            case "Bardzo zła" -> { return "rgba(153, 0, 0, 0.3)"; }
            default -> { return ""; }
        }
    }

}