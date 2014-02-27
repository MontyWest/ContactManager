package test;

import interfaces.Contact;
import interfaces.FutureMeeting;
import interfaces.Meeting;
import interfaces.PastMeeting;

import java.util.Calendar;
import java.util.Set;

public class MockMeetingImpl implements Meeting, PastMeeting, FutureMeeting {
  
  public int getId() {
    return 0;
  }
  
  public Calendar getDate() {
    return null;
  }
  
  public Set<Contact> getContacts() {
    return null;
  }
  
  public String getNotes() {
    return null;
  }
  
  public void addNotes(String note) {
  }
}