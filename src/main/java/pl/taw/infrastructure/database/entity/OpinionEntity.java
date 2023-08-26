package pl.taw.infrastructure.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@With
@Entity
@Builder
@ToString(of = {"opinionId", "doctorId", "patientId", "comment", "createdAt"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "opinions")
public class OpinionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opinion_id")
    private Integer opinionId;

    @Column(name = "doctor_id")
    private Integer doctorId;

    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "visit_id")
    private Integer visitId;

    @Column(name = "comment")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // relacje
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id", insertable = false, updatable = false)
    @JsonIgnore // adnotacja dla rekurencyjnego mapowania obiektów przez 'objectmappera'
    private DoctorEntity doctor;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", insertable = false, updatable = false)
    @JsonIgnore
    private PatientEntity patient;

    // id wizyty na początku zawsze będzie null, dlatego nie ustawiłem jako klucz obcy
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "visit_id", referencedColumnName = "visit_id", insertable = false, updatable = false)
    @JsonIgnore
    private VisitEntity visit;

}
