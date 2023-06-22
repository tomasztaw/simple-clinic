package pl.taw.util;

import lombok.experimental.UtilityClass;
import pl.taw.infrastructure.database.entity.DoctorEntity;
import pl.taw.infrastructure.database.entity.PatientEntity;
import pl.taw.infrastructure.database.entity.VisitEntity;
//import pl.zajavka.infrastructure.database.entity.CarToBuyEntity;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class EntityFixtures {

    public static List<PatientEntity> somePatientList = List.of(
            somePatient1(), somePatient2(), somePatient3(), somePatient4(), somePatient5());

    public static List<DoctorEntity> someDoctorList = List.of(
            someDoctor1(), someDoctor2(), someDoctor3(), someDoctor4(), someDoctor5(), someDoctor6(), someDoctor7());

    public static List<VisitEntity> someVisitList = List.of(
            someVisit1(), someVisit2(), someVisit3(), someVisit4(), someVisit5());

    public static PatientEntity somePatient1() {
        return PatientEntity.builder()
                .patientId(1)
                .name("Agata")
                .surname("Andrzejewska")
                .pesel("700011012345")
                .email("aa@gmail.com")
                .phone("+48 220 221 222")
                .build();
    }

    public static PatientEntity somePatient2() {
        return PatientEntity.builder()
                .patientId(2)
                .name("Wojciech")
                .surname("Suchodolski")
                .pesel("72072514777")
                .email("suchy@gmail.com")
                .phone("+48 258 369 147")
                .build();
    }

    public static PatientEntity somePatient3() {
        return PatientEntity.builder()
                .patientId(3)
                .name("Stefan")
                .surname("Zajavka")
                .pesel("72072514725")
                .email("zajavka@zajavka.pl")
                .phone("+48 548 664 441")
                .build();
    }

    public static PatientEntity somePatient4() {
        return PatientEntity.builder()
                .patientId(4)
                .name("Agnieszka")
                .surname("Spring")
                .pesel("85061718342")
                .email("aga@zajavka.pl")
                .phone("+48 548 115 312")
                .build();
    }

    public static PatientEntity somePatient5() {
        return PatientEntity.builder()
                .patientId(5)
                .name("Tomasz")
                .surname("Hibernate")
                .pesel("85061718378")
                .email("hibertomasz@hibernate.com")
                .phone("+48 548 656 565")
                .build();
    }

    public static DoctorEntity someDoctor1() {
        return DoctorEntity.builder()
                .doctorId(1)
                .name("Alojzy")
                .surname("Kowalski")
                .title("Laryngolog")
                .email("alojzykowalski@eclinic.pl")
                .phone("+48 120 121 122")
                .build();
    }

    public static DoctorEntity someDoctor2() {
        return DoctorEntity.builder()
                .doctorId(2)
                .name("Anna")
                .surname("Nowak")
                .title("Kardiolog")
                .email("annanowak@eclinic.pl")
                .phone("+48 120 130 140")
                .build();
    }

    public static DoctorEntity someDoctor3() {
        return DoctorEntity.builder()
                .doctorId(3)
                .name("Kornel")
                .surname("Makuszyński")
                .title("Lekarz rodzinny")
                .email("kornel@eclinic.pl")
                .phone("+48 120 130 142")
                .build();
    }

    public static DoctorEntity someDoctor4() {
        return DoctorEntity.builder()
                .doctorId(4)
                .name("Jadwiga")
                .surname("Kuszyńska")
                .title("Pediatra")
                .email("jkuszynska@eclinic.pl")
                .phone("+48 120 130 148")
                .build();
    }

    public static DoctorEntity someDoctor5() {
        return DoctorEntity.builder()
                .doctorId(5)
                .name("Wacław")
                .surname("Piątkowski")
                .title("Gastrolog")
                .email("wacek@eclinic.pl")
                .phone("+48 120 130 150")
                .build();
    }

    public static DoctorEntity someDoctor6() {
        return DoctorEntity.builder()
                .doctorId(6)
                .name("Aleksander")
                .surname("Newski")
                .title("Gastrolog")
                .email("oleknew@eclinic.pl")
                .phone("+48 120 130 152")
                .build();
    }

    public static DoctorEntity someDoctor7() {
        return DoctorEntity.builder()
                .doctorId(7)
                .name("Urszula")
                .surname("Nowakowska")
                .title("Lekarz rodzinny")
                .email("ulala@eclinic.pl")
                .phone("+48 120 130 158")
                .build();
    }

    public static VisitEntity someVisit1() {
        return VisitEntity.builder()
                .visitId(1)
                .doctor(someDoctor1())
                .patient(somePatient1())
                .dateTime(LocalDateTime.of(2023, 6, 1, 8,30, 0))
                .note("Pacjent bardzo chory")
                .status("odbyta")
                .build();
    }

    public static VisitEntity someVisit2() {
        return VisitEntity.builder()
                .visitId(2)
                .doctor(someDoctor2())
                .patient(somePatient2())
                .dateTime(LocalDateTime.of(2023, 6, 1, 8,30, 0))
                .note("Chore zatoki, zwolnienie L4")
                .status("odbyta")
                .build();
    }

    public static VisitEntity someVisit3() {
        return VisitEntity.builder()
                .visitId(3)
                .doctor(someDoctor3())
                .patient(somePatient3())
                .dateTime(LocalDateTime.of(2023, 6, 1, 10,0, 0))
                .note("Przedawkowanie opiatów")
                .status("odbyta")
                .build();
    }

    public static VisitEntity someVisit4() {
        return VisitEntity.builder()
                .visitId(4)
                .doctor(someDoctor4())
                .patient(somePatient4())
                .dateTime(LocalDateTime.of(2023, 6, 1, 12,30, 0))
                .note("Pacjent symuluje chorobę")
                .status("odbyta")
                .build();
    }

    public static VisitEntity someVisit5() {
        return VisitEntity.builder()
                .visitId(5)
                .doctor(someDoctor5())
                .patient(somePatient5())
                .dateTime(LocalDateTime.of(2023, 6, 2, 8,30, 0))
                .note("Chore nerki, zwolnienie L4")
                .status("odbyta")
                .build();
    }


}
