package pl.taw.proby.rozne;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;

@Service
public class JavaClientService {

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();

//    public UserExample showUser(int id) {
//        try {
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri()
//                    .GET()
//                    .build();
//            return objectMapper.readValue(response.body(), UserExample.class);
//        } catch (URISyntaxException | IOException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
