package pl.taw.infrastructure.database.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.List;

@With
@Getter
@Setter
@EqualsAndHashCode(of = "doctorId")
@ToString(of = {"doctorId", "name", "surname", "title", "email"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doctors")
public class DoctorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Integer doctorId;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "title")
    private String title;

    @Column(name = "phone", unique = true)
    private String phone;

    @Email
    @Column(name = "email", unique = true)
    private String email;

    // przy usunięciu doktora, usuwane są też wizyty
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<VisitEntity> visits;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<DoctorScheduleEntity> schedules;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<OpinionEntity> opinions;

}
