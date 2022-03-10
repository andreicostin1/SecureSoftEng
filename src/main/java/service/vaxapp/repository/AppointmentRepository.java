package service.vaxapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.vaxapp.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    @Query(value = "SELECT * FROM appointment WHERE user_id=:userId AND status='pending'", nativeQuery = true)
    Appointment findPending(Integer userId);

    @Query(value = "SELECT * FROM appointment WHERE user_id=:userId", nativeQuery = true)
    List<Appointment> findByUser(Integer userId);
}
