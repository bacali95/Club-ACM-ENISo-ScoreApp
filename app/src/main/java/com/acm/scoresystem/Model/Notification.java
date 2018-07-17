package com.acm.scoresystem.Model;

public class Notification {
    private String body;
    private String sentTime;

    public Notification() {
    }

    public Notification(String body, String sentTime) {
        this.body = body;
        this.sentTime = sentTime;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "body='" + body + '\'' +
                ", sentTime=" + sentTime +
                '}';
    }
}
