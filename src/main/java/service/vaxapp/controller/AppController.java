package service.vaxapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import service.vaxapp.model.ForumAnswer;
import service.vaxapp.model.ForumQuestion;
import service.vaxapp.model.User;
import service.vaxapp.repository.AppointmentRepository;
import service.vaxapp.repository.ForumAnswerRepository;
import service.vaxapp.repository.ForumQuestionRepository;
import service.vaxapp.repository.UserRepository;
import service.vaxapp.repository.VaccineCentreRepository;
import service.vaxapp.repository.VaccineRepository;
import service.vaxapp.repository.VaccineTypeRepository;
import org.springframework.web.bind.annotation.*;

import service.vaxapp.UserSession;
import service.vaxapp.model.User;
import service.vaxapp.repository.*;

@Controller
public class AppController {
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

    @Autowired
    private UserSession userSession;

    @GetMapping("/")
    public String index(Model model) {
        // TODO - add DB retrieval logic
        model.addAttribute("userSession", userSession);
        return "index.html";
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        // TODO - add DB retrieval logic + authorization check
        model.addAttribute("userSession", userSession);
        return "statistics.html";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        if (!userSession.isLoggedIn())
            return "redirect:/login";
        if (!userSession.getUser().isAdmin())
            return "redirect:/";

        model.addAttribute("userSession", userSession);
        return "dashboard.html";
    }

    @GetMapping("/login")
    public String login(Model model) {
        // TODO - add DB retrieval logic
        model.addAttribute("userSession", userSession);
        return "login.html";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email, @RequestParam("pps") String pps) {
        // make sure the user is found in db by PPS and email
        User user = userRepository.findByCredentials(email, pps);
        if (user == null) {
            return "redirect:/login";
        }
        userSession.setUserId(user.getId());
        return "redirect:/";
    }

    @GetMapping("/register")
    public String register(Model model) {
        // TODO - add DB retrieval logic
        model.addAttribute("userSession", userSession);
        return "register.html";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String register(User user) {
        if (userRepository.findByPPS(user.getPPS()) != null) {
            return "redirect:/register";
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "redirect:/register";
        }
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout() {
        userSession.setUserId(null);
        return "redirect:/";
    }

    @GetMapping("/forum")
    public String forum(Model model) {
        // Retrieve all questions and answers from database
        List<ForumQuestion> questions = forumQuestionRepository.findAll();
        model.addAttribute("questions", questions);
        // TODO - add DB retrieval logic + authorization check
        model.addAttribute("userSession", userSession);
        return "forum.html";
    }

    @GetMapping("/ask-a-question")
    public String askAQuestion(Model model) {
        // TODO
        // If admin, return to index page
        // If user, return ask a question page
        model.addAttribute("userSession", userSession);
        return "ask-a-question.html";
    }

    @PostMapping("/ask-a-question")
    @ResponseBody
    public Integer askAQuestion(@RequestBody Question question, Model model) {
        // User not logged in || User is Admin
        if (!userSession.isLoggedIn() || userSession.getUser().isAdmin()) {
            return null;
        }

        // Create new question entry in db
        ForumQuestion newQuestion = new ForumQuestion(question.title, question.details, question.dateSubmitted);
        newQuestion.setUser(userSession.getUser());

        // Add question to database
        forumQuestionRepository.save(newQuestion);

        // Return new question
        return newQuestion.getId();
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        // TODO - add DB retrieval logic + authorization check
        if (!userSession.isLoggedIn()) {
            return "redirect:/login";
        }

        model.addAttribute("userSession", userSession);
        return "profile.html";
    }

    @GetMapping("/profile/{stringId}")
    public String profile(@PathVariable String stringId, Model model) {
        if (stringId == null)
            return "404";

        try {
            Integer id = Integer.valueOf(stringId);
            Optional<User> user = userRepository.findById(id);

            if (!user.isPresent()) {
                return "404";
            }

            model.addAttribute("userSession", userSession);
            model.addAttribute("profile", user.get());
            return "profile";
        } catch (NumberFormatException ex) {
            return "404";
        }
    }

    @GetMapping("/question")
    public String getQuestionById(@RequestParam(name = "id") Integer id, Model model) {
        Optional<ForumQuestion> question = forumQuestionRepository.findById(id);
        if (question.isPresent()) {
            model.addAttribute("question", question.get());
            model.addAttribute("userSession", userSession);
            return "question.html";
        } else {
            return "redirect:/forum";
        }
    }

    // @PostMapping("/question")
    // public String answerQuestion(@RequestParam(name = "id") Integer id,
    // @RequestBody Answer answer, Model model) {
    // // TODO
    // // Retrieve user account using session
    // // If admin, add answer to databse for the question with id and save answer
    // and
    // // question updates to db
    // // If user, do not allow answer & return redirect with error
    // Optional<Admin> admin = adminRepository.findById("987654AB"); // TODO - use
    // session instead
    // Optional<ForumQuestion> question = forumQuestionRepository.findById(id);
    // if (admin.isPresent() && question.isPresent()) {
    // ForumAnswer newAnswer = new ForumAnswer(answer.body, answer.dateSubmitted);
    // newAnswer.setAdmin(admin.get());
    // newAnswer.setQuestion(question.get());
    // question.get().addAnswer(newAnswer);
    // // Save forum question and answer
    // forumAnswerRepository.save(newAnswer);
    // forumQuestionRepository.save(question.get());
    // model.addAttribute("question", question);
    // return "question.html";
    // }

    // return "ask-a-question.html";
    // }

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