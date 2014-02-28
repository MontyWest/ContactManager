package impl;

import interfaces.Contact;
import interfaces.FutureMeeting;
import interfaces.Meeting;
import interfaces.PastMeeting;

import java.util.Calendar;
import java.util.Set;

public class MeetingImpl extends DomainObject implements Meeting, PastMeeting, FutureMeeting {

  private static final long serialVersionUID = 3L;
  private final Calendar date;
  private final Set<Contact> contacts;
  private String notes = "";

  public MeetingImpl(Calendar date, Set<Contact> contacts) {
    this.date = date;
    this.contacts = contacts;
  }
  
  @Override
  public Calendar getDate() {
    return date;
  }
  
  @Override
  public Set<Contact> getContacts() {
    return contacts;
  }
  
  @Override
  public String getNotes() {
    return notes;
  }
  
  public void addNotes(String note) {
    if (!notes.equals(""))
      notes += "; ";
    notes += note;
  }
}