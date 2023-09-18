package pl.taw.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import pl.taw.business.dao.PatientDAO;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.security.RoleEntity;
import pl.taw.infrastructure.security.RoleRepository;
import pl.taw.infrastructure.security.UserEntity;
import pl.taw.infrastructure.security.UserRepository;

import java.util.Set;

@Controller
@RequestMapping(RegisterController.REGISTER)
@AllArgsConstructor
public class RegisterController {

    public static final String REGISTER = "/register";
    public static final String LOGIN_PAGE = "/loginPage";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientDAO patientDAO;
    private final RoleRepository roleRepository;

    /**
     * Klasę będę przerabiał i test napiszę po przeróbkach
     * 7-08-2023 r.
     */

    @GetMapping("/hello")
    public String helloFromRegister() {
        return "calendar";
    }

    @Secured("permitAll")
    @PostMapping("/addUserXXX")
    @Transactional
    public String addUserXXX(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam("name") String name,
                             @RequestParam("surname") String surname,
                             @RequestParam("pesel") String pesel,
                             @RequestParam("phone") String phone,
                             @RequestParam("email") String email,
                             Model model) {

        if (userRepository.findByUserName(username) != null) {
            model.addAttribute("error", "Nazwa użytkownika jest już zajęta");
            return "register-security";
        } else if (userRepository.findByEmail(email) != null) {
            model.addAttribute("error", "E-mail jest już zajęty");
            return "register-security";
        }

        UserEntity user = UserEntity.builder()
                .userName(username)
                .password(passwordEncoder.encode(password))
                .build();

        userRepository.save(user);

        PatientEntity patient = PatientEntity.builder()
                .name(name)
                .surname(surname)
                .pesel(pesel)
                .phone(phone)
                .email(email)
                .build();

        patientDAO.save(patient);

        return "redirect:/login?registered";
    }

    @PostMapping("/kredki")
    public String dodajKredki(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              @RequestParam("email") String email,
                              @RequestParam("name") String name) {
        RoleEntity role = roleRepository.findByRole("USER");
        UserEntity user = UserEntity.builder()
                .userName(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .name(name)
                .active(true)
                .roles(Set.of(role))
                .build();
        userRepository.save(user);
        return "redirect:/";
    }

    @PostMapping("/addUser")
    @Transactional
    public ModelAndView addUser(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                @RequestParam("name") String name,
                                @RequestParam("surname") String surname,
                                @RequestParam("pesel") String pesel,
                                @RequestParam("phone") String phone,
                                @RequestParam("email") String email,
                                Model model) {
        if (userRepository.findByUserName(username) != null) {
            model.addAttribute("error", "Nazwa użytkownika jest już zajęta");
        } else if (userRepository.findByEmail(email) != null) {
            model.addAttribute("error", "E-mail jest już zajęty");
        }

        RoleEntity role = roleRepository.findByRole("USER");

        System.out.println("role = " + role);

        Set<RoleEntity> roles = Set.of(role);

        UserEntity user = UserEntity.builder()
                .userName(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .roles(roles)
                .name(name)
                .active(true)
                .build();

        System.out.println("user = " + user);

        userRepository.save(user);

        PatientEntity patient = PatientEntity.builder()
                .name(name)
                .surname(surname)
                .pesel(pesel)
                .phone(phone)
                .email(email)
                .build();

        System.out.println("patient = " + patient);

        patientDAO.save(patient);

        ModelAndView modelAndView = new ModelAndView("core/registration-confirmation");
        return modelAndView;
    }

    @GetMapping
    public String registerPatient() {
        return "core/register-security";
    }


    @GetMapping(LOGIN_PAGE)
    public String loginPage() {
        return "core/loginPage";
    }


}
