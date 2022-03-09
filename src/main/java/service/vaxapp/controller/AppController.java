package service.vaxapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import service.vaxapp.model.ForumQuestion;
import service.vaxapp.repository.AdminRepository;
import service.vaxapp.repository.AppointmentRepository;
import service.vaxapp.repository.ForumAnswerRepository;
import service.vaxapp.repository.ForumQuestionRepository;
import service.vaxapp.repository.UserRepository;
import service.vaxapp.repository.VaccineCentreRepository;
import service.vaxapp.repository.VaccineRepository;
import service.vaxapp.repository.VaccineTypeRepository;

@Controller
public class AppController {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private ForumAnswerRepository forumAnswerRepository;
    @Autowired
    private ForumQuestionRepository forumQuestionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VaccineCentreRepository vaccineCentreRepository;
    @Autowired
    private VaccineRepository vaccineRepository;
    @Autowired
    private VaccineTypeRepository vaccineTypeRepository;

    @GetMapping("/")
    public String index(Model model) {
        // TODO - add DB retrieval logic
        return "index.html";
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        // TODO - add DB retrieval logic + authorization check
        return "statistics.html";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // TODO - add DB retrieval logic + authorization check
        return "dashboard.html";
    }

    @GetMapping("/login")
    public String login(Model model) {
        // TODO - add DB retrieval logic
        return "login.html";
    }

    @GetMapping("/register")
    public String register(Model model) {
        // TODO - add DB retrieval logic
        return "register.html";
    }

    @GetMapping("/forum")
    public String forum(Model model) {
        // Retrieve all questions and answers from database
        List<ForumQuestion> questions = forumQuestionRepository.findAll();
        model.addAttribute("questions", questions);
        // TODO
        // STEP 3. Add dynamic thymeleaf display of retrieved model
        // STEP 4. In frontend, if model is empty, display "There are no questions yet"
        return "forum.html";
    }

    @GetMapping("/ask-a-question")
    public String askAQuestion(Model model) {
        // TODO
        // If admin, return to index page
        // If user, return ask a question page
        return "ask-a-question.html";
    }

    @PostMapping("/ask-a-question")
    public String askAQuestion(@RequestBody Question question) {
        // TODO
        // STEP 1. retrieve question data
        // STEP 2. Retrieve current date in string format // not needed
        // STEP 3. Add question data to database
        // STEP 4. Return corresponding question page
        return "ask-a-question.html";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        // TODO - add DB retrieval logic + authorization check
        return "profile.html";
    }

    @GetMapping("/question")
    public String getQuestionById(@RequestParam(name = "id") Integer id, Model model) {
        // TODO
        // STEP 1. Add question details to model
        // STEP 2. Add answer details to model
        // STEP 3. If user, return question without answer functionality
        // STEP 3. If admin, return question with answer functionality
        // STEP 4. return model and correct page
        return "question.html";
    }

    @PostMapping("/question")
    public String answerQuestion(@RequestParam(name = "id") Integer id, @RequestBody Answer answer) {
        // TODO - check if authorized to answer (if session is admin)
        // If admin, get adminId
        // Add answer to databse for the question with id
        //
        return "";
    }

    /**
     * /########################
     * <p>
     * DTOs
     * </p>
     * /#######################
     */

    static class Question {
        public String title;
        public String details;
        public String dateSubmitted;

        public Question(String title, String details, String dateSubmitted) {
            this.title = title;
            this.details = details;
            this.dateSubmitted = dateSubmitted;
        }
    }

    static class Answer {
        public String body;
        public String dateSubmitted;

        public Answer(String body, String dateSubmitted) {
            this.body = body;
            this.dateSubmitted = dateSubmitted;
        }
    }
}