package pl.taw.infrastructure.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@With
@Entity
@Builder
@ToString(of = {"reservationId", "doctorId", "day", "startTimeR", "occupied"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservations")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer reservationId;

    @Column(name = "doctor_id")
    private Integer doctorId;

    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "day")
    private LocalDate day;

    @Column(name = "start_time_r")
    private LocalDateTime startTimeR;

    @Column(name = "occupied")
    private Boolean occupied;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id", insertable = false, updatable = false)
    private DoctorEntity doctor;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", insertable = false, updatable = false)
    private PatientEntity patient;

}
