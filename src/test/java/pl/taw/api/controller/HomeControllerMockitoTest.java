package pl.taw.api.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.Model;
import pl.taw.api.dto.PatientDTO;
import pl.taw.business.dao.PatientDAO;
import pl.taw.infrastructure.security.UserEntity;
import pl.taw.infrastructure.security.UserRepository;
import pl.taw.proby.jakoscpowietrza.AirQualityService;
import pl.taw.util.DtoFixtures;
import pl.taw.util.EntityFixtures;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerMockitoTest {

    @Mock
    private PatientDAO patientDAO;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Model model;
    // próby z air
    @Mock
    private AirQualityService airQualityService;

    @InjectMocks
    private HomeController homeController;

    @Test
    public void testHomePageWithUserAuthority() {
        // given
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> "USER");
        UserEntity user = EntityFixtures.someUser1();
        PatientDTO patient = DtoFixtures.somePatient1();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUserName(), null, authorities);

        when(userRepository.findByUserName(user.getUserName())).thenReturn(user);
        when(patientDAO.findByEmail(user.getEmail())).thenReturn(patient);
        // air
        when(airQualityService.getIndexLevelName()).thenReturn("Umiarkowana");

        // when
        String result = homeController.homePage(model, authentication);

        // then
        Assertions.assertThat(result).isEqualTo("home");

        verify(model).addAttribute(eq("username"), eq(user.getUserName()));
        verify(model).addAttribute(eq("isUser"), eq(true));
        verify(model).addAttribute(eq("isDoctor"), eq(false));
        verify(model).addAttribute("patient", patient);

        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(patientDAO, times(1)).findByEmail(user.getEmail());
        verifyNoMoreInteractions(userRepository, patientDAO);
    }

    @Test
    public void testHomePageWithoutAuthentication() {
        // given, when
        String viewName = homeController.homePage(model, null);

        // then
        assertEquals("home", viewName);
        verify(model, never()).addAttribute(anyString(), any());
    }

}