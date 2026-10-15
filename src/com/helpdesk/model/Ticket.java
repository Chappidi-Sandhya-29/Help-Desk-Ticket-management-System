
package com.helpdesk.model;

public class Ticket {

    private int ticketId;
    private String title;
    private String description;
    private String category;
    private String priority;
    private String status;
    private int createdBy;
    private Integer assignedTo;

    public Ticket(String title, String description,
                  String category, String priority, int createdBy) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.createdBy = createdBy;
        this.status = "OPEN";
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public Integer getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Integer assignedTo) {
        this.assignedTo = assignedTo;
    }
}
