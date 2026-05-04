package org.example.repository;

import org.example.entity.User;
import org.example.util.HibernateUtil;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTestcontainersTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private UserRepository userRepository;
    private SessionFactory sessionFactory;

    @BeforeAll
    void setUp() {
        Properties props = new Properties();
        props.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        props.setProperty("hibernate.connection.username", postgres.getUsername());
        props.setProperty("hibernate.connection.password", postgres.getPassword());
        props.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        props.setProperty("hibernate.show_sql", "true");

        sessionFactory = new Configuration()
                .addProperties(props)
                .addAnnotatedClass(User.class)
                .buildSessionFactory();

        HibernateUtil.setSessionFactoryForTesting(sessionFactory);
        userRepository = new UserRepository();
    }

    @AfterAll
    void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    @DisplayName("Save and find user in real PostgreSQL")
    void saveAndFindUser_shouldWorkWithRealPostgres() {
        User user = new User("Test", "testcontainers@example.com", 30);
        User saved = userRepository.save(user);

        assertNotNull(saved.getId());

        var found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("testcontainers@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Duplicate email should fail in real PostgreSQL")
    void duplicateEmail_shouldFail() {
        User user1 = new User("First", "dup@test.com", 20);
        User user2 = new User("Second", "dup@test.com", 25);

        userRepository.save(user1);

        assertThrows(RuntimeException.class, () -> userRepository.save(user2));
    }
}

