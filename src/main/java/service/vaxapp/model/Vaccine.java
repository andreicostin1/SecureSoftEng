package service.vaxapp.model;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@Entity
public class Vaccine {
    @EmbeddedId
    private VaccineId vaccineId;

    @ManyToOne
    @MapsId("userPPS")
    private User user;

    @Column(name = "date_received")
    private LocalDate dateReceived;

    // Unidirectional one-to-one relationship (One vaccine may be of one vaccine
    // type)
    @OneToOne(targetEntity = VaccineType.class, cascade = CascadeType.ALL)
    private VaccineType vaccineType;

    // Unidirectional one-to-one relationship (One vaccine may be given at one
    // vaccine centre)
    @OneToOne(targetEntity = VaccineCentre.class, cascade = CascadeType.ALL)
    private VaccineCentre vaccineCentre;

    // Bidirectional many-to-one relationship (Many vaccines may be assigned by one
    // admin)
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "admin_pps")
    private Admin admin;

    public Vaccine() {
    }

    public Vaccine(LocalDate dateReceived) {
        this.dateReceived = dateReceived;
    }

    public VaccineId getVaccineId() {
        return vaccineId;
    }

    public void setVaccineId(VaccineId vaccineId) {
        this.vaccineId = vaccineId;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
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

    public LocalDate getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(LocalDate dateReceived) {
        this.dateReceived = dateReceived;
    }
}
