package service.vaxapp.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.vaxapp.UserSession;
import service.vaxapp.model.*;
import service.vaxapp.repository.*;
import service.vaxapp.service.EncryptionService;
import service.vaxapp.service.SecurityService;
import service.vaxapp.service.UserService;
import service.vaxapp.validator.UserValidator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Controller
public class AppController {

    private static final Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private ForumAnswerRepository forumAnswerRepository;
    @Autowired
    private ForumQuestionRepository forumQuestionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserValidator userValidator;
    @Autowired
    private UserService userService;
    @Autowired
    private VaccineCentreRepository vaccineCentreRepository;
    @Autowired
    private VaccineRepository vaccineRepository;
    @Autowired
    private AppointmentSlotRepository appointmentSlotRepository;
    @Autowired
    private SecurityService securityService;

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
            logger.info("Guest attempted to make an appointment.");
            redirectAttributes.addFlashAttribute("error", "You must be logged in to make an appointment.");
            return "redirect:/login";
        }

        // A user shouldn't have more than one pending appointment
        if (appointmentRepository.findPending(userSession.getUserId()) != null) {
            logger.info("User (ID " + userSession.getUserId()
                    + ") denied appointment due to existing pending appointment.");
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

        logger.info("User (ID " + userSession.getUserId() + ") successfully booked an appointment at "
                + app.getVaccineCentre().getName() + " on "
                + app.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        redirectAttributes.addFlashAttribute("success",
                "Your appointment has been made! Please see the details of your new appointment.");
        return "redirect:/profile";
    }

    @GetMapping("/stats")
    public String statistics(Model model) {
        getStats(model, "irish");
        return "stats.html";
    }

    @PostMapping("/stats")
    public String statistics(Model model, @RequestParam("nationality") String country) {
        getStats(model, country);
        return "stats.html";
    }

    /**
     * User Area
     */
    @GetMapping("/login")
    public String login(Model model, String error, String logout, RedirectAttributes redirectAttributes) {
        if (logout != null) {
            logger.info("User logged out succesfully.");
            model.addAttribute("logout", "You have been logged out successfully.");
        }

        User currentUser = getCurrentUser();
        if (currentUser != null) {
            if (decryptAndSetSensitiveData(currentUser) == null) {
                model.addAttribute("userSession", userSession);
                return "login";
            }

            userSession.setUserId(currentUser.getId());
            redirectAttributes.addFlashAttribute("success", "Welcome, " +
                    currentUser.getFullName() + "!");
            return "redirect:/";
        }

        model.addAttribute("userSession", userSession);
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        logger.info("Guest navigated to registration page");
        model.addAttribute("userSession", userSession);
        // Adding user attribute for BindingResult
        model.addAttribute("user", new User());
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String register(@ModelAttribute("user") User user, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (user.getDateOfBirth().isEmpty() || user.getEmail().isEmpty() || user.getAddress().isEmpty()
                || user.getFullName().isEmpty() || user.getGender().isEmpty() || user.getNationality().isEmpty()
                || user.getPhoneNumber().isEmpty() || user.getPPS().isEmpty() || user.getPassword().isEmpty()
                || user.getPasswordConfirm().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "All fields are required!");
            logger.info("Guest registration failed due to missing fields.");
            return "redirect:/register";
        }

        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Account could not be created. Invalid email or password.");
            return "redirect:/register";
        }

        userService.save(user);
        securityService.autoLogin(user.getEmail(), user.getPasswordConfirm());
        redirectAttributes.addFlashAttribute("success", "Account created! You can sign in now.");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout() {
        logger.info((userSession.getUser().isAdmin() ? "Admin" : "User") + " (ID " + userSession.getUserId()
                + ") logged out successfully!");
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
            logger.info("Guest attempted to access the ask-a-question page.");
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
            if (userSession.getUser().isAdmin()) {
                logger.info("Admin (ID " + userSession.getUserId() + ") attempted to ask a question on the forum.");
            } else {
                logger.info("Guest attempted to ask question on the forum.");
            }
            redirectAttributes.addFlashAttribute("error", "Users must be logged in to ask questions.");
            return "redirect:/forum";
        }

        // Create new question entry in db
        ForumQuestion newQuestion = new ForumQuestion(title, details, getDateSubmitted(), userSession.getUser());

        // Add question to database
        forumQuestionRepository.save(newQuestion);

        logger.info("User (ID " + userSession.getUserId() + ") asked '" + newQuestion.getTitle() + "' (ID "
                + newQuestion.getId() + ") on the forum.");

        redirectAttributes.addFlashAttribute("success", "The question was successfully submitted.");

        // Redirect to new question page
        return "redirect:/question?id=" + newQuestion.getId();
    }

    @PostMapping("/question")
    public String answerQuestion(@RequestParam String body, @RequestParam String id, Model model,
            RedirectAttributes redirectAttributes) {
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

                    logger.info("Admin (ID " + userSession.getUserId() + ") provided answer (ID " + newAnswer.getId()
                            + ") to question '" + question.get().getTitle() + "' (ID " + questionId + ").");

                    redirectAttributes.addFlashAttribute("success", "The answer was successfully submitted.");
                    // Redirect to updated question page
                    return "redirect:/question?id=" + question.get().getId();
                } else {
                    if (!userSession.isLoggedIn()) {
                        logger.info("Guest attempted to answer question (ID " + questionId + ").");
                    } else {
                        logger.info("User (ID " + userSession.getUserId() + ") attempted to answer question (ID "
                                + questionId + ").");
                    }
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
        if (userSession.getUser() != null) {
            User user = decryptAndSetSensitiveData(userSession.getUser());
            if (user != null) {
                userSession.setUserId(user.getId());
            }
        }
        model.addAttribute("userProfile", userSession.getUser());
        model.addAttribute("isSelf", true);
        model.addAttribute("userDoses", vaxes.size());
        model.addAttribute("userQuestions", forumQuestionRepository.findByUser(userSession.getUserId()).size());
        model.addAttribute("userAppts", appointmentRepository.findByUser(userSession.getUserId()).size());
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

            User decryptedUser = decryptAndSetSensitiveData(user.get());

            if (decryptedUser == null) {
                return "404";
            }

            List<Vaccine> vaxes = vaccineRepository.findByUser(user.get().getId());

            if (userSession.isLoggedIn() && userSession.getUser().isAdmin()) {
                logger.info("Admin (ID " + userSession.getUserId() + ") accessed user profile for User (ID "
                        + user.get().getId() + ").");
                // admins can see everybody's appointments
                List<Appointment> apps = appointmentRepository.findByUser(user.get().getId());
                Collections.reverse(apps);
                Collections.reverse(vaxes);

                model.addAttribute("appointments", apps);
                model.addAttribute("vaccines", vaxes);
            }

            model.addAttribute("vaccineCenters", vaccineCentreRepository.findAll());
            model.addAttribute("userSession", userSession);
            model.addAttribute("userProfile", decryptedUser);


            // model.addAttribute("userQuestions", forumQuestionRepository.findByUser(decryptedUser.getId()).size());
            // model.addAttribute("userDoses", vaxes.size());
            // model.addAttribute("userAppts", appointmentRepository.findByUser(decryptedUser.getId()).size());
            return "profile";
        } catch (NumberFormatException ex) {
            return "404";
        }
    }

    @GetMapping("/cancel-appointment/{stringId}")
    public String cancelAppointment(@PathVariable String stringId, RedirectAttributes redirectAttributes) {
        if (!userSession.isLoggedIn()) {
            logger.info("Guest attempted to cancel appointment (ID " + new String(stringId) + ").");
            return "redirect:/login";
        }

        Integer id = Integer.valueOf(stringId);
        Appointment app = appointmentRepository.findById(id).get();

        if (!userSession.getUser().isAdmin() && userSession.getUser().getId() != app.getUser().getId()) {
            // Hacker detected! You can't cancel someone else's appointment!
            logger.info("Guest attempted to cancel appointment (ID " + new String(stringId) + ").");
            return "404";
        }

        app.setStatus("cancelled");
        appointmentRepository.save(app);

        AppointmentSlot appSlot = new AppointmentSlot(app.getVaccineCentre(), app.getDate(), app.getTime());
        appointmentSlotRepository.save(appSlot);
        if (userSession.getUser().isAdmin()) {
            logger.info("Admin (ID " + userSession.getUserId() + " cancelled appointment (ID " + new String(stringId)
                    + ").");
        } else {
            logger.info("User (ID " + userSession.getUserId() + " cancelled appointment (ID " + new String(stringId)
                    + ").");
        }

        redirectAttributes.addFlashAttribute("success", "The appointment was successfully cancelled.");

        if (app.getUser().getId() != userSession.getUser().getId()) {
            return "redirect:/profile/" + app.getUser().getId();
        }

        return "redirect:/profile";
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

    /**
     * Admin area
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        if (!userSession.isLoggedIn() || !userSession.getUser().isAdmin()) {
            if (userSession.isLoggedIn()) {
                logger.info("User (ID " + userSession.getUserId() + ") attempted to access admin dashboard.");
            } else {
                logger.info("Guest attempted to access admin dashboard.");
            }
            return "redirect:/login";
        }

        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("userSession", userSession);
        return "dashboard";
    }

    @PostMapping(value = "/find-user")
    public String findUser(@RequestParam Map<String, String> body, Model model, RedirectAttributes redirectAttributes) {
        if (!userSession.isLoggedIn() || !userSession.getUser().isAdmin()) {
            if (userSession.isLoggedIn()) {
                logger.info("User (ID " + userSession.getUserId() + ") attempted to lookup other user.");
            } else {
                logger.info("Guest attempted to lookup other user.");
            }
            redirectAttributes.addFlashAttribute("error", "Users cannot look up other users.");
            return "redirect:/login";
        }

        String fullName = body.get("fullName");
        String pps = body.get("pps");
        User user = null;

        if (fullName.isEmpty() && pps.isEmpty()) {
            logger.info("Admin (ID " + userSession.getUserId() + ") performed user lookup without PPS or Full Name.");
            redirectAttributes.addFlashAttribute("error", "One of PPS or Full Name is needed to search.");
        } else if (!fullName.isEmpty()) {
            user = userRepository.findByFullBName(fullName);

        } else if (!pps.isEmpty()) {
            user = userRepository.findUserByPPS(pps);
        }

        if (user == null) {
            logger.info("Admin (ID " + userSession.getUserId() + ") user lookup failed.");
            return "redirect:/dashboard";
        }

        logger.info(
                "Admin (ID " + userSession.getUserId() + ") user lookup succeeded for user (ID " + user.getId() + ").");

        return "redirect:/profile/" + user.getId();
    }

    @PostMapping(value = "/assign-vaccine")
    public String assignVaccine(@RequestParam Map<String, String> body, Model model,
            RedirectAttributes redirectAttributes) {
        if (!userSession.isLoggedIn() || !userSession.getUser().isAdmin()) {
            if (userSession.isLoggedIn()) {
                logger.info("User (ID " + userSession.getUserId() + ") attempted to assign vaccine to user (ID "
                        + new String(body.get("user_id")) + ").");
            } else {
                logger.info("Guest attempted to lookup other user.");
            }
            return "redirect:/login";
        }

        LocalDate vaxDate = LocalDate.parse(body.get("date"), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Integer userId = Integer.valueOf(body.get("user_id"));
        Integer centreId = Integer.valueOf(body.get("center_id"));
        String vaxType = body.get("vaccine");

        User vaxUser = userRepository.findById(userId).get();
        VaccineCentre vaxCentre = vaccineCentreRepository.findById(centreId).get();
        redirectAttributes.addFlashAttribute("success", "The vaccine was recorded.");

        // See how many other doses there are per user
        List<Vaccine> vaccines = vaccineRepository.findByUser(userId);
        if (vaccines == null || vaccines.size() == 0) {
            // Getting date in 3 weeks for second vaccination between 9 and 17
            LocalDate date = vaxDate.plusDays(21);
            LocalTime time = LocalTime.of(9, 00, 00);
            Appointment appointment = appointmentRepository.findByDetails(centreId, date, time);
            while (appointment != null) {
                time = time.plusMinutes(15);
                if (time.getHour() > 17) {
                    if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                        date = date.plusDays(3);
                    } else {
                        date = date.plusDays(1);
                    }
                    time = LocalTime.of(9, 00, 00);
                }
                appointment = appointmentRepository.findByDetails(centreId, date, time);
            }
            User user = userRepository.findById(userId).get();
            // Creating new appointment for the user
            appointment = new Appointment(vaxCentre, date, time, user, "pending");
            appointmentRepository.save(appointment);
            logger.info("Automatic follow-up appointment recorded for user (ID " + vaxUser.getId() + ") at "
                    + vaxCentre.getName() + " on " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".");
            redirectAttributes.addFlashAttribute("success",
                    "The vaccine was recorded and a new appointment at least 3 weeks from now has been made for the user.");
        }
        // Save new vaccine
        Vaccine vax = new Vaccine(userSession.getUser(), vaxDate, vaxCentre, vaxUser, vaxType);
        vaccineRepository.save(vax);

        logger.info("Admin (ID " + userSession.getUserId() + ") recorded " + vax.getType() + "vaccine (ID "
                + vax.getId() + ") for user (ID " + vaxUser.getId() + ").");

        return "redirect:/profile/" + userId;
    }

    @GetMapping("/complete-appointment/{stringId}")
    public String completeAppointment(@PathVariable String stringId, RedirectAttributes redirectAttributes) {
        if (!userSession.isLoggedIn()) {
            logger.info("Guest attempted to complete appointment (ID " + new String(stringId) + ").");
            return "redirect:/login";
        }

        if (!userSession.getUser().isAdmin()) {
            logger.info("User (ID" + userSession.getUserId() + ") attempted to complete appointment (ID "
                    + new String(stringId) + ").");
            // Hacker detected! You can't modify if you're not an admin!
            return "404";
        }

        Integer id = Integer.valueOf(stringId);
        Appointment app = appointmentRepository.findById(id).get();

        app.setStatus("done");
        appointmentRepository.save(app);

        logger.info(
                "Admin (ID" + userSession.getUserId() + ") completed appointment (ID " + new String(stringId) + ").");

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

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String)
            return null;
        User user = userService.findByEmail(((UserDetails) principal).getUsername());
        return user;
    }

    private void getStats(Model model, String country) {
        model.addAttribute("userSession", userSession);
        model.addAttribute("totalDoses", vaccineRepository.count());
        List<User> users = vaccineRepository.findAll().stream().map(Vaccine::getUser).collect(Collectors.toList());

        model.addAttribute("dosesByNationality",
                users.stream().distinct().filter(x -> x.getNationality().equalsIgnoreCase(country)).count());
        model.addAttribute("country", country);

        long total = users.size();
        long male = users.stream().filter(x -> x.getGender().equalsIgnoreCase("male")).count();
        long female = total - male;
        Map<Integer, Double> ageRanges = new TreeMap<>();

        for (AtomicInteger i = new AtomicInteger(1); i.get() <= 8; i.incrementAndGet()) {
            long count = users.stream().filter(x -> {
                try {
                    String decodedDateOfBirthString = EncryptionService.decrypt(x.getDateOfBirth());
                    return x.getAge(decodedDateOfBirthString) / 10 == i.get();
                } catch (Exception e) {
                    logger.error("Error occurred while decoding date of birth. Error: " + e.toString());
                    return false;
                }
            }).count();
            ageRanges.put(i.get() * 10, count == 0 ? 0.0 : count / total * 100);
        }

        model.addAttribute("agerange", ageRanges);
        model.addAttribute("maleDosePercent", male * 100.0 / (double) total);
        model.addAttribute("femaleDosePercent", female * 100.0 / (double) total);
    }

    private User decryptAndSetSensitiveData(User user) {
        try {
            // Decrypt data
            String decodedDateOfBirth = EncryptionService.decrypt(user.getDateOfBirth());
            String decodedPPS = EncryptionService.decrypt(user.getPPS());
            String decodedPhoneNumber = EncryptionService.decrypt(user.getPhoneNumber());
            // Set data in user
            user.setDateOfBirth(decodedDateOfBirth);
            user.setPPS(decodedPPS);
            user.setPhoneNumber(decodedPhoneNumber);
        } catch (Exception e) {
            logger.error("An error occurred while trying to decrypt sensitive data. Error: " + e);
            return null;
        }
        return user;
    }
}