package service.vaxapp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ForumAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
