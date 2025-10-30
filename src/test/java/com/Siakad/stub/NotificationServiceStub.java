package com.Siakad.stub;

import com.Siakad.service.NotificationService; // Pastikan import ini benar

public class NotificationServiceStub implements NotificationService {

    // Variabel state
    public String lastEmailSentTo = null;
    public String lastSubject = null;
    public String lastSmsSentTo = null; // Dari penambahan sebelumnya

    @Override
    public void sendEmail(String to, String subject, String body) {
        // Baris ini HARUSNYA mengubah null menjadi alamat email
        this.lastEmailSentTo = to;
        this.lastSubject = subject;
        System.out.println("STUB: NotificationService sendEmail() dipanggil. Tujuan: " + to + ", Subjek: " + subject);
    }

    @Override
    public void sendSMS(String phoneNumber, String message) {
        this.lastSmsSentTo = phoneNumber;
        System.out.println("STUB: sendSMS dipanggil ke nomor: " + phoneNumber + " dengan pesan: " + message);
    }
}