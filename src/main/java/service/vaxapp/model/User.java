package service.vaxapp.model;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
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
    @Column(nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean admin = false;
    @Column
    private String password;
    @Transient
    private String passwordConfirm;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked = true;
     
    @Column(name = "failed_attempt")
    private int failedAttempt;
     
    @Column(name = "lock_time")
    private Date lockTime;

    // Bidirectional one-to-many relationship (One user may get multiple vaccines)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Vaccine> vaccines;

    // Bidirectional one-to-many relationship (One user may ask multiple forum
    // questions)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<ForumQuestion> questions;

    // Bidirectional one-to-many relationship (One user may be assigned multiple
    // appointments)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Appointment> appointments;

    public User() {
    }

    public User(String PPS, String fullName, String address, String phoneNumber, String email, String dateOfBirth,
            String nationality, String gender, Boolean admin, String password) {
        this.PPS = PPS;
        this.fullName = fullName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
        this.gender = gender;
        this.admin = admin;
        this.password = password;
        this.accountNonLocked = true;
        this.failedAttempt = 0;
    }

    public Integer getId() {
        return id;
    }

    public String getPPS() {
        return PPS;
    }

    public void setPPS(String PPS) {
        this.PPS = PPS;
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

    public int getAge() {
        LocalDate birthday = LocalDate.parse(this.dateOfBirth, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return Period.between(birthday, LocalDate.now()).getYears();
    }

    public int getAge(String dateOfBirth) {
        LocalDate birthday = LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return Period.between(birthday, LocalDate.now()).getYears();
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

    public Boolean isAdmin() {
        return admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    public int getFailedAttempt() {
        return this.failedAttempt;
    }

    public void setAccountNonLocked(boolean value) {
        this.accountNonLocked = value;
    }

    public void setFailedAttempt(int failedAttempt) {
        this.failedAttempt = failedAttempt;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }
}
