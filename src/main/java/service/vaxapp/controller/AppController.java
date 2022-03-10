package service.vaxapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.vaxapp.UserSession;
import service.vaxapp.model.Appointment;
import service.vaxapp.model.AppointmentSlot;
import service.vaxapp.model.User;
import service.vaxapp.model.Vaccine;
import service.vaxapp.repository.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


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
    private AppointmentSlotRepository appointmentSlotRepository;

    @Autowired
    private UserSession userSession;

    @GetMapping("/")
    public String index(Model model) {
        ArrayList<AppointmentSlot> appSlots = (ArrayList<AppointmentSlot>) appointmentSlotRepository.findAll();
        model.addAttribute("appSlots", appSlots);
        model.addAttribute("userSession", userSession);
        return "index";
    }

    @PostMapping(value = "/make-appointment")
    public String makeAppointment(@RequestParam Map<String, String> body, Model model) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/login";
        }

        // A user shouldn't have more than one pending appointment
        if (appointmentRepository.findPending(userSession.getUserId()) != null) {
            return "redirect:/";
        }

        Integer centerId = Integer.valueOf(body.get("center_id"));
        LocalDate date = LocalDate.parse(body.get("date"));
        LocalTime time = LocalTime.parse(body.get("time"));

        AppointmentSlot appSlot = appointmentSlotRepository.findByDetails(centerId, date, time);
        if (appSlot == null) {
            return "redirect:/";
        }

        Appointment app = new Appointment(appSlot.getVaccineCentre(), appSlot.getDate(), appSlot.getStartTime(), userSession.getUser(), "pending");
        appointmentRepository.save(app);
        appointmentSlotRepository.delete(appSlot);

        return "redirect:/profile";
    }

    @GetMapping("/stats")
    public String statistics(Model model) {
        //if (!userSession.isLoggedIn()) return "redirect:/login";
        //if (!userSession.getUser().isAdmin()) return "redirect:/";

        // TODO - add DB retrieval logic + authorization check
        model.addAttribute("userSession", userSession);
        model.addAttribute("totalDoses", vaccineRepository.count());
        model.addAttribute("dosesByNationality", userRepository.countByNationality("Ireland").size());
        model.addAttribute("country", "Ireland");
        getVaccineStatsByGender(model);
        return "stats.html";
    }

    private void getVaccineStatsByGender(Model model) {
        int male = 0;
        int female = 0;

        for (Vaccine v : vaccineRepository.findAll()) {
            if (v.getUser().getGender() == "male") {
                male++;
            } else {
                female++;
            }
        }

        model.addAttribute("maleDosePercent", male / userRepository.count());
        model.addAttribute("femaleDosePercent", female / userRepository.count());
    }

    @PostMapping("/stats")
    public String statistics(Model model, @RequestParam("nationality") String country) {
        model.addAttribute("userSession", userSession);
        model.addAttribute("totalDoses", vaccineRepository.count());
        model.addAttribute("dosesByNationality", userRepository.countByNationality(country).size());
        model.addAttribute("country", country);
        getVaccineStatsByGender(model);
        return "stats.html";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        if (!userSession.isLoggedIn()) return "redirect:/login";
        if (!userSession.getUser().isAdmin()) return "redirect:/";

        model.addAttribute("userSession", userSession);
        return "dashboard";
    }

    @GetMapping("/login")
    public String login(Model model) {
        // TODO - add DB retrieval logic
        model.addAttribute("userSession", userSession);
        return "login";
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
        return "register";
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
        return "forum";
    }

    @GetMapping("/ask-a-question")
    public String askAQuestion(Model model) {
        // TODO - add DB retrieval logic + authorization check
        model.addAttribute("userSession", userSession);
        return "ask-a-question";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        // TODO - add DB retrieval logic + authorization check
        if (!userSession.isLoggedIn()) {
            return "redirect:/login";
        }

        List<Appointment> apps = appointmentRepository.findByUser(userSession.getUserId());

        model.addAttribute("appointments", apps);
        model.addAttribute("userSession", userSession);
        return "profile";
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
        } catch (NumberFormatException ex) {
            return "404";
        }
    }

    @GetMapping("/question")
    public String question(Model model) {
        // TODO - add DB retrieval logic + authorization check + question id
        model.addAttribute("userSession", userSession);
        return "question";
    }

}