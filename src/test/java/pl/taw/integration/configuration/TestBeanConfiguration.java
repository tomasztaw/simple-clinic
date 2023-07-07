package pl.taw.integration.configuration;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.taw.business.VisitService;
import pl.taw.business.dao.VisitDAO;

@Configuration
public class TestBeanConfiguration {  // TODO, sposób nisko poziomowy, pewnie będzie zmieniony, problemy z działaniem (użyłem @MockBean)

    @Mock
    private VisitDAO visitDAO;

    public TestBeanConfiguration() {
        try (AutoCloseable autoCloseable = MockitoAnnotations.openMocks(this)) {
            System.out.println("Correctly opened mocks");
        } catch (Exception e) {
            System.err.println("Unable to open mocks: " + e);
            throw new RuntimeException(e);
        }
    }

    @Bean
    public VisitDAO visitDAO() {
        return visitDAO;
    }
//    @Bean
//    public VisitService visitService() {
//        return new VisitService();
//    }
//
    // drugi sposób
//    @Bean
//    public VisitDAO visitDAO() {
//        return visitDAO;
////        return Mockito.mock(VisitDAO.class);
//    }
}
