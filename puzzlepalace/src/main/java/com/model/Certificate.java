package com.model;

public class Certificate {
    private String certificateId;
    private String playerName;
    private int completionDate;
    private String scoreSummary;

    public Certificate() {

    }

    public Certificate(String certificateId, String playerName, int completionDate, String scoreSummary) {
        this.certificateId = certificateId;
        this.playerName = playerName;
        this.completionDate = completionDate;
        this.scoreSummary = scoreSummary;
    }

    public void generatePDF() {
        System.out.println("Generating PDF for certificate: " + certificateId);
    }

    public void emailCertificate(String email) {
        System.out.println("emailCertificate() called with email: " + email);
    }

    public void displayCertificate() {
        System.out.println("displayCertificate() called (stub)");
    }
    
    
}
