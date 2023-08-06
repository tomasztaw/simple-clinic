package pl.taw.api.controller;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class HomeControllerTest {

    // nie działa - będzie do usunięcia

    @Mock
    private PatientDAO patientDAO;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HomeController homeController;

    private MockMvc mockMvc;

    @Test
    public void testHomePage_authenticatedUser() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("user@example.com");
        when(userRepository.findByUserName(anyString())).thenReturn(userEntity);

        PatientDTO patientDTO = DtoFixtures.somePatient1().withEmail("user@example.com");
        when(patientDAO.findByEmail(anyString())).thenReturn(patientDTO);

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> "USER");

        Authentication authentication = new CustomAuthentication("user", authorities);

        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();

        mockMvc.perform(get("/")
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("username", "isUser", "patient"));
    }

    // Define a custom Authentication class for testing
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
}
