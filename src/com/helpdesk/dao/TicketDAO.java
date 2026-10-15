
package com.helpdesk.dao;

import java.sql.SQLException;
import java.util.List;

import com.helpdesk.model.Ticket;

public interface TicketDAO {

    boolean createTicket(Ticket ticket) throws SQLException;

    List<Ticket> findTicketsByEmployee(int employeeId)
            throws SQLException;

    List<Ticket> findAllTickets() throws SQLException;

    List<Ticket> findAssignedTickets(int agentId) throws SQLException;

    boolean assignTicket(int ticketId, int agentId, int adminId)
            throws SQLException;

    boolean updateTicketStatus(
            int ticketId, int agentId, String newStatus)
            throws SQLException;

    boolean addComment(int ticketId, int userId, String comment)
            throws SQLException;

    List<String> findComments(int ticketId) throws SQLException;
}
