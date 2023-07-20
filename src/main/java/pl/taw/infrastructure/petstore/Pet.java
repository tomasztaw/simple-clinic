package pl.taw.infrastructure.petstore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.taw.infrastructure.petstore.model.Category;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pet {

    private Long id;
    private String name;
//    private String status;
    private String category;

}
