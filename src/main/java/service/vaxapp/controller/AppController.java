package service.vaxapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.vaxapp.model.User;
import service.vaxapp.repository.*;

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

    @PostMapping("/login")
    public String login(@RequestParam("email") String email, @RequestParam("pps") String pps) {
        // make sure the user is found in db by PPS number, and confirm email matches
        User login = userRepository.findUserByPPS(pps);
        if (login == null || !login.getEmail().equals(email)) {
            return "login.html";
        }
        return "index.html";
    }

    @GetMapping("/register")
    public String register(Model model) {
        // TODO - add DB retrieval logic
        return "register.html";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String register(User user) {
        if (userRepository.findUserByPPS(user.getPPS()) != null) {
            return "/index.html";
        }
        userRepository.save(user);
        return "/login.html";
    }

    @GetMapping("/forum")
    public String forum(Model model) {
        // TODO - add DB retrieval logic + authorization check
        return "forum.html";
    }

    @GetMapping("/ask-a-question")
    public String askAQuestion(Model model) {
        // TODO - add DB retrieval logic + authorization check
        return "ask-a-question.html";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        // TODO - add DB retrieval logic + authorization check
        return "profile.html";
    }

    @GetMapping("/question")
    public String question(Model model) {
        // TODO - add DB retrieval logic + authorization check + question id
        return "question.html";
    }

}