
package com.helpdesk.service;

import java.util.List;

import com.helpdesk.model.Ticket;

public interface TicketService {

    boolean raiseComplaint(String title, String description,
            String category, String priority, int employeeId)
            throws Exception;

    List<Ticket> viewMyComplaints(int employeeId) throws Exception;

    List<Ticket> viewAllTickets() throws Exception;

    List<Ticket> viewAssignedTickets(int agentId) throws Exception;

    boolean assignTicket(int ticketId, int agentId, int adminId)
            throws Exception;

    boolean updateTicketStatus(int ticketId, int agentId, String status)
            throws Exception;

    boolean addComment(int ticketId, int userId, String comment)
            throws Exception;

    List<String> viewComments(int ticketId) throws Exception;
}
