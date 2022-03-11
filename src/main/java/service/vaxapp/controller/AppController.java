package service.vaxapp.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import service.vaxapp.model.ForumAnswer;
import service.vaxapp.model.ForumQuestion;
import service.vaxapp.model.User;
import service.vaxapp.repository.AppointmentRepository;
import service.vaxapp.repository.ForumAnswerRepository;
import service.vaxapp.repository.ForumQuestionRepository;
import service.vaxapp.repository.UserRepository;
import service.vaxapp.repository.VaccineCentreRepository;
import service.vaxapp.repository.VaccineRepository;
import org.springframework.web.bind.annotation.*;
import service.vaxapp.UserSession;
import service.vaxapp.model.Appointment;
import service.vaxapp.model.AppointmentSlot;
import service.vaxapp.model.Vaccine;
import service.vaxapp.model.VaccineCentre;
import service.vaxapp.repository.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

        // sort time slots by center and date
        Collections.sort(appSlots, new Comparator<AppointmentSlot>() {
            public int compare(AppointmentSlot o1, AppointmentSlot o2) {
                if (o1.getVaccineCentre().getName() == o2.getVaccineCentre().getName()) {
                    if (o1.getDate() == o2.getDate())
                        return o1.getStartTime().compareTo(o2.getStartTime());
                    return o1.getDate().compareTo(o2.getDate());
                }

                return o1.getVaccineCentre().getName().compareTo(o2.getVaccineCentre().getName());
            }
        });

        model.addAttribute("appSlots", appSlots);
        model.addAttribute("userSession", userSession);
        return "index";
    }

    @PostMapping(value = "/make-appointment")
    public String makeAppointment(@RequestParam Map<String, String> body, Model model,
            RedirectAttributes redirectAttributes) {
        if (!userSession.isLoggedIn()) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to make an appointment.");
            return "redirect:/login";
        }

        // A user shouldn't have more than one pending appointment
        if (appointmentRepository.findPending(userSession.getUserId()) != null) {
            redirectAttributes.addFlashAttribute("error",
                    "You can only have one pending appointment at a time. Please check your appointment list.");
            return "redirect:/";
        }

        Integer centerId = Integer.valueOf(body.get("center_id"));
        LocalDate date = LocalDate.parse(body.get("date"));
        LocalTime time = LocalTime.parse(body.get("time"));

        AppointmentSlot appSlot = appointmentSlotRepository.findByDetails(centerId, date, time);
        if (appSlot == null) {
            redirectAttributes.addFlashAttribute("error", "The appointment slot you selected is no longer available.");
            return "redirect:/";
        }

        Appointment app = new Appointment(appSlot.getVaccineCentre(), appSlot.getDate(), appSlot.getStartTime(),
                userSession.getUser(), "pending");
        appointmentRepository.save(app);
        appointmentSlotRepository.delete(appSlot);

        redirectAttributes.addFlashAttribute("success",
                "Your appointment has been made! Please see the details of your new appointment.");
        return "redirect:/profile";
    }

    @PostMapping(value = "/assign-vaccine")
    public String assignVaccine(@RequestParam Map<String, String> body, Model model,
            RedirectAttributes redirectAttributes) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/login";
        }

        if (!userSession.getUser().isAdmin()) {
            // Hacks detected!
            return "redirect:/login";
        }

        LocalDate vaxDate = LocalDate.parse(body.get("date"), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Integer userId = Integer.valueOf(body.get("user_id"));
        Integer centreId = Integer.valueOf(body.get("center_id"));
        String vaxType = body.get("vaccine");

        User vaxUser = userRepository.findById(userId).get();
        VaccineCentre vaxCentre = vaccineCentreRepository.findById(centreId).get();

        Vaccine vax = new Vaccine(userSession.getUser(), vaxDate, vaxCentre, vaxUser, vaxType);
        vaccineRepository.save(vax);

        redirectAttributes.addFlashAttribute("success",
                "The user's new vaccine information was recorded successfully.");
        return "redirect:/profile/" + userId;
    }

    @GetMapping("/stats")
    public String statistics(Model model) {
        model.addAttribute("dosesByNationality", userRepository.countByNationality("Ireland").size());
        model.addAttribute("country", "Irish");
        getStats(model);
        return "stats.html";
    }

    private void getStats(Model model) {
        model.addAttribute("userSession", userSession);
        model.addAttribute("totalDoses", vaccineRepository.count());
        List<User> users = vaccineRepository.findAll().stream().map(Vaccine::getUser).collect(Collectors.toList());
        long male = users.stream().filter(x -> x.getGender().equals("male")).count();
        long female = users.size() - male;
        Map<Integer, Long> ageRanges = new TreeMap<>();

        for (AtomicInteger i = new AtomicInteger(0); i.get() <= 8; i.incrementAndGet()) {
            ageRanges.put(i.get() * 10, users.stream().filter(x -> x.getAge() / 10 == i.get()).count());
        }

        ageRanges.forEach((k, v) -> v = v / userRepository.count());

        model.addAttribute("agerange", ageRanges);
        model.addAttribute("maleDosePercent", male / userRepository.count());
        model.addAttribute("femaleDosePercent", female / userRepository.count());
    }

    @PostMapping("/stats")
    public String statistics(Model model, @RequestParam("nationality") String country) {
        model.addAttribute("dosesByNationality", userRepository.countByNationality(country).size());
        model.addAttribute("country", country);
        getStats(model);
        return "stats.html";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, RedirectAttributes redirectAttributes) {
        if (!userSession.isLoggedIn()) {
            redirectAttributes.addFlashAttribute("error",
                    "The dashboard may only be accessed by logged in admins. If you are an admin, please log in.");
            return "redirect:/login";
        }

        if (!userSession.getUser().isAdmin()) {
            redirectAttributes.addFlashAttribute("error",
                    "The dashboard may only be accessed by logged in admins.");
            return "redirect:/";
        }

        model.addAttribute("userSession", userSession);
        return "dashboard";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("userSession", userSession);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email, @RequestParam("pps") String pps,
            RedirectAttributes redirectAttributes) {
        // make sure the user is found in db by PPS and email
        User user = userRepository.findByCredentials(email, pps);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User does not exist.");
            return "redirect:/login";
        }
        userSession.setUserId(user.getId());
        redirectAttributes.addFlashAttribute("success", "You are now logged in.");
        return "redirect:/";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userSession", userSession);
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String register(User user, RedirectAttributes redirectAttributes) {
        if (userRepository.findByPPS(user.getPPS()) != null) {
            redirectAttributes.addFlashAttribute("error", "User with pps number already exists.");
            return "redirect:/register";
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            redirectAttributes.addFlashAttribute("error", "User with email already exists.");
            return "redirect:/register";
        }
        // Ensure user is 18 or older
        if (isUserUnderage(user.getDateOfBirth())) {
            redirectAttributes.addFlashAttribute("error", "Users under 18 cannot create an account.");
            return "redirect:/register";
        }
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "User was created, you may now login.");
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
        model.addAttribute("userSession", userSession);
        return "forum";
    }

    @GetMapping("/ask-a-question")
    public String askAQuestion(Model model, RedirectAttributes redirectAttributes) {
        // If not logged in or admin, return to forum
        if (!userSession.isLoggedIn() || userSession.getUser().isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Users must be logged in to ask questions.");
            return "redirect:/forum";
        }
        // If user, return ask-a-question page
        model.addAttribute("userSession", userSession);
        return "ask-a-question";
    }

    @PostMapping("/ask-a-question")
    public String askAQuestion(@RequestParam String title, @RequestParam String details, Model model,
            RedirectAttributes redirectAttributes) {
        // If user is not logged in or is admin
        if (!userSession.isLoggedIn() || userSession.getUser().isAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Users must be logged in to ask questions.");
            return "redirect:/forum";
        }

        // Create new question entry in db
        ForumQuestion newQuestion = new ForumQuestion(title, details, getDateSubmitted());
        newQuestion.setUser(userSession.getUser());

        // Add question to database
        forumQuestionRepository.save(newQuestion);

        redirectAttributes.addFlashAttribute("success", "The question was successfully submitted.");

        // Redirect to new question page
        return "redirect:/question?id=" + newQuestion.getId();
    }

    @GetMapping("/question")
    public String getQuestionById(@RequestParam(name = "id") Integer id, Model model,
            RedirectAttributes redirectAttributes) {
        // Retrieve question
        Optional<ForumQuestion> question = forumQuestionRepository.findById(id);
        if (question.isPresent()) {
            // Return question information
            model.addAttribute("question", question.get());
            model.addAttribute("userSession", userSession);
            return "question.html";
        } else {
            redirectAttributes.addFlashAttribute("error", "The question you requested could not be found.");
            // Redirect if question not found
            return "redirect:/forum";
        }
    }

    @PostMapping("/question")
    public String answerQuestion(
            @RequestParam String body, @RequestParam String id, Model model, RedirectAttributes redirectAttributes) {
        // Retrieving question
        try {
            Integer questionId = Integer.parseInt(id);
            Optional<ForumQuestion> question = forumQuestionRepository.findById(questionId);
            if (question.isPresent()) {
                // If user is admin
                if (userSession.isLoggedIn() && userSession.getUser() != null && userSession.getUser().isAdmin()) {
                    // Create new answer entry in db
                    ForumAnswer newAnswer = new ForumAnswer(body, getDateSubmitted());
                    // Save forum question and answer
                    newAnswer.setAdmin(userSession.getUser());
                    newAnswer.setQuestion(question.get());
                    forumAnswerRepository.save(newAnswer);
                    question.get().addAnswer(newAnswer);
                    forumQuestionRepository.save(question.get());

                    redirectAttributes.addFlashAttribute("success", "The answer was successfully submitted.");
                    // Redirect to updated question page
                    return "redirect:/question?id=" + question.get().getId();
                } else {
                    redirectAttributes.addFlashAttribute("error",
                            "Only admins may answer questions. If you are an admin, please log in.");
                    // Redirect to unchanged same question page
                    return "redirect:/question?id=" + question.get().getId();
                }
            }

        } catch (NumberFormatException e) {
            return "redirect:/forum";
        }
        return "redirect:/forum";
    }

    @GetMapping("/profile")
    public String profile(Model model, RedirectAttributes redirectAttributes) {
        if (!userSession.isLoggedIn()) {
            redirectAttributes.addFlashAttribute("error",
                    "You must be logged in to view your profile. If you do not already have an account, please register.");
            return "redirect:/login";
        }

        List<Appointment> apps = appointmentRepository.findByUser(userSession.getUserId());
        Collections.reverse(apps);

        List<Vaccine> vaxes = vaccineRepository.findByUser(userSession.getUserId());
        Collections.reverse(vaxes);

        model.addAttribute("vaccineCenters", vaccineCentreRepository.findAll());
        model.addAttribute("appointments", apps);
        model.addAttribute("vaccines", vaxes);
        model.addAttribute("userSession", userSession);
        model.addAttribute("userProfile", userSession.getUser());
        model.addAttribute("isSelf", true);
        return "profile";
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

            if (userSession.isLoggedIn() && userSession.getUser().isAdmin()) {
                // admins can see everybody's appointments
                List<Appointment> apps = appointmentRepository.findByUser(user.get().getId());
                Collections.reverse(apps);

                List<Vaccine> vaxes = vaccineRepository.findByUser(user.get().getId());
                Collections.reverse(vaxes);

                model.addAttribute("appointments", apps);
                model.addAttribute("vaccines", vaxes);
            }

            model.addAttribute("vaccineCenters", vaccineCentreRepository.findAll());
            model.addAttribute("userSession", userSession);
            model.addAttribute("userProfile", user.get());
            return "profile";
        } catch (NumberFormatException ex) {
            return "404";
        }
    }

    @GetMapping("/cancel-appointment/{stringId}")
    public String cancelAppointment(@PathVariable String stringId, RedirectAttributes redirectAttributes) {
        if (!userSession.isLoggedIn())
            return "redirect:/login";

        Integer id = Integer.valueOf(stringId);
        Appointment app = appointmentRepository.findById(id).get();

        if (!userSession.getUser().isAdmin() && userSession.getUser().getId() != app.getUser().getId()) {
            // Hacker detected! You can't cancel someone else's appointment!
            return "404";
        }

        app.setStatus("cancelled");
        appointmentRepository.save(app);

        AppointmentSlot appSlot = new AppointmentSlot(app.getVaccineCentre(), app.getDate(), app.getTime());
        appointmentSlotRepository.save(appSlot);

        redirectAttributes.addFlashAttribute("success", "The appointment was successfully cancelled.");

        if (app.getUser().getId() != userSession.getUser().getId()) {
            return "redirect:/profile/" + app.getUser().getId();
        }

        return "redirect:/profile";
    }

    @GetMapping("/complete-appointment/{stringId}")
    public String completeAppointment(@PathVariable String stringId, RedirectAttributes redirectAttributes) {
        if (!userSession.isLoggedIn())
            return "redirect:/login";

        Integer id = Integer.valueOf(stringId);
        Appointment app = appointmentRepository.findById(id).get();

        if (!userSession.getUser().isAdmin()) {
            // Hacker detected! You can't modify if you're not an admin!
            return "404";
        }

        app.setStatus("done");
        appointmentRepository.save(app);

        redirectAttributes.addFlashAttribute("success", "The appointment was marked as complete.");

        if (app.getUser().getId() != userSession.getUser().getId()) {
            return "redirect:/profile/" + app.getUser().getId();
        }

        return "redirect:/profile";
    }

    /**
     * /########################
     * <p>
     * Helpers
     * </p>
     * /#######################
     */

    private String getDateSubmitted() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return currentDate.format(formatter);
    }

    private boolean isUserUnderage(String dateOfBirth) {
        LocalDate dob = LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return Period.between(dob, LocalDate.now()).getYears() < 18;
    }
}