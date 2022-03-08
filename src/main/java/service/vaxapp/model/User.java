package service.vaxapp.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_pps", unique = true)
    private String PPS;
    @Column(name = "full_name")
    private String fullName;
    @Column
    private String address;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column
    private String email;
    @Column(name = "date_of_birth")
    private String dateOfBirth;
    @Column
    private String nationality;
    @Column
    private String gender;

    // Bidirectional one-to-many relationship (One user may get multiple vaccines)
    @OneToMany(mappedBy = "user")
    private List<Vaccine> vaccines;

    // Bidirectional one-to-many relationship (One user may ask multiple forum
    // questions)
    @OneToMany(mappedBy = "user")
    private List<ForumQuestion> questions;

    // Bidirectional one-to-many relationship (One user may be assigned multiple
    // appointments)
    @OneToMany(mappedBy = "user")
    private List<Appointment> appointments;

    public User() {
    }

    public User(String PPS, String fullName, String address, String phoneNumber, String email, String dateOfBirth,
            String nationality, String gender) {
        this.PPS = PPS;
        this.fullName = fullName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
        this.gender = gender;
    }

    public String getPPS() {
        return PPS;
    }

    public void setPPS(String pPS) {
        PPS = pPS;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
