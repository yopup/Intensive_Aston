package org.example.repository;

import org.example.entity.User;
import org.example.util.HibernateUtil;
import org.example.util.HibernateUtilTest;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

    private UserRepository userRepository;
    private SessionFactory sessionFactory;

    @BeforeAll
    void setUp() {
        // Берём тестовую SessionFactory из HibernateUtilTest
        sessionFactory = HibernateUtilTest.getTestSessionFactory();
        // Подменяем в HibernateUtil на тестовую
        HibernateUtil.setSessionFactoryForTesting(sessionFactory);
        userRepository = new UserRepository();
    }

    @AfterAll
    void tearDown() {
        HibernateUtilTest.shutdown();
        HibernateUtil.shutdown();
    }

    @BeforeEach
    void cleanDatabase() {
        try (var session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            tx.commit();
        }
    }

    @Test
    @DisplayName("Save user — should generate id and persist")
    void save_shouldGenerateIdAndPersist() {
        User user = new User("John", "john@test.com", 25);
        User saved = userRepository.save(user);
        assertNotNull(saved.getId());
        assertEquals("john@test.com", saved.getEmail());
    }

    @Test
    @DisplayName("Save user with duplicate email — should throw exception")
    void save_duplicateEmail_shouldThrow() {
        User user1 = new User("John", "dup@test.com", 25);
        User user2 = new User("Jane", "dup@test.com", 30);
        userRepository.save(user1);
        assertThrows(RuntimeException.class, () -> userRepository.save(user2));
    }

    @Test
    @DisplayName("Find by id — should return user when exists")
    void findById_whenExists_shouldReturnUser() {
        User saved = userRepository.save(new User("John", "john@find.com", 25));
        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("john@find.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Find by id — should return empty when not exists")
    void findById_whenNotExists_shouldReturnEmpty() {
        Optional<User> found = userRepository.findById(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Find all — should return all users")
    void findAll_shouldReturnAllUsers() {
        userRepository.save(new User("A", "a@test.com", 20));
        userRepository.save(new User("B", "b@test.com", 25));
        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("Update — should change fields")
    void update_shouldChangeFields() {
        User saved = userRepository.save(new User("Old", "old@test.com", 20));
        saved.setName("New");
        saved.setAge(30);
        User updated = userRepository.update(saved);
        assertEquals("New", updated.getName());
        assertEquals(30, updated.getAge());
    }

    @Test
    @DisplayName("Delete by id — should remove user")
    void deleteById_shouldRemoveUser() {
        User saved = userRepository.save(new User("John", "delete@test.com", 25));
        boolean deleted = userRepository.deleteById(saved.getId());
        assertTrue(deleted);
        assertTrue(userRepository.findById(saved.getId()).isEmpty());
    }

    @Test
    @DisplayName("Delete by id — should return false if not exists")
    void deleteById_notExists_shouldReturnFalse() {
        boolean deleted = userRepository.deleteById(999L);
        assertFalse(deleted);
    }

    @Test
    @DisplayName("Find by email — should return user when exists")
    void findByEmail_whenExists_shouldReturnUser() {
        userRepository.save(new User("John", "unique@test.com", 25));
        Optional<User> found = userRepository.findByEmail("unique@test.com");
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getName());
    }

    @Test
    @DisplayName("Find by email — should return empty when not exists")
    void findByEmail_whenNotExists_shouldReturnEmpty() {
        Optional<User> found = userRepository.findByEmail("missing@test.com");
        assertTrue(found.isEmpty());
    }
}

