package pl.taw.infrastructure.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@With
@Getter
@Setter
@EqualsAndHashCode(of = "patientId")
@ToString(of = {"patientId", "name", "surname", "pesel", "email"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patients")
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "pesel", unique = true)
    private String pesel;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "email", unique = true)
    private String email;

    // przy usunięciu pacjent, usuwane są też wizyty
    @OneToMany(mappedBy = "patient", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<VisitEntity> visits;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<OpinionEntity> createdOpinions;

    // dodajemy zwierzęta do konsumowania api
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "patient")
    private Set<PetEntity> pets;
}
