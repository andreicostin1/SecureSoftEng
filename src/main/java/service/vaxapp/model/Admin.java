package service.vaxapp.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Admin {
    @Id
    @Column(name = "admin_pps")
    private String PPS;
    @Column
    private String name;
    @Column
    private String email;

    // Bidirectional one-to-many relationship (One admin may assign many vaccines)
    @OneToMany(mappedBy = "admin")
    private List<Vaccine> vaccines;

    // Bidirectional one-to-many relationship (One admin may answer many questions)
    @OneToMany(mappedBy = "admin")
    private List<ForumAnswer> answers;

    public Admin() {
    }

    public Admin(String PPS, String name, String email) {
        this.PPS = PPS;
        this.name = name;
        this.email = email;
    }

    public String getPPS() {
        return PPS;
    }

    public void setPPS(String pPS) {
        PPS = pPS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Vaccine> getVaccines() {
        return vaccines;
    }

    public void setVaccines(List<Vaccine> vaccines) {
        this.vaccines = vaccines;
    }

    public List<ForumAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<ForumAnswer> answers) {
        this.answers = answers;
    }
}
