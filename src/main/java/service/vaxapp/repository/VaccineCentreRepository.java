package service.vaxapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import antlr.collections.List;
import service.vaxapp.model.VaccineCentre;

@Repository
public interface VaccineCentreRepository extends JpaRepository<VaccineCentre, Integer> {
    
}
