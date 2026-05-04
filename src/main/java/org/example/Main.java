package org.example;

import jakarta.persistence.*;
import org.example.controller.UserController;
import org.example.dto.UserRequestDTO;
import org.example.dto.UserResponseDTO;
import org.example.exception.UserServiceException;
import org.example.service.UserService;
import org.example.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final UserController userController = new UserController();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        logger.info("Starting User Service Application");

        try {
            showMenu();
        } catch (Exception e) {
            logger.error("Application error: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
            HibernateUtil.shutdown();
            logger.info("Application shutdown");
        }
    }

    private static void showMenu() {
        while (true) {
            System.out.println("\n========== USER SERVICE MENU ==========");
            System.out.println("1. Create User");
            System.out.println("2. Get User by ID");
            System.out.println("3. Get All Users");
            System.out.println("4. Update User");
            System.out.println("5. Delete User");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");

            String choiceStr = scanner.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid option. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    createUser();
                    break;
                case 2:
                    getUserById();
                    break;
                case 3:
                    getAllUsers();
                    break;
                case 4:
                    updateUser();
                    break;
                case 5:
                    deleteUser();
                    break;
                case 6:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void createUser() {
        System.out.println("\n--- CREATE NEW USER ---");

        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter age: ");
        String ageStr = scanner.nextLine();
        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid age. Please enter a number.");
            return;
        }

        try {
            UserRequestDTO request = new UserRequestDTO(name, email, age);
            UserResponseDTO response = userController.createUser(request);
            System.out.println("✓ User created successfully!");
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private static void getUserById() {
        System.out.println("\n--- GET USER BY ID ---");
        System.out.print("Enter user ID: ");
        String idStr = scanner.nextLine();
        Long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid ID. Please enter a number.");
            return;
        }

        try {
            UserResponseDTO user = userController.getUser(id);
            System.out.println("User found:");
            System.out.println(user);
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private static void getAllUsers() {
        System.out.println("\n--- ALL USERS ---");
        try {
            List<UserResponseDTO> users = userController.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("No users found.");
            } else {
                System.out.println("Found " + users.size() + " user(s):");
                for (UserResponseDTO user : users) {
                    System.out.println(user);
                }
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private static void updateUser() {
        System.out.println("\n--- UPDATE USER ---");
        System.out.print("Enter user ID to update: ");
        String idStr = scanner.nextLine();
        Long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid ID. Please enter a number.");
            return;
        }

        try {
            // First check if user exists
            UserResponseDTO existing = userController.getUser(id);
            System.out.println("Current user info:");
            System.out.println(existing);

            System.out.println("\nEnter new values (press Enter to keep current):");
            System.out.print("New name [" + existing.getName() + "]: ");
            String name = scanner.nextLine();
            if (name.trim().isEmpty()) {
                name = existing.getName();
            }

            System.out.print("New email [" + existing.getEmail() + "]: ");
            String email = scanner.nextLine();
            if (email.trim().isEmpty()) {
                email = existing.getEmail();
            }

            System.out.print("New age [" + existing.getAge() + "]: ");
            String ageStr = scanner.nextLine();
            Integer age = existing.getAge();
            if (!ageStr.trim().isEmpty()) {
                try {
                    age = Integer.parseInt(ageStr);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid age format, keeping current age: " + existing.getAge());
                }
            }

            UserRequestDTO request = new UserRequestDTO(name, email, age);
            UserResponseDTO updated = userController.updateUser(id, request);
            System.out.println("✓ User updated successfully!");
            System.out.println(updated);
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        System.out.println("\n--- DELETE USER ---");
        System.out.print("Enter user ID to delete: ");
        String idStr = scanner.nextLine();
        Long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid ID. Please enter a number.");
            return;
        }

        System.out.print("Are you sure? (y/n): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("y")) {
            try {
                boolean deleted = userController.deleteUser(id);
                if (deleted) {
                    System.out.println("✓ User deleted successfully!");
                } else {
                    System.out.println("✗ User not found.");
                }
            } catch (Exception e) {
                System.out.println("✗ Error: " + e.getMessage());
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
}

package org.example.controller;

import org.example.dto.UserRequestDTO;
import org.example.dto.UserResponseDTO;
import org.example.exception.UserServiceException;
import org.example.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UserController {
    private static final Logger logger = LogManager.getLogger(org.example.controller.UserController.class);
    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        try {
            logger.info("Controller: Processing create user request for email: {}", requestDTO.getEmail());
            return userService.createUser(requestDTO);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (UserServiceException e) {
            logger.error("Controller: {}", e.getMessage());
            throw e;
        }
    }

    public UserResponseDTO getUser(Long id) {
        try {
            logger.info("Controller: Processing get user request for id: {}", id);
            return userService.getUserById(id);
        } catch (Exception e) {
            logger.error("Controller: Error getting user: {}", e.getMessage());
            throw e;
        }
    }

    public List<UserResponseDTO> getAllUsers() {
        try {
            logger.info("Controller: Processing get all users request");
            return userService.getAllUsers();
        } catch (Exception e) {
            logger.error("Controller: Error getting all users: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO) {
        try {
            logger.info("Controller: Processing update user request for id: {}", id);
            return userService.updateUser(id, requestDTO);
        } catch (Exception e) {
            logger.error("Controller: Error updating user: {}", e.getMessage());
            throw e;
        }
    }

    public boolean deleteUser(Long id) {
        try {
            logger.info("Controller: Processing delete user request for id: {}", id);
            return userService.deleteUser(id);
        } catch (Exception e) {
            logger.error("Controller: Error deleting user: {}", e.getMessage());
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}

package org.example.controller;

import org.example.dto.UserRequestDTO;
import org.example.dto.UserResponseDTO;
import org.example.exception.UserServiceException;
import org.example.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UserController {
    private static final Logger logger = LogManager.getLogger(org.example.controller.UserController.class);
    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        try {
            logger.info("Controller: Processing create user request for email: {}", requestDTO.getEmail());
            return userService.createUser(requestDTO);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (UserServiceException e) {
            logger.error("Controller: {}", e.getMessage());
            throw e;
        }
    }

    public UserResponseDTO getUser(Long id) {
        try {
            logger.info("Controller: Processing get user request for id: {}", id);
            return userService.getUserById(id);
        } catch (Exception e) {
            logger.error("Controller: Error getting user: {}", e.getMessage());
            throw e;
        }
    }

    public List<UserResponseDTO> getAllUsers() {
        try {
            logger.info("Controller: Processing get all users request");
            return userService.getAllUsers();
        } catch (Exception e) {
            logger.error("Controller: Error getting all users: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO) {
        try {
            logger.info("Controller: Processing update user request for id: {}", id);
            return userService.updateUser(id, requestDTO);
        } catch (Exception e) {
            logger.error("Controller: Error updating user: {}", e.getMessage());
            throw e;
        }
    }

    public boolean deleteUser(Long id) {
        try {
            logger.info("Controller: Processing delete user request for id: {}", id);
            return userService.deleteUser(id);
        } catch (Exception e) {
            logger.error("Controller: Error deleting user: {}", e.getMessage());
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}
package org.example.dto;

import java.time.LocalDateTime;

public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private LocalDateTime createdAt;

    public UserResponseDTO(Long id, String name, String email, Integer age, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Name: %s | Email: %s | Age: %d | Created: %s",
                id, name, email, age, createdAt);
    }
}
package org.example.entity;

import jakarta.persistence.*;
        import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private Integer age;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public User() {}

    public User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', email='%s', age=%d, createdAt=%s}",
                id, name, email, age, createdAt);
    }
}
package org.example.exception;

public class UserServiceException extends RuntimeException {

    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
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
    private static final Logger logger = LogManager.getLogger(org.example.repository.UserRepository.class);

    public org.example.entity.User save(org.example.entity.User user) {
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
            throw new org.example.exception.UserServiceException("User with email " + user.getEmail() + " already exists");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error saving user", e);
            throw new org.example.exception.UserServiceException("Database error while saving user", e);
        }
    }

    public Optional<org.example.entity.User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.example.entity.User user = session.get(org.example.entity.User.class, id);
            logger.info("User findById: {}", id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error finding user by id {}", id, e);
            throw new org.example.exception.UserServiceException("Database error while finding user", e);
        }
    }

    public List<org.example.entity.User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<org.example.entity.User> users = session.createQuery("FROM User", org.example.entity.User.class).list();
            logger.info("Found {} users", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error finding all users", e);
            throw new org.example.exception.UserServiceException("Database error while fetching users", e);
        }
    }

    public org.example.entity.User update(org.example.entity.User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            org.example.entity.User merged = session.merge(user);
            transaction.commit();
            logger.info("User updated: {}", user.getId());
            return merged;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error updating user {}", user.getId(), e);
            throw new org.example.exception.UserServiceException("Database error while updating user", e);
        }
    }

    public boolean deleteById(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            org.example.entity.User user = session.get(org.example.entity.User.class, id);

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
            throw new org.example.exception.UserServiceException("Database error while deleting user", e);
        }
    }

    public Optional<org.example.entity.User> findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.example.entity.User user = session.createQuery("FROM User WHERE email = :email", org.example.entity.User.class)
                    .setParameter("email", email)
                    .uniqueResult();
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error finding user by email", e);
            throw new org.example.exception.UserServiceException("Database error while finding user by email", e);
        }
    }
}
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
    private static final Logger logger = LogManager.getLogger(org.example.repository.UserRepository.class);

    public org.example.entity.User save(org.example.entity.User user) {
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
            throw new org.example.exception.UserServiceException("User with email " + user.getEmail() + " already exists");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error saving user", e);
            throw new org.example.exception.UserServiceException("Database error while saving user", e);
        }
    }

    public Optional<org.example.entity.User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.example.entity.User user = session.get(org.example.entity.User.class, id);
            logger.info("User findById: {}", id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error finding user by id {}", id, e);
            throw new org.example.exception.UserServiceException("Database error while finding user", e);
        }
    }

    public List<org.example.entity.User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<org.example.entity.User> users = session.createQuery("FROM User", org.example.entity.User.class).list();
            logger.info("Found {} users", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error finding all users", e);
            throw new org.example.exception.UserServiceException("Database error while fetching users", e);
        }
    }

    public org.example.entity.User update(org.example.entity.User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            org.example.entity.User merged = session.merge(user);
            transaction.commit();
            logger.info("User updated: {}", user.getId());
            return merged;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error updating user {}", user.getId(), e);
            throw new org.example.exception.UserServiceException("Database error while updating user", e);
        }
    }

    public boolean deleteById(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            org.example.entity.User user = session.get(org.example.entity.User.class, id);

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
            throw new org.example.exception.UserServiceException("Database error while deleting user", e);
        }
    }

    public Optional<org.example.entity.User> findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.example.entity.User user = session.createQuery("FROM User WHERE email = :email", org.example.entity.User.class)
                    .setParameter("email", email)
                    .uniqueResult();
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error finding user by email", e);
            throw new org.example.exception.UserServiceException("Database error while finding user by email", e);
        }
    }
}

package org.example.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HibernateUtil {
    private static final Logger logger = LogManager.getLogger(org.example.util.HibernateUtil.class);
    private static SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            logger.error("Initial SessionFactory creation failed: {}", ex.getMessage());
            logger.error("SessionFactory creation failed", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void setSessionFactoryForTesting(SessionFactory testFactory) {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
        sessionFactory = testFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            logger.info("SessionFactory closed");
        }
    }
}

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
                "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">org.h2.Driver</property>
        <property name="hibernate.connection.url">jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL</property>
        <property name="hibernate.connection.username">sa</property>
        <property name="hibernate.connection.password"></property>

        <property name="hibernate.dialect">org.hibernate.dialect.H2Dialect</property>
        <property name="hibernate.hbm2ddl.auto">create-drop</property>
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">true</property>

        <mapping class="org.example.entity.User"/>
    </session-factory>
</hibernate-configuration>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
                "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">org.h2.Driver</property>
        <property name="hibernate.connection.url">jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL</property>
        <property name="hibernate.connection.username">sa</property>
        <property name="hibernate.connection.password"></property>

        <property name="hibernate.dialect">org.hibernate.dialect.H2Dialect</property>
        <property name="hibernate.hbm2ddl.auto">create-drop</property>
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">true</property>

        <mapping class="org.example.entity.User"/>
    </session-factory>
</hibernate-configuration>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
                "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">org.h2.Driver</property>
        <property name="hibernate.connection.url">jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL</property>
        <property name="hibernate.connection.username">sa</property>
        <property name="hibernate.connection.password"></property>

        <property name="hibernate.dialect">org.hibernate.dialect.H2Dialect</property>
        <property name="hibernate.hbm2ddl.auto">create-drop</property>
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">true</property>

        <mapping class="org.example.entity.User"/>
    </session-factory>
</hibernate-configuration>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
                "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">org.h2.Driver</property>
        <property name="hibernate.connection.url">jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL</property>
        <property name="hibernate.connection.username">sa</property>
        <property name="hibernate.connection.password"></property>

        <property name="hibernate.dialect">org.hibernate.dialect.H2Dialect</property>
        <property name="hibernate.hbm2ddl.auto">create-drop</property>
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">true</property>

        <mapping class="org.example.entity.User"/>
    </session-factory>
</hibernate-configuration>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
                "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">org.h2.Driver</property>
        <property name="hibernate.connection.url">jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL</property>
        <property name="hibernate.connection.username">sa</property>
        <property name="hibernate.connection.password"></property>

        <property name="hibernate.dialect">org.hibernate.dialect.H2Dialect</property>
        <property name="hibernate.hbm2ddl.auto">create-drop</property>
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">true</property>

        <mapping class="org.example.entity.User"/>
    </session-factory>
</hibernate-configuration>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
                "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">org.h2.Driver</property>
        <property name="hibernate.connection.url">jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL</property>
        <property name="hibernate.connection.username">sa</property>
        <property name="hibernate.connection.password"></property>

        <property name="hibernate.dialect">org.hibernate.dialect.H2Dialect</property>
        <property name="hibernate.hbm2ddl.auto">create-drop</property>
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">true</property>

        <mapping class="org.example.entity.User"/>
    </session-factory>
</hibernate-configuration>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
                "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">org.h2.Driver</property>
        <property name="hibernate.connection.url">jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL</property>
        <property name="hibernate.connection.username">sa</property>
        <property name="hibernate.connection.password"></property>

        <property name="hibernate.dialect">org.hibernate.dialect.H2Dialect</property>
        <property name="hibernate.hbm2ddl.auto">create-drop</property>
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">true</property>

        <mapping class="org.example.entity.User"/>
    </session-factory>
</hibernate-configuration>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
                "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">org.h2.Driver</property>
        <property name="hibernate.connection.url">jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL</property>
        <property name="hibernate.connection.username">sa</property>
        <property name="hibernate.connection.password"></property>

        <property name="hibernate.dialect">org.hibernate.dialect.H2Dialect</property>
        <property name="hibernate.hbm2ddl.auto">create-drop</property>
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">true</property>

        <mapping class="org.example.entity.User"/>
    </session-factory>
</hibernate-configuration>

version: '3.8'

services:
postgres:
image: postgres:15
container_name: module_2-hibernate-postgres
environment:
POSTGRES_DB: userdb
POSTGRES_USER: postgres
POSTGRES_PASSWORD: root
ports:
        - "5432:5432"
volumes:
        - postgres_data:/var/lib/postgresql/data

volumes:
postgres_data:

version: '3.8'

services:
postgres:
image: postgres:15
container_name: module_2-hibernate-postgres
environment:
POSTGRES_DB: userdb
POSTGRES_USER: postgres
POSTGRES_PASSWORD: root
ports:
        - "5432:5432"
volumes:
        - postgres_data:/var/lib/postgresql/data

volumes:
postgres_data:
