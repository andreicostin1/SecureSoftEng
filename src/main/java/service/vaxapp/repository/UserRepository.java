package service.vaxapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.vaxapp.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "SELECT * FROM users WHERE email=:email AND user_pps=:pps", nativeQuery = true)
    User findByCredentials(String email, String pps);

    @Query(value = "SELECT * FROM users WHERE user_pps=:pps", nativeQuery = true)
    User findByPPS(String pps);

    @Query(value = "SELECT * FROM users WHERE email=:email", nativeQuery = true)
    User findByEmail(String email);

    @Query(value = "SELECT COUNT(id) WHERE nationality=:nationality", nativeQuery = true)
    int countByNationality(String nationality);
}
