package pl.taw.business.dao;

import pl.taw.infrastructure.petstore.Pet;

import java.util.Optional;

public interface PetDAO {

    Optional<Pet> getPet(final Long petId);

}
