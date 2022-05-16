package service.vaxapp.service;

import service.vaxapp.model.User;
// import service.vaxapp.repository.RoleRepository;
import service.vaxapp.repository.UserRepository;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {
    private static final long LOCK_TIME_DURATION = 30000; // 30 seconds

    @Autowired
    private UserRepository userRepository;
    // @Autowired
    // private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        // user.setRoles(new HashSet<>(roleRepository.findAll()));
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findByPPS(String PPS) {
        return userRepository.findByPPS(PPS);
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
        // userRepository.updateFailedAttempts(newFailAttempts, user.getEmail());
    }
     
    public void resetFailedAttempts(User user) {
        user.setFailedAttempt(0);
        userRepository.save(user);
    }
     
    public void lock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
         
        userRepository.save(user);
    }
     
    public boolean unlockWhenTimeExpired(User user) {
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();
         
        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);
             
            userRepository.save(user);
             
            return true;
        }
         
        return false;
    }
}
