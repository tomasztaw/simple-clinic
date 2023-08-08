package pl.taw.api.dto;

import org.junit.jupiter.api.Test;
import pl.taw.util.DtoFixtures;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DoctorsDTOTest {

    @Test
    public void testDoctorsDTO() {
        // given
        DoctorDTO doctor1 = DtoFixtures.someDoctor1().withName("Lucjan").withSurname("Skórny").withTitle("Dermatolog");
        DoctorDTO doctor2 = DtoFixtures.someDoctor2().withName("Adam").withSurname("Sercowy").withTitle("Kardiolog");

        List<DoctorDTO> doctorList = new ArrayList<>();
        doctorList.add(doctor1);
        doctorList.add(doctor2);

        // when
        DoctorsDTO doctorsDTO = DoctorsDTO.builder()
                .doctors(doctorList)
                .build();

        // Then
        assertNotNull(doctorsDTO);
        assertEquals(2, doctorsDTO.getDoctors().size());
        assertEquals("Dermatolog", doctorsDTO.getDoctors().get(0).getTitle());
        assertEquals("Kardiolog", doctorsDTO.getDoctors().get(1).getTitle());
        assertEquals("Lucjan", doctorsDTO.getDoctors().get(0).getName());
        assertEquals("Skórny", doctorsDTO.getDoctors().get(0).getSurname());
        assertEquals("Adam", doctorsDTO.getDoctors().get(1).getName());
    }

}