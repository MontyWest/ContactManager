package test;

import interfaces.Contact;
import interfaces.Meeting;

import java.util.Calendar;
import java.util.Set;

public class MockMeetingImpl implements Meeting {
  
  public int getId() {
    return 0;
  }
  
  public Calendar getDate() {
    return null;
  }
  
  public Set<Contact> getContacts() {
    return null;
  }
}