package pl.taw.api.dto;

import lombok.*;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetDTO {

    private Integer petId;
    private Long petStorePetId;
    private String name;
    private String category;

}
