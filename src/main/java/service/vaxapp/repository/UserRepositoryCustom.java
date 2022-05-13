package service.vaxapp.repository;

import service.vaxapp.model.User;

public interface UserRepositoryCustom {
    User findUserByPPS(String pps);
}
