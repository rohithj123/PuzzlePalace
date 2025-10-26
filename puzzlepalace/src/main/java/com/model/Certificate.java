package com.model;
/**
 * This will show a player's achievement certificate in the game.
 * This will store information about the certificate, player details,
 * completion date, and score summary.
 */

public class Certificate {
    private String certificateId;
    private String playerName;
    private int completionDate;
    private String scoreSummary;

    /**
     * Default constructor.
     */

    public Certificate() {
    /**
     * Constructor with parameters.
     *
     * @param certificateId is the ID of the certificate
     * @param playerName is the player's name
     * @param completionDate is the date the player completed the puzzle
     * @param scoreSummary is the score or summary of performance
     */

    }

    public Certificate(String certificateId, String playerName, int completionDate, String scoreSummary) {
        this.certificateId = certificateId;
        this.playerName = playerName;
        this.completionDate = completionDate;
        this.scoreSummary = scoreSummary;
    }
    /**
     * Generates a PDF for the certificate.
     */
    public void generatePDF() {
        System.out.println("Generating PDF for certificate: " + certificateId);
    }

    public void emailCertificate(String email) {
        System.out.println("emailCertificate() called with email: " + email);
    }

    /**
     * Displays the certificate details.
     */
    public void displayCertificate() {
        System.out.println("displayCertificate() called (stub)");
    }
    
    
}
