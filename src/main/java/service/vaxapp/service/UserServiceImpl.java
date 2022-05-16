package service.vaxapp.service;

import service.vaxapp.model.User;
import service.vaxapp.repository.UserRepository;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {
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
            // TODO: add logging
            System.out.println("Error occurred while encrypting sensitive data. Error: " + e.toString());
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
}
