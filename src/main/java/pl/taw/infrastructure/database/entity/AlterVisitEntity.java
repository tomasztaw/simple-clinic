package pl.taw.infrastructure.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "visitId")
@ToString(of = {"visitId", "doctor", "patient", "note", "dateTime", "status"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alter_visits")
public class AlterVisitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_id")
    private Integer visitId;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "doctor_id", nullable = true)
    private DoctorEntity doctor;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "patient_id", nullable = true)
    private PatientEntity patient;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "note")
    private String note;


    @Column(name = "status")
    private String status;

}
