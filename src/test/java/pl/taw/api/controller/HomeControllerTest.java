package pl.taw.api.controller;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import pl.taw.api.dto.PatientDTO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.infrastructure.security.UserEntity;
import pl.taw.infrastructure.security.UserRepository;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
//@AllArgsConstructor(onConstructor = @__(@Autowired))
public class HomeControllerTest {

    // nie działa - będzie do usunięcia

    @Mock
    private PatientDAO patientDAO;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private HomeController homeController;

    private MockMvc mockMvc;

    @Test
    public void testHomePage_authenticatedUser() throws Exception {
//        UserEntity userEntity = new UserEntity();
        UserEntity userEntity = EntityFixtures.someUser1();
//        userEntity.setEmail("user@example.com");
//        when(userRepository.findByUserName(anyString())).thenReturn(userEntity);

        PatientDTO patientDTO = DtoFixtures.somePatient1().withEmail(userEntity.getEmail());
//        PatientDTO patientDTO = DtoFixtures.somePatient1().withEmail("user@example.com");
//        when(patientDAO.findByEmail(anyString())).thenReturn(patientDTO);

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> "USER");

        Authentication authentication = new CustomAuthentication(userEntity.getName(), authorities);

        String username = authentication.getName();

        boolean isUser = authorities.stream().anyMatch(a -> a.getAuthority().equals("USER"));

        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();

        mockMvc.perform(get("/")
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
//                .andExpect(model().attribute("username", username))
//                .andExpect(model().attribute("isUser", isUser))
//                .andExpect(model().attribute("patient", patientDTO));
//                .andExpect(model().attributeExists("username", "isUser", "patient"));
    }

    private static class CustomAuthentication implements Authentication {
        private final String name;
        private final Collection<? extends GrantedAuthority> authorities;

        public CustomAuthentication(String name, Collection<? extends GrantedAuthority> authorities) {
            this.name = name;
            this.authorities = authorities;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return null;
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Test
    public void testHomePage() {
        // given
        String email = "test@exaple.com";
        UserEntity userEntity = new UserEntity().withEmail(email);
//        userEntity.setEmail("test@example.com");
        PatientDTO patientDTO = DtoFixtures.somePatient1().withEmail(email);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(userRepository.findByUserName(any())).thenReturn(userEntity);
        when(patientDAO.findByEmail(any())).thenReturn(new PatientDTO());

        // when
        String viewName = homeController.homePage(model, authentication);

        // then
        assertEquals("home", viewName);

        verify(model).addAttribute("username", any());
        verify(model).addAttribute("isAdmin", anyBoolean());
        verify(model).addAttribute("isUser", anyBoolean());
        verify(model).addAttribute("isDoctor", anyBoolean());
        verify(model).addAttribute("patient", patientDTO);
    }

    @Test
    public void testHomePageWithUserAuthentication() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenReturn(new ArrayList<>()); // You can populate this list with GrantedAuthority instances

        String username = "testUser";
        String userEmail = "test@example.com";
        when(authentication.getName()).thenReturn(username);

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userEmail);
        when(userRepository.findByUserName(username)).thenReturn(userEntity);

        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setEmail(userEmail);
        when(patientDAO.findByEmail(userEmail)).thenReturn(patientDTO);

        Model model = mock(Model.class);

        String viewName = homeController.homePage(model, authentication);

        assertEquals("home", viewName);

        verify(model).addAttribute("username", username);
        verify(model).addAttribute("isUser", true);
        verify(model).addAttribute("patient", patientDTO);
        verify(model, never()).addAttribute("isAdmin", any());
        verify(model, never()).addAttribute("isDoctor", any());
    }

    @Test
    public void testHomePageWithUserAuthentication2() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenReturn(new ArrayList<>());

//        String username = "testUser";
        String username = "username";
        String userEmail = "test@example.com";
        when(authentication.getName()).thenReturn(username);

        UserEntity userEntity = EntityFixtures.someUser1().withEmail(userEmail).withUserName(username);
        when(userRepository.findByUserName(username)).thenReturn(userEntity);

        PatientDTO patientDTO = DtoFixtures.somePatient1().withEmail(userEmail);
        when(patientDAO.findByEmail(userEmail)).thenReturn(patientDTO);

        Model model = mock(Model.class);

        String viewName = homeController.homePage(model, authentication);

        assertEquals("home", viewName);

        verify(model).addAttribute("username", username);
//        verify(model).addAttribute("isUser", true);
        verify(model).addAttribute("patient", patientDTO);
//        verify(model, never()).addAttribute("isAdmin", any());
//        verify(model, never()).addAttribute("isDoctor", any());
    }


    @Test
    public void testHomePageWithoutAuthentication() {
        Authentication authentication = null;
        Model model = mock(Model.class);

        String viewName = homeController.homePage(model, authentication);

        assertEquals("home", viewName);

        verify(model, never()).addAttribute(anyString(), any());
    }
}
