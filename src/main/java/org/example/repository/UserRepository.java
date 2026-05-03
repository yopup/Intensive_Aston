package org.example.repository;

import org.example.entity.User;
import org.example.exception.UserServiceException;
import org.example.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;
import java.util.Optional;

public class UserRepository {
    private static final Logger logger = LogManager.getLogger(UserRepository.class);

    public User save(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("User saved: {}", user.getEmail());
            return user;
        } catch (ConstraintViolationException e) {
            if (transaction != null) transaction.rollback();
            logger.error("Email already exists: {}", user.getEmail());
            throw new UserServiceException("User with email " + user.getEmail() + " already exists");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error saving user", e);
            throw new UserServiceException("Database error while saving user", e);
        }
    }

    public Optional<User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            logger.info("User findById: {}", id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error finding user by id {}", id, e);
            throw new UserServiceException("Database error while finding user", e);
        }
    }

    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> users = session.createQuery("FROM User", User.class).list();
            logger.info("Found {} users", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error finding all users", e);
            throw new UserServiceException("Database error while fetching users", e);
        }
    }

    public User update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User merged = session.merge(user);
            transaction.commit();
            logger.info("User updated: {}", user.getId());
            return merged;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error updating user {}", user.getId(), e);
            throw new UserServiceException("Database error while updating user", e);
        }
    }

    public boolean deleteById(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);

            if (user != null) {
                session.remove(user);
                transaction.commit();
                logger.info("User deleted: {}", id);
                return true;
            }

            transaction.commit();
            logger.warn("User not found for deletion: {}", id);
            return false;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error deleting user {}", id, e);
            throw new UserServiceException("Database error while deleting user", e);
        }
    }

    public Optional<User> findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error finding user by email", e);
            throw new UserServiceException("Database error while finding user by email", e);
        }
    }
}
