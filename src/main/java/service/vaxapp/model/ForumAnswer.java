package service.vaxapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "forum_answer")
public class ForumAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String answer;

    // Bidirectional many-to-one relationship (A forum question may have multiple
    // answers)
    @ManyToOne
    @JoinColumn(name = "forum_question_id", nullable = false)
    private ForumQuestion question;

    // Bidirectional many-to-one relationship (An admin may give multiple answers)
    @ManyToOne
    @JoinColumn(name = "admin_pps", nullable = false)
    private Admin admin;

    public ForumAnswer() {
    }

    public ForumAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public ForumQuestion getQuestion() {
        return question;
    }

    public void setQuestion(ForumQuestion question) {
        this.question = question;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
}