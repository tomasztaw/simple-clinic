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
@Table(name = "visits")
public class VisitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_id")
    private Integer visitId;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "doctor_id", nullable = true)
    private DoctorEntity doctor;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "patient_id", nullable = true)
    private PatientEntity patient;

    @Column(name = "note")
    private String note;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "status")
    private String status;

    // relacje
//    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH})
//    @JoinColumn(name = "doctor_id", insertable = false, updatable = false)
//    private DoctorEntity doctor;
//
//    // przy usuwaniu wizyty, doktor/pacjent zostaje
//    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH})
//    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
//    private PatientEntity patient;

}
