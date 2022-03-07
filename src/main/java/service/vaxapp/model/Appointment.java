package service.vaxapp.model;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "date_and_time")
    private LocalDateTime dateAndTime;
    @Column
    private Boolean doseNumber;
    @Column
    private String status;

    // Unidirectional one-to-one relationship (An appointment involves one type of
    // vaccine)
    @OneToOne(targetEntity = VaccineType.class, cascade = CascadeType.ALL)
    // @JoinColumn(name = "vaccine_type_id", referencedColumnName = "id")
    private VaccineType vaccineType;

    // Unidirectional one-to-one relationship (An appointment involves one vaccine
    // centre)
    @OneToOne(targetEntity = VaccineCentre.class, cascade = CascadeType.ALL)
    // @JoinColumn(name = "vaccine_centre_id", referencedColumnName = "id")
    private VaccineCentre vaccineCentre;

    // Bidirectional many-to-one relationship (A user may have multiple vaccine
    // appointments)
    @ManyToOne
    @JoinColumn(name = "user_pps", nullable = false)
    private User user;

    public Appointment() {
    }

    public Appointment(Integer id, LocalDateTime dateAndTime, Boolean doseNumber, String status) {
        this.id = id;
        this.dateAndTime = dateAndTime;
        this.doseNumber = doseNumber;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(LocalDateTime dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public Boolean getDoseNumber() {
        return doseNumber;
    }

    public void setDoseNumber(Boolean doseNumber) {
        this.doseNumber = doseNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public VaccineType getVaccineType() {
        return vaccineType;
    }

    public void setVaccineType(VaccineType vaccineType) {
        this.vaccineType = vaccineType;
    }

    public VaccineCentre getVaccineCentre() {
        return vaccineCentre;
    }

    public void setVaccineCentre(VaccineCentre vaccineCentre) {
        this.vaccineCentre = vaccineCentre;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
