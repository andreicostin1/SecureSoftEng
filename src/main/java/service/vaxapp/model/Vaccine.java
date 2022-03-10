package service.vaxapp.model;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToOne;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Vaccine {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

    @ManyToOne
    private User user;

    @Column(name = "date_received")
    private LocalDate dateReceived;

    // many-to-one relationship (Many vaccines may be given at one
    // vaccine centre)
    @ManyToOne(targetEntity = VaccineCentre.class, cascade = CascadeType.ALL)
    private VaccineCentre vaccineCentre;

    // Bidirectional many-to-one relationship (Many vaccines may be assigned by one
    // admin)
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User admin;

    public Vaccine() {
    }

    public Vaccine(LocalDate dateReceived) {
        this.dateReceived = dateReceived;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
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
