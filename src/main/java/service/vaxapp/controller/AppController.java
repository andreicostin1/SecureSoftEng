package service.vaxapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import service.vaxapp.model.Admin;
import service.vaxapp.model.ForumAnswer;
import service.vaxapp.model.ForumQuestion;
import service.vaxapp.model.User;
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
    @ResponseBody
    public String askAQuestion(@RequestBody Question question, Model model) {
        // Retrieve user account
        // TODO: retrieve user info from session instead
        // If user is logged in, allow question
        // Otherwise do not allow question
        Optional<User> user = userRepository.findById("1234567A");
        if (user.isPresent()) {
            ForumQuestion newQuestion = new ForumQuestion(question.title, question.details, question.dateSubmitted);
            newQuestion.setUser(user.get());
            // Add question to database
            forumQuestionRepository.save(newQuestion);
            model.addAttribute("question", newQuestion);
            return "question.html";
        }

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
        // Retrieve session info on user
        // STEP 3. If user, return question without answer functionality
        // STEP 3. If admin, return question with answer functionality
        Optional<ForumQuestion> question = forumQuestionRepository.findById(id);
        if (question.isPresent()) {
            model.addAttribute("question", question);
            return "question.html";
        }
        return "redirect:/forum";
    }

    @PostMapping("/question")
    public String answerQuestion(@RequestParam(name = "id") Integer id, @RequestBody Answer answer, Model model) {
        // TODO
        // Retrieve user account using session
        // If admin, add answer to databse for the question with id and save answer and
        // question updates to db
        // If user, do not allow answer & return redirect with error
        Optional<Admin> admin = adminRepository.findById("987654AB"); // TODO - use session instead
        Optional<ForumQuestion> question = forumQuestionRepository.findById(id);
        if (admin.isPresent() && question.isPresent()) {
            ForumAnswer newAnswer = new ForumAnswer(answer.body, answer.dateSubmitted);
            newAnswer.setAdmin(admin.get());
            newAnswer.setQuestion(question.get());
            question.get().addAnswer(newAnswer);
            // Save forum question and answer
            forumAnswerRepository.save(newAnswer);
            forumQuestionRepository.save(question.get());
            model.addAttribute("question", question);
            return "question.html";
        }

        return "ask-a-question.html";
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