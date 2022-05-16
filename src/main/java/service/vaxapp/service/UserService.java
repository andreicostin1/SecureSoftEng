package service.vaxapp.service;

import service.vaxapp.model.User;

public interface UserService {
    public static final int MAX_FAILED_ATTEMPTS = 3;

    void save(User user);

    User findByEmail(String email);

    User findByPPS(String PPS);

    boolean isUserUnderage(String dateOfBirth);

    void increaseFailedAttempts(User user);
    void resetFailedAttempts(User user);
     
    void lock(User user);
     
    boolean unlockWhenTimeExpired(User user);
}
