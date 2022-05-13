package service.vaxapp.service;

import service.vaxapp.model.User;

public interface UserService {
    void save(User user);

    User findByEmail(String email);

    User findByPPS(String PPS);

    boolean isUserUnderage(String dateOfBirth);
}
