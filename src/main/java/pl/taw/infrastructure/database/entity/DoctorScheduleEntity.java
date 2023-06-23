package pl.taw.infrastructure.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Data
@With
@Entity
@Builder
@ToString(of = {"scheduleId", "doctorId", "dayOfTheWeek", "startTimeDs"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "doctors_schedule")
public class DoctorScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer scheduleId;

    @Column(name = "doctor_id")
    private Integer doctorId;

    @Column(name = "day_of_week")
    private Integer dayOfTheWeek;

    @Column(name = "start_time_ds")
    private LocalTime startTimeDs;

    @Column(name = "end_time_ds")
    private LocalTime endTimeDs;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id", insertable = false, updatable = false)
    private DoctorEntity doctor;

}
