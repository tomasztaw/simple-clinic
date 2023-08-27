package pl.taw.infrastructure.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@With
@Getter
@Setter
@EqualsAndHashCode(of = "visitId")
@ToString(of = {"visitId", "doctorId", "patientId", "note", "dateTime", "status"})
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

    @Column(name = "doctor_id")
    private Integer doctorId;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "doctor_id", nullable = true, referencedColumnName = "doctor_id", insertable = false, updatable = false)
    @JsonIgnore
    private DoctorEntity doctor;

    @Column(name = "patient_id")
    private Integer patientId;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "patient_id", nullable = true, referencedColumnName = "patient_id", insertable = false, updatable = false)
    @JsonIgnore
    private PatientEntity patient;

    @Column(name = "note")
    private String note;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "status")
    private String status;

    @OneToOne(mappedBy = "visit", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JsonIgnore
    private OpinionEntity opinion;

}
