package pl.taw.util;

import lombok.experimental.UtilityClass;
import pl.taw.api.dto.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@UtilityClass
public class DtoFixtures {


    public static List<PatientDTO> patients = List.of(somePatient1(), somePatient2());
    public static List<DoctorDTO> doctors = List.of(someDoctor1(), someDoctor2(), someDoctor3());
    public static List<OpinionDTO> opinions = List.of(someOpinion1(), someOpinion2());
    public static List<VisitDTO> visits = List.of(someVisit1(), someVisit2());
    public static VisitsDTO visitsDTO = VisitsDTO.of(visits);
    public static List<ReservationDTO> reservations = List.of(someReservation1(), someReservation2(), someReservation3());
    public static ReservationsDTO reservationsDTO = ReservationsDTO.of(reservations);

    public static PatientDTO somePatient1() {
        return PatientDTO.builder()
                .name("Agnieszka")
                .surname("Zajavkowa")
                .pesel("85061712345")
                .phone("+48 548 665 441")
                .email("aga@zajavka.com")
                .build();
    }

    public static PatientDTO somePatient2() {
        return PatientDTO.builder()
                .name("Tomasz")
                .surname("Bednarek")
                .pesel("85061712345")
                .phone("+48 548 200 488")
                .email("tomala@zajavka.com")
                .build();
    }

    public static DoctorDTO someDoctor1() {
        return DoctorDTO.builder()
                .doctorId(1)
                .name("Alojzy")
                .surname("Popiołek")
                .title("Lekarz rodzinny")
                .phone("+48 120 100 101")
                .email("alojzypopiolek@eclinic.pl")
                .build();
    }
    public static DoctorDTO someDoctor2() {
        return DoctorDTO.builder()
                .doctorId(2)
                .name("Kornelia")
                .surname("Zależna")
                .title("Lekarz rodzinny")
                .phone("+48 120 100 102")
                .email("korneliazalezna@eclinic.pl")
                .build();
    }
    public static DoctorDTO someDoctor3() {
        return DoctorDTO.builder()
                .doctorId(3)
                .name("Tomasz")
                .surname("Piasecki")
                .title("Psychiatra")
                .phone("+48 120 100 102")
                .email("tomaszpiasecki@eclinic.pl")
                .build();
    }

    public static DoctorDTO invalidDoctor() {
        return DoctorDTO.builder()
                .doctorId(3)
                .name("Adam")
                .surname("Kowalski")
                .title("Lekarz")
                .phone("+48 120 100 102 102")
                .email("tomaszpiasecki#eclinic.pl")
                .build();
    }

    public static OpinionDTO someOpinion1() {
        return OpinionDTO.builder()
                .opinionId(1)
                .doctorId(1)
                .doctor(someDoctor1())
                .patientId(1)
                .patient(somePatient1())
                .visitId(1)
                .comment("Opinia napisania w pochlebnym tonie")
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    public static OpinionDTO someOpinion2() {
        return OpinionDTO.builder()
                .opinionId(2)
                .doctorId(2)
                .doctor(someDoctor2())
                .patientId(2)
                .patient(somePatient2())
                .visitId(2)
                .comment("Opinia napisania w nie pochlebnym tonie")
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    public static VisitDTO someVisit1() {
        return VisitDTO.builder()
                .visitId(1)
                .doctorId(5)
                .patientId(5)
                .note("Pacjent będzie żył")
                .dateTime(LocalDateTime.now())
                .status("odbyta")
                .build();
    }

    public static VisitDTO someVisit2() {
        return VisitDTO.builder()
                .visitId(2)
                .doctorId(1)
                .patientId(5)
                .note("Będzie co będzie")
                .dateTime(LocalDateTime.now())
                .status("odbyta")
                .build();
    }

    public static ReservationDTO someReservation1() {
        return ReservationDTO.builder()
                .reservationId(1)
                .doctorId(1)
                .patientId(1)
                .day(LocalDate.of(2023, 8, 4))
                .startTimeR(LocalTime.of(12, 10, 0))
                .occupied(true)
                .build();
    }
    public static ReservationDTO someReservation2() {
        return ReservationDTO.builder()
                .reservationId(2)
                .doctorId(2)
                .patientId(2)
                .day(LocalDate.of(2023, 8, 4))
                .startTimeR(LocalTime.of(13, 10, 0))
                .occupied(true)
                .build();
    }
    public static ReservationDTO someReservation3() {
        return ReservationDTO.builder()
                .reservationId(3)
                .doctorId(5)
                .patientId(5)
                .day(LocalDate.of(2023, 8, 4))
                .startTimeR(LocalTime.of(13, 30, 0))
                .occupied(true)
                .build();
    }

    public static VisitDTO someVisit4() {
        return VisitDTO.builder()
                .visitId(1)
                .doctor(someDoctor1())
                .patient(somePatient1())
                .dateTime(LocalDateTime.of(2023, 6, 1, 8,30, 0))
                .note("Pacjent bardzo chory")
                .status("odbyta")
                .build();
    }

    public static DoctorScheduleDTO someSchedule1() {
        return DoctorScheduleDTO.builder()
                .scheduleId(1)
                .doctorId(1)
                .dayOfTheWeek(1)
                .startTimeDs(LocalTime.of(10, 0))
                .endTimeDs(LocalTime.of(14, 0))
                .build();
    }

    public static DoctorScheduleDTO someSchedule2() {
        return DoctorScheduleDTO.builder()
                .scheduleId(2)
                .doctorId(1)
                .dayOfTheWeek(2)
                .startTimeDs(LocalTime.of(10, 0))
                .endTimeDs(LocalTime.of(14, 0))
                .build();
    }

    public static DoctorScheduleDTO someSchedule3() {
        return DoctorScheduleDTO.builder()
                .scheduleId(3)
                .doctorId(5)
                .dayOfTheWeek(3)
                .startTimeDs(LocalTime.of(8, 0))
                .endTimeDs(LocalTime.of(12, 0))
                .build();
    }

    public static DoctorScheduleDTO someSchedule4() {
        return DoctorScheduleDTO.builder()
                .scheduleId(4)
                .doctorId(5)
                .dayOfTheWeek(5)
                .startTimeDs(LocalTime.of(11, 0))
                .endTimeDs(LocalTime.of(15, 0))
                .build();
    }


}
