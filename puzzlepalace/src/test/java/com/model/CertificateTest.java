package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

import org.junit.Test;

public class CertificateTest {

    @Test
    public void parameterisedConstructorPopulatesFields() throws Exception {
        Certificate certificate = new Certificate("cert-1", "Dana", 20240101, "Score 9001");

        Field idField = Certificate.class.getDeclaredField("certificateId");
        idField.setAccessible(true);
        Field nameField = Certificate.class.getDeclaredField("playerName");
        nameField.setAccessible(true);
        Field dateField = Certificate.class.getDeclaredField("completionDate");
        dateField.setAccessible(true);
        Field summaryField = Certificate.class.getDeclaredField("scoreSummary");
        summaryField.setAccessible(true);

        assertEquals("cert-1", idField.get(certificate));
        assertEquals("Dana", nameField.get(certificate));
        assertEquals(20240101, dateField.get(certificate));
        assertEquals("Score 9001", summaryField.get(certificate));
    }

    @Test
    public void defaultConstructorLeavesFieldsUnset() throws Exception {
        Certificate certificate = new Certificate();

        Field idField = Certificate.class.getDeclaredField("certificateId");
        idField.setAccessible(true);
        Field nameField = Certificate.class.getDeclaredField("playerName");
        nameField.setAccessible(true);
        Field summaryField = Certificate.class.getDeclaredField("scoreSummary");
        summaryField.setAccessible(true);

        assertNull(idField.get(certificate));
        assertNull(nameField.get(certificate));
        assertNull(summaryField.get(certificate));
    }

    @Test
    public void generatePdfAndEmailCertificateProduceReadableOutput() {
        Certificate certificate = new Certificate("cert-77", "Eve", 20240522, "Top performer");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(buffer));
        try {
            certificate.generatePDF();
            certificate.emailCertificate("eve@example.com");
            certificate.displayCertificate();
        } finally {
            System.setOut(original);
        }

        String output = buffer.toString();
        org.junit.Assert.assertTrue(output.contains("Generating PDF for certificate: cert-77"));
        org.junit.Assert.assertTrue(output.contains("emailCertificate() called with email: eve@example.com"));
        org.junit.Assert.assertTrue(output.contains("displayCertificate() called"));
    }
}