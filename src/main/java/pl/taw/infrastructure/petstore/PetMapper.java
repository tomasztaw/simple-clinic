package pl.taw.infrastructure.petstore;

import org.springframework.stereotype.Component;
import pl.taw.infrastructure.petstore.model.Category;

import java.util.Optional;

@Component
public class PetMapper {

    public Pet map(pl.taw.infrastructure.petstore.model.Pet pet) {
        return Pet.builder()
                .id(pet.getId())
                .name(pet.getName())
                .category(Optional.ofNullable(pet.getCategory()).map(Category::getName).orElse(null))
                .build();
    }
}
