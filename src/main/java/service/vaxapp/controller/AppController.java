package service.vaxapp.controller;

import java.util.Optional;

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
        if (!userSession.isLoggedIn()) return "redirect:/login";
        if (!userSession.getUser().isAdmin()) return "redirect:/";

        // TODO - add DB retrieval logic + authorization check
        model.addAttribute("userSession", userSession);
        return "statistics.html";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        if (!userSession.isLoggedIn()) return "redirect:/login";
        if (!userSession.getUser().isAdmin()) return "redirect:/";
        
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
        // TODO - add DB retrieval logic + authorization check
        model.addAttribute("userSession", userSession);
        return "forum.html";
    }

    @GetMapping("/ask-a-question")
    public String askAQuestion(Model model) {
        // TODO - add DB retrieval logic + authorization check
        model.addAttribute("userSession", userSession);
        return "ask-a-question.html";
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
        if (stringId == null) return "404";
        
        try {
            Integer id = Integer.valueOf(stringId);
            Optional<User> user = userRepository.findById(id);
            
            if (!user.isPresent()) {
                return "404";
            }

            model.addAttribute("userSession", userSession);
            model.addAttribute("profile", user.get());
            return "profile";
        }
        catch (NumberFormatException ex){
            return "404";
        }
    }

    @GetMapping("/question")
    public String question(Model model) {
        // TODO - add DB retrieval logic + authorization check + question id
        model.addAttribute("userSession", userSession);
        return "question.html";
    }

}