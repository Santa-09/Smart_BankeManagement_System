package com.smartbank.threads;

import com.smartbank.services.MailService;

public class MailThread extends Thread {
    private String to;
    private String subject;
    private String body;

    public MailThread(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    @Override
    public void run() {
        MailService.sendEmail(to, subject, body);
    }
}