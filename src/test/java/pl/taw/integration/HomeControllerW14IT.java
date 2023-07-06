package pl.taw.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.taw.api.controller.HomeController;

@ExtendWith(SpringExtension.class)
public class HomeControllerW14IT {

    @Autowired
    private HomeController homeController;

//    private final PatientDAO patientDAO;
//    private final UserRepository userRepository;

    @BeforeEach
    void setUp() {
        Assertions.assertNotNull(homeController);
    }

    @Test
    void someTest() {

    }
}
