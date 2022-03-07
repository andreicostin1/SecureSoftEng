package service.vaxapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.vaxapp.model.VaccineType;

@Repository
public interface VaccineTypeRepository extends JpaRepository<VaccineType, Integer> {
}
