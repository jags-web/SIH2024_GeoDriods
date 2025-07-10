package com.example.sih;

public class LeaveRequest {
    private String fullName;
    private String email;
    private String department;
    private String managerName;
    private String leaveReason;
    private String firstDay;
    private String lastDay;
    private String notes;
    private String status; // Pending, Completed, Rejected

    public LeaveRequest(String fullName, String email, String department, String managerName,
                        String leaveReason, String firstDay, String lastDay, String notes) {
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.managerName = managerName;
        this.leaveReason = leaveReason;
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        this.notes = notes;
        this.status = "Pending"; // Default status
    }

    // Getters for the properties
    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }

    public String getManagerName() {
        return managerName;
    }

    public String getLeaveReason() {
        return leaveReason;
    }

    public String getFirstDay() {
        return firstDay;
    }

    public String getLastDay() {
        return lastDay;
    }

    public String getNotes() {
        return notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Leave Request Submitted:\n" +
                "Full Name: " + fullName + "\n" +
                "Email: " + email + "\n" +
                "Department: " + department + "\n" +
                "Manager/Supervisor: " + managerName + "\n" +
                "Reason for Leave: " + leaveReason + "\n" +
                "From: " + firstDay + "\n" +
                "To: " + lastDay + "\n" +
                "Notes: " + notes + "\n" +
                "Status: " + status + "\n\n";
    }
}
