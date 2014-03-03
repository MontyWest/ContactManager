package test;

import static org.junit.Assert.*;

import impl.MeetingImpl;
import interfaces.Contact;
import interfaces.PastMeeting;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class PastMeetingTest {
  
  Calendar pastDate;
  Set<Contact> contacts;

  @Before
  public void setUp() {
    pastDate = Calendar.getInstance();
    pastDate.add(Calendar.YEAR, -1);
    contacts = new HashSet<Contact>();
    contacts.add(new MockContactImpl());
    contacts.add(new MockContactImpl());
  }
  
  @Test
  public void testGetNotesNone() {
    PastMeeting pastMeeting = new MeetingImpl(pastDate, contacts);
    
    assertEquals("", pastMeeting.getNotes());
  }
  
  @Test
  public void testAddNotesSingle() {
    PastMeeting pastMeeting = new MeetingImpl(pastDate, contacts);
    pastMeeting.addNotes("Notes 1");
    
    assertTrue(pastMeeting.getNotes().contains("Notes 1"));
  }
  
  @Test
  public void testAddNotesMultiple() {
    PastMeeting pastMeeting = new MeetingImpl(pastDate, contacts);
    pastMeeting.addNotes("Notes 1");
    pastMeeting.addNotes("Notes 2");
    
    assertTrue(pastMeeting.getNotes().contains("Notes 1"));
    assertTrue(pastMeeting.getNotes().contains("Notes 2"));
  }
}