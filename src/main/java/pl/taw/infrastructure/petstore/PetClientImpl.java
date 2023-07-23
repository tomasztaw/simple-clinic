package pl.taw.infrastructure.petstore;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.taw.business.dao.PetDAO;
import pl.taw.infrastructure.petstore.api.PetApi;

import java.util.Optional;

@Component
@AllArgsConstructor
public class PetClientImpl implements PetDAO {

//    private final WebClient webClient;

    private final PetApi petApi;
    private final PetMapper petMapper;

    @Override
    public Optional<Pet> getPet(Long petId) {
        try {
//            final var available = petApi.findPetsByStatusWithHttpInfo("available")
//                    .block()
//                    .getBody();
            return Optional.ofNullable(petApi.getPetById(petId)
                    .block())
                    .map(petMapper::map);
        } catch (Exception e) {
            return Optional.empty();
        }
    }



//    @Override
//    public Optional<Pet> getPet(Long petId) {
//        try {
//            Pet result = webClient.get()
//                    .uri("/pet/" + petId)
//                    .retrieve()
//                    .bodyToMono(Pet.class)
//                    .block();
//            return Optional.ofNullable(result);
//        } catch (Exception e) {
//            return Optional.empty();
//        }
//    }
}
