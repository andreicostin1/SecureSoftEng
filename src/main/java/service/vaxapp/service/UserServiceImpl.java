package service.vaxapp.service;

import service.vaxapp.model.User;
import service.vaxapp.repository.UserRepository;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final long LOCK_TIME_DURATION = 30000; // 30 seconds

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(User user) {
        // Encrypt password
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        try {
            // Encrypt sensitive information (PPS, phone number, date of birth)
            user.setPPS(EncryptionService.encrypt(user.getPPS()));
            user.setPhoneNumber(EncryptionService.encrypt(user.getPhoneNumber()));
            user.setDateOfBirth(EncryptionService.encrypt(user.getDateOfBirth()));
        } catch (Exception e) {
            logger.error("Error occurred while encrypting sensitive data. Error: " + e.toString());
        }
        // user.setRoles(new HashSet<>(roleRepository.findAll()));
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findByPPS(String PPS) {
        return userRepository.findUserByPPS(PPS);
    }

    @Override
    public boolean isUserUnderage(String dateOfBirth) {
        LocalDate dob = LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return Period.between(dob, LocalDate.now()).getYears() < 18;
    }

    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        user.setFailedAttempt(newFailAttempts);
        userRepository.save(user);
        logger.info("Account for user (ID " + user.getId() + ") failed authentication attempt.");
        // userRepository.updateFailedAttempts(newFailAttempts, user.getEmail());
    }

    public void resetFailedAttempts(User user) {
        logger.info("Failed authentication attempts reset to 0 for user (ID " + user.getId() + ").");
        user.setFailedAttempt(0);
        userRepository.save(user);
    }

    public void lock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());

        logger.info("Account locked for user (ID " + user.getId() + ") after 3 consecutive failed attempts.");

        userRepository.save(user);
    }

    public boolean unlockWhenTimeExpired(User user) {
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();

        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);

            logger.info("Account unlocked for user (ID " + user.getId() + ") after lock time expired.");

            userRepository.save(user);

            return true;
        }

        return false;
    }
}
