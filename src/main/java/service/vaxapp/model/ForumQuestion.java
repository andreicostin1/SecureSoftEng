package service.vaxapp.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "forum_question")
public class ForumQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String body;

    @Column
    private String details;

    @Column(name = "date_submitted")
    private String dateSubmitted;

    // Bidirectional one-to-many relationship (One question may have many answers)
    @OneToMany(mappedBy = "question")
    private List<ForumAnswer> answers;

    // Bidirectional many-to-one relationship (Many questions may be asked by one
    // user)
    @ManyToOne()
    @JoinColumn(name = "user_pps", nullable = false)
    private User user;

    public ForumQuestion() {
    }

    public ForumQuestion(String body, String details, String dateSubmitted) {
        this.body = body;
        this.details = details;
        this.dateSubmitted = dateSubmitted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(String dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public List<ForumAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<ForumAnswer> answers) {
        this.answers = answers;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
