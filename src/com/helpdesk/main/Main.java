
package com.helpdesk.main;

import java.util.List;
import java.util.Scanner;

import com.helpdesk.dao.UserDAO;
import com.helpdesk.dao.UserDAOImpl;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import com.helpdesk.service.TicketService;
import com.helpdesk.service.UserService;
import com.helpdesk.service.impl.TicketServiceImpl;
import com.helpdesk.service.impl.UserServiceImpl;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    private static final UserService userService = new UserServiceImpl();
    private static final TicketService ticketService = new TicketServiceImpl();
    private static final UserDAO userDAO = new UserDAOImpl();

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n========== HELP DESK PRO ==========");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        register();
                        break;

                    case "2":
                        login();
                        break;

                    case "3":
                        System.out.println("Thank you for using Help Desk Pro!");
                        sc.close();
                        return;

                    default:
                        System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void register() throws Exception {
        System.out.println("\n========== REGISTRATION ==========");

        System.out.print("Full name: ");
        String name = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password (minimum 8 characters): ");
        String password = sc.nextLine();

        System.out.println("Roles: EMPLOYEE, AGENT, ADMIN");
        System.out.print("Role: ");
        String role = sc.nextLine();

        if (userService.registerUser(name, email, password, role)) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Registration failed.");
        }
    }

    private static void login() throws Exception {
        System.out.println("\n========== LOGIN ==========");

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        User user = userService.loginUser(email, password);

        if (user == null) {
            System.out.println("Invalid email or password.");
            return;
        }

        System.out.println("\nWelcome, " + user.getFullName() + "!");
        System.out.println("Role: " + user.getRole());

        switch (user.getRole()) {
            case "EMPLOYEE":
                employeeMenu(user);
                break;

            case "AGENT":
                agentMenu(user);
                break;

            case "ADMIN":
                adminMenu(user);
                break;

            default:
                System.out.println("Unknown user role.");
        }
    }

    private static void employeeMenu(User user) {
        while (true) {
            System.out.println("\n========== EMPLOYEE MENU ==========");
            System.out.println("1. Raise Complaint");
            System.out.println("2. View My Complaints");
            System.out.println("3. Add Comment");
            System.out.println("4. View Ticket Comments");
            System.out.println("5. Logout");
            System.out.print("Choose option: ");

            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        System.out.print("Complaint title: ");
                        String title = sc.nextLine();

                        System.out.print("Description: ");
                        String description = sc.nextLine();

                        System.out.print("Category (Hardware/Software/Network): ");
                        String category = sc.nextLine();

                        System.out.print("Priority (LOW/MEDIUM/HIGH): ");
                        String priority = sc.nextLine();

                        boolean created = ticketService.raiseComplaint(
                                title, description, category, priority,
                                user.getUserId());

                        System.out.println(created
                                ? "Complaint raised successfully!"
                                : "Complaint could not be created.");
                        break;

                    case "2":
                        printTickets(ticketService.viewMyComplaints(
                                user.getUserId()));
                        break;

                    case "3":
                        addComment(user);
                        break;

                    case "4":
                        viewComments();
                        break;

                    case "5":
                        System.out.println("Logged out successfully.");
                        return;

                    default:
                        System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    private static void agentMenu(User user) {
        while (true) {
            System.out.println("\n========== AGENT MENU ==========");
            System.out.println("1. View Assigned Tickets");
            System.out.println("2. Update Ticket Status");
            System.out.println("3. Add Comment");
            System.out.println("4. View Ticket Comments");
            System.out.println("5. Logout");
            System.out.print("Choose option: ");

            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        printTickets(ticketService.viewAssignedTickets(
                                user.getUserId()));
                        break;

                    case "2":
                        System.out.print("Ticket ID: ");
                        int ticketId = Integer.parseInt(sc.nextLine().trim());

                        System.out.println(
                                "Statuses: IN_PROGRESS, RESOLVED, CLOSED");
                        System.out.print("New status: ");
                        String status = sc.nextLine();

                        boolean updated = ticketService.updateTicketStatus(
                                ticketId, user.getUserId(), status);

                        System.out.println(updated
                                ? "Ticket status updated successfully!"
                                : "Ticket not found or not assigned to you.");
                        break;

                    case "3":
                        addComment(user);
                        break;

                    case "4":
                        viewComments();
                        break;

                    case "5":
                        System.out.println("Logged out successfully.");
                        return;

                    default:
                        System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    private static void adminMenu(User user) {
        while (true) {
            System.out.println("\n========== ADMIN MENU ==========");
            System.out.println("1. View All Tickets");
            System.out.println("2. Assign Ticket to Agent");
            System.out.println("3. View Agents");
            System.out.println("4. Add Comment");
            System.out.println("5. View Ticket Comments");
            System.out.println("6. Logout");
            System.out.print("Choose option: ");

            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        printTickets(ticketService.viewAllTickets());
                        break;

                    case "2":
                        printTickets(ticketService.viewAllTickets());

                        System.out.print("Ticket ID to assign: ");
                        int ticketId = Integer.parseInt(
                                sc.nextLine().trim());

                        printAgents();

                        System.out.print("Agent user ID: ");
                        int agentId = Integer.parseInt(
                                sc.nextLine().trim());

                        boolean assigned = ticketService.assignTicket(
                                ticketId, agentId, user.getUserId());

                        System.out.println(assigned
                                ? "Ticket assigned successfully!"
                                : "Ticket could not be assigned.");
                        break;

                    case "3":
                        printAgents();
                        break;

                    case "4":
                        addComment(user);
                        break;

                    case "5":
                        viewComments();
                        break;

                    case "6":
                        System.out.println("Logged out successfully.");
                        return;

                    default:
                        System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    private static void printTickets(List<Ticket> tickets) {
        if (tickets.isEmpty()) {
            System.out.println("No tickets found.");
            return;
        }

        System.out.println("\n========== TICKET LIST ==========");

        for (Ticket ticket : tickets) {
            System.out.println("---------------------------------");
            System.out.println("Ticket ID: " + ticket.getTicketId());
            System.out.println("Title: " + ticket.getTitle());
            System.out.println("Description: " + ticket.getDescription());
            System.out.println("Category: " + ticket.getCategory());
            System.out.println("Priority: " + ticket.getPriority());
            System.out.println("Status: " + ticket.getStatus());
            System.out.println("Created By: " + ticket.getCreatedBy());
            System.out.println("Assigned To: "
                    + (ticket.getAssignedTo() == null
                    ? "Not assigned" : ticket.getAssignedTo()));
        }
    }

    private static void printAgents() throws Exception {
        List<User> agents = userDAO.findUsersByRole("AGENT");

        System.out.println("\n========== AVAILABLE AGENTS ==========");

        if (agents.isEmpty()) {
            System.out.println("No agents registered.");
            return;
        }

        for (User agent : agents) {
            System.out.println("ID: " + agent.getUserId()
                    + " | Name: " + agent.getFullName()
                    + " | Email: " + agent.getEmail());
        }
    }

    private static void addComment(User user) throws Exception {
        System.out.print("Ticket ID: ");
        int ticketId = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Comment: ");
        String comment = sc.nextLine();

        boolean added = ticketService.addComment(
                ticketId, user.getUserId(), comment);

        System.out.println(added
                ? "Comment added successfully!"
                : "Comment could not be added.");
    }

    private static void viewComments() throws Exception {
        System.out.print("Ticket ID: ");
        int ticketId = Integer.parseInt(sc.nextLine().trim());

        List<String> comments = ticketService.viewComments(ticketId);

        System.out.println("\n========== TICKET COMMENTS ==========");

        if (comments.isEmpty()) {
            System.out.println("No comments found.");
            return;
        }

        for (String comment : comments) {
            System.out.println(comment);
        }
    }
}
