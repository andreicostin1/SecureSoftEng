package service.vaxapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import service.vaxapp.model.AppointmentSlot;
import service.vaxapp.model.User;
import service.vaxapp.model.VaccineCentre;
import service.vaxapp.repository.AppointmentRepository;
import service.vaxapp.repository.AppointmentSlotRepository;
import service.vaxapp.repository.UserRepository;
import service.vaxapp.repository.VaccineCentreRepository;
import service.vaxapp.repository.VaccineRepository;

import java.time.LocalDate;
import java.time.LocalTime;

@SpringBootApplication
public class VaxApplication {
    public static void main(String[] args) {
        SpringApplication.run(VaxApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(VaccineCentreRepository vaccineCentreRepo, VaccineRepository vaccineRepo, AppointmentSlotRepository appointmentSlotRepo, UserRepository userRepo, AppointmentRepository appointmentRepo) {
        return args -> {
            System.out.println("VaxApp started");

            appointmentSlotRepo.deleteAll();
            appointmentRepo.deleteAll();
            vaccineRepo.deleteAll();
            vaccineCentreRepo.deleteAll();
            userRepo.deleteAll();

            User admin = new User("1234", "Vladolf Putler", "Kremlin", "", "admin@vaxapp.com", "07/10/1952", "Russian", "Male", true);
            User dragos = new User("123", "Dragos", "Bucharest", "", "dragos@vaxapp.com", "05/06/1999", "Romanian", "Male", false);
            userRepo.save(admin);
            userRepo.save(dragos);
            
            VaccineCentre vc1 = new VaccineCentre("RDS Vaccination Centre");
            VaccineCentre vc2 = new VaccineCentre("UCD Health Centre");
            vaccineCentreRepo.save(vc1);
            vaccineCentreRepo.save(vc2);

            AppointmentSlot as1 = new AppointmentSlot(vc1, LocalDate.of(2022, 4, 1), LocalTime.of(9, 0));
            AppointmentSlot as2 = new AppointmentSlot(vc1, LocalDate.of(2022, 4, 2), LocalTime.of(9, 15));
            AppointmentSlot as3 = new AppointmentSlot(vc1, LocalDate.of(2022, 4, 3), LocalTime.of(9, 30));

            AppointmentSlot as4 = new AppointmentSlot(vc2, LocalDate.of(2022, 4, 1), LocalTime.of(9, 0));
            AppointmentSlot as5 = new AppointmentSlot(vc2, LocalDate.of(2022, 4, 2), LocalTime.of(9, 15));
            AppointmentSlot as6 = new AppointmentSlot(vc2, LocalDate.of(2022, 4, 3), LocalTime.of(9, 30));

            appointmentSlotRepo.save(as1);
            appointmentSlotRepo.save(as2);
            appointmentSlotRepo.save(as3);
            appointmentSlotRepo.save(as4);
            appointmentSlotRepo.save(as5);
            appointmentSlotRepo.save(as6);
        };
    }
}
