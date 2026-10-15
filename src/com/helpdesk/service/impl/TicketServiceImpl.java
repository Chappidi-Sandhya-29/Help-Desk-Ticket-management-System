
package com.helpdesk.service.impl;

import java.util.List;

import com.helpdesk.dao.TicketDAO;
import com.helpdesk.dao.TicketDAOImpl;
import com.helpdesk.dao.UserDAO;
import com.helpdesk.dao.UserDAOImpl;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import com.helpdesk.service.TicketService;

public class TicketServiceImpl implements TicketService {

    private final TicketDAO ticketDAO = new TicketDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    public boolean raiseComplaint(String title, String description,
            String category, String priority, int employeeId)
            throws Exception {

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required.");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required.");
        }

        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category is required.");
        }

        if (priority == null) {
            throw new IllegalArgumentException("Priority is required.");
        }

        priority = priority.trim().toUpperCase();

        if (!priority.equals("LOW") && !priority.equals("MEDIUM")
                && !priority.equals("HIGH")) {
            throw new IllegalArgumentException(
                    "Priority must be LOW, MEDIUM, or HIGH.");
        }

        if (employeeId <= 0) {
            throw new IllegalArgumentException("Invalid employee ID.");
        }

        Ticket ticket = new Ticket(
                title.trim(), description.trim(), category.trim(),
                priority, employeeId);

        return ticketDAO.createTicket(ticket);
    }

    @Override
    public List<Ticket> viewMyComplaints(int employeeId) throws Exception {
        return ticketDAO.findTicketsByEmployee(employeeId);
    }

    @Override
    public List<Ticket> viewAllTickets() throws Exception {
        return ticketDAO.findAllTickets();
    }

    @Override
    public List<Ticket> viewAssignedTickets(int agentId) throws Exception {
        return ticketDAO.findAssignedTickets(agentId);
    }

    @Override
    public boolean assignTicket(int ticketId, int agentId, int adminId)
            throws Exception {

        if (ticketId <= 0 || agentId <= 0 || adminId <= 0) {
            throw new IllegalArgumentException("Invalid ID supplied.");
        }

        User agent = null;

        for (User user : userDAO.findUsersByRole("AGENT")) {
            if (user.getUserId() == agentId) {
                agent = user;
                break;
            }
        }

        if (agent == null) {
            throw new IllegalArgumentException(
                    "The supplied user ID does not belong to an agent.");
        }

        return ticketDAO.assignTicket(ticketId, agentId, adminId);
    }

    @Override
    public boolean updateTicketStatus(
            int ticketId, int agentId, String status) throws Exception {

        if (ticketId <= 0) {
            throw new IllegalArgumentException("Invalid ticket ID.");
        }

        if (status == null) {
            throw new IllegalArgumentException("Status is required.");
        }

        status = status.trim().toUpperCase();

        if (!status.equals("IN_PROGRESS")
                && !status.equals("RESOLVED")
                && !status.equals("CLOSED")) {
            throw new IllegalArgumentException(
                    "Choose IN_PROGRESS, RESOLVED, or CLOSED.");
        }

        return ticketDAO.updateTicketStatus(ticketId, agentId, status);
    }

    @Override
    public boolean addComment(
            int ticketId, int userId, String comment) throws Exception {

        if (ticketId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Invalid ID supplied.");
        }

        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty.");
        }

        return ticketDAO.addComment(ticketId, userId, comment.trim());
    }

    @Override
    public List<String> viewComments(int ticketId) throws Exception {
        if (ticketId <= 0) {
            throw new IllegalArgumentException("Invalid ticket ID.");
        }

        return ticketDAO.findComments(ticketId);
    }
}
