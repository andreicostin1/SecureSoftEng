package service.vaxapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import service.vaxapp.UserSession;
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

    @Autowired
	private UserSession userSession;

    @GetMapping("/")
    public String index(Model model) {
        // TODO - add DB retrieval logic
        model.addAttribute("user", userSession);
        return "index.html";
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        // TODO - add DB retrieval logic + authorization check
        model.addAttribute("user", userSession);
        return "statistics.html";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // TODO - add DB retrieval logic + authorization check
        model.addAttribute("user", userSession);
        return "dashboard.html";
    }

    @GetMapping("/login")
    public String login(Model model) {
        // TODO - add DB retrieval logic
        model.addAttribute("user", userSession);
        return "login.html";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email, @RequestParam("pps") String pps) {
        // make sure the user is found in db by PPS and email
        User user = userRepository.findByCredentials(email, pps);
        if (user == null) {
            return "redirect:/login";
        }
        return "redirect:/";
    }

    @GetMapping("/register")
    public String register(Model model) {
        // TODO - add DB retrieval logic
        // model.addAttribute("user", userSession);
        return "register.html";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String register(User user) {
        if (userRepository.findByPPS(user.getPPS()) != null) {
            return "register.html";
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "register.html";
        }
        userRepository.save(user);
        return "login.html";
    }

    @GetMapping("/logout")
	public String logout() {
		userSession.setUserId(null);
		return "redirect:/";
	}

    @GetMapping("/forum")
    public String forum(Model model) {
        // TODO - add DB retrieval logic + authorization check
        model.addAttribute("user", userSession);
        return "forum.html";
    }

    @GetMapping("/ask-a-question")
    public String askAQuestion(Model model) {
        // TODO - add DB retrieval logic + authorization check
        model.addAttribute("user", userSession);
        return "ask-a-question.html";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        // TODO - add DB retrieval logic + authorization check
        model.addAttribute("user", userSession);
        return "profile.html";
    }

    @GetMapping("/question")
    public String question(Model model) {
        // TODO - add DB retrieval logic + authorization check + question id
        model.addAttribute("user", userSession);
        return "question.html";
    }

}