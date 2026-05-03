package org.example;

import org.example.controller.UserController;
import org.example.dto.UserRequestDTO;
import org.example.dto.UserResponseDTO;
import org.example.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
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
