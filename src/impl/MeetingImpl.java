package impl;

import interfaces.Contact;
import interfaces.FutureMeeting;
import interfaces.Meeting;
import interfaces.PastMeeting;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class MeetingImpl extends DomainObject implements Meeting, PastMeeting, FutureMeeting, Serializable {

  private static final long serialVersionUID = 3L;
  private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
  private final Calendar date;
  private final Set<Contact> contacts;
  private String notes = "";

  public MeetingImpl() {
    date = null;
    contacts = null;
  }
  
  public MeetingImpl(Calendar date, Set<Contact> contacts) {
    this.date = date;
    this.contacts = contacts;
  }
  
  @Override
  public Calendar getDate() {
    return (Calendar) date.clone();
  }
  
  @Override
  public Set<Contact> getContacts() {
    return new HashSet<Contact>(contacts);
  }
  
  @Override
  public String getNotes() {
    return notes;
  }
  
  /***
   * @see "Adds a semicolon to separate new note from existing notes."
   */
  @Override
  public void addNotes(String note) {
    if (!notes.equals(""))
      notes += "; ";
    notes += note;
  }
  
  /***
   * @see "Two Meetings are equal if they have the same id."
   */
  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof MeetingImpl)) {
      return false;
    }
    MeetingImpl contact = (MeetingImpl) obj;
    return this.getId() == contact.getId();
  }
  
  /***
   * @see "Date is formated in dd-MM-yyyy."
   */
  @Override
  public String toString() {
    String meetingNotesToStr = "";
    if(!this.notes.equals("")) {
      meetingNotesToStr = ", Notes: " + this.getNotes();
    }
    return "[Meeting-> Id: " + getId() + ", Date: " + sdf.format(getDate().getTime()) + ", Contacts: " + getContacts() + meetingNotesToStr + "]";
  }
}