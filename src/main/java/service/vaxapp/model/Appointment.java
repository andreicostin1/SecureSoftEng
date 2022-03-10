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

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date_and_time")
    private LocalDateTime dateAndTime;

    @Column
    private String status;

    // manye-to-one relationship (many appointments can take place in a centre)
    @ManyToOne(targetEntity = VaccineCentre.class, cascade = CascadeType.ALL)
    private VaccineCentre vaccineCentre;

    // Bidirectional many-to-one relationship (A user may have multiple vaccine
    // appointments)
    @ManyToOne
    @JoinColumn(name = "user_pps", nullable = false)
    private User user;

    public Appointment() {
    }

    public Appointment(Integer id, LocalDateTime dateAndTime, String status) {
        this.id = id;
        this.dateAndTime = dateAndTime;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
