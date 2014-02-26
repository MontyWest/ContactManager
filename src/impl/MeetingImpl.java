package impl;

import interfaces.Contact;
import interfaces.Meeting;

import java.util.Calendar;
import java.util.Set;

public class MeetingImpl extends DomainObject implements Meeting {

  private static final long serialVersionUID = 3L;
  private final Calendar date;
  private final Set<Contact> contacts;

  public MeetingImpl(Calendar date, Set<Contact> contacts) {
    this.date = date;
    this.contacts = contacts;
  }
  
  public Calendar getDate() {
    return date;
  }
  
  public Set<Contact> getContacts() {
    return contacts;
  }
}