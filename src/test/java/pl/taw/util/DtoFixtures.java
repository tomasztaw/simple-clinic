package pl.taw.util;

import lombok.experimental.UtilityClass;
import pl.taw.api.dto.DoctorDTO;
import pl.taw.api.dto.PatientDTO;

import java.util.List;

@UtilityClass
public class DtoFixtures {

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

    public static List<DoctorDTO> doctors = List.of(someDoctor1(), someDoctor2(), someDoctor3());
}
