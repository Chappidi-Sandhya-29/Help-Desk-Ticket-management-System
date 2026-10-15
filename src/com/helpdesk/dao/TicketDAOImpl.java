
package com.helpdesk.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.helpdesk.model.Ticket;
import com.helpdesk.util.DBConnection;

public class TicketDAOImpl implements TicketDAO {

    private Ticket readTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket(
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("category"),
                rs.getString("priority"),
                rs.getInt("created_by"));

        ticket.setTicketId(rs.getInt("ticket_id"));
        ticket.setStatus(rs.getString("status"));

        int assigned = rs.getInt("assigned_to");
        ticket.setAssignedTo(rs.wasNull() ? null : assigned);

        return ticket;
    }

    private List<Ticket> executeTicketQuery(
            String sql, Integer id) throws SQLException {

        List<Ticket> tickets = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (id != null) {
                ps.setInt(1, id);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(readTicket(rs));
                }
            }
        }

        return tickets;
    }

    @Override
    public boolean createTicket(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets "
                + "(title, description, category, priority, status, created_by) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ticket.getTitle());
            ps.setString(2, ticket.getDescription());
            ps.setString(3, ticket.getCategory());
            ps.setString(4, ticket.getPriority());
            ps.setString(5, ticket.getStatus());
            ps.setInt(6, ticket.getCreatedBy());

            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public List<Ticket> findTicketsByEmployee(int employeeId)
            throws SQLException {

        return executeTicketQuery(
                "SELECT * FROM tickets WHERE created_by = ? "
                + "ORDER BY ticket_id DESC", employeeId);
    }

    @Override
    public List<Ticket> findAllTickets() throws SQLException {
        return executeTicketQuery(
                "SELECT * FROM tickets ORDER BY ticket_id DESC", null);
    }

    @Override
    public List<Ticket> findAssignedTickets(int agentId)
            throws SQLException {

        return executeTicketQuery(
                "SELECT * FROM tickets WHERE assigned_to = ? "
                + "ORDER BY ticket_id DESC", agentId);
    }

    @Override
    public boolean assignTicket(int ticketId, int agentId, int adminId)
            throws SQLException {

        String selectSql =
                "SELECT status FROM tickets WHERE ticket_id = ?";
        String updateSql =
                "UPDATE tickets SET assigned_to = ?, status = 'ASSIGNED', "
                + "updated_at = CURRENT_TIMESTAMP WHERE ticket_id = ?";
        String historySql =
                "INSERT INTO ticket_history "
                + "(ticket_id, changed_by, old_status, new_status, note) "
                + "VALUES (?, ?, ?, 'ASSIGNED', ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                String oldStatus;

                try (PreparedStatement ps = con.prepareStatement(selectSql)) {
                    ps.setInt(1, ticketId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }
                        oldStatus = rs.getString("status");
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                    ps.setInt(1, agentId);
                    ps.setInt(2, ticketId);

                    if (ps.executeUpdate() != 1) {
                        con.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(historySql)) {
                    ps.setInt(1, ticketId);
                    ps.setInt(2, adminId);
                    ps.setString(3, oldStatus);
                    ps.setString(4, "Ticket assigned to agent ID " + agentId);
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    @Override
    public boolean updateTicketStatus(
            int ticketId, int agentId, String newStatus)
            throws SQLException {

        String selectSql = "SELECT status FROM tickets "
                + "WHERE ticket_id = ? AND assigned_to = ?";

        String updateSql = "UPDATE tickets SET status = ?, "
                + "updated_at = CURRENT_TIMESTAMP WHERE ticket_id = ? "
                + "AND assigned_to = ?";

        String historySql = "INSERT INTO ticket_history "
                + "(ticket_id, changed_by, old_status, new_status, note) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                String oldStatus;

                try (PreparedStatement ps = con.prepareStatement(selectSql)) {
                    ps.setInt(1, ticketId);
                    ps.setInt(2, agentId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }
                        oldStatus = rs.getString("status");
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                    ps.setString(1, newStatus);
                    ps.setInt(2, ticketId);
                    ps.setInt(3, agentId);

                    if (ps.executeUpdate() != 1) {
                        con.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(historySql)) {
                    ps.setInt(1, ticketId);
                    ps.setInt(2, agentId);
                    ps.setString(3, oldStatus);
                    ps.setString(4, newStatus);
                    ps.setString(5, "Status updated by assigned agent");
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    @Override
    public boolean addComment(
            int ticketId, int userId, String comment) throws SQLException {

        String sql = "INSERT INTO ticket_comments "
                + "(ticket_id, user_id, comment_text) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ticketId);
            ps.setInt(2, userId);
            ps.setString(3, comment);

            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public List<String> findComments(int ticketId) throws SQLException {
        List<String> comments = new ArrayList<>();

        String sql = "SELECT c.comment_text, c.created_at, u.full_name "
                + "FROM ticket_comments c "
                + "JOIN users u ON c.user_id = u.user_id "
                + "WHERE c.ticket_id = ? ORDER BY c.comment_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ticketId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comments.add(rs.getString("full_name") + " ("
                            + rs.getString("created_at") + "): "
                            + rs.getString("comment_text"));
                }
            }
        }

        return comments;
    }
}
