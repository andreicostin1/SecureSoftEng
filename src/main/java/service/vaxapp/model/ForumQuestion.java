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
    private String question;

    // Bidirectional one-to-many relationship (One question may have many answers)
    @OneToMany(mappedBy = "question")
    private List<ForumAnswer> answers;

    // Bidirectional many-to-one relationship (Many questions may be asked by one
    // user)
    @ManyToOne()
    private User user;

    public ForumQuestion() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
