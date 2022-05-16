package service.vaxapp.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import service.vaxapp.model.User;
import service.vaxapp.repository.UserRepositoryCustom;

@Repository
@Transactional(readOnly = true)
public class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public User findUserByPPS(String pps) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cQuery = builder.createQuery(User.class);
        Root<User> root = cQuery.from(User.class);

        // Get all users
        cQuery.select(root);
        List<User> users = entityManager.createQuery(cQuery).getResultList();

        for (User user : users) {
            try {
                String decryptedPPS = EncryptionService.decrypt(user.getPPS());
                if (pps.equals(decryptedPPS)) {
                    return user;
                }
            } catch (Exception e) {
                // TODO: add logging
                System.out.println("An error occurred while decrypting pps for user in custom method. Error: "
                        + e.toString());
            }
        }
        return null;
    }
}
