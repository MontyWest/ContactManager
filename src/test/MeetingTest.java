package test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import impl.MeetingImpl;
import interfaces.Contact;
import interfaces.Meeting;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class MeetingTest {

  Calendar nowDate;
  Set<Contact> contacts;
  
  @Before
  public void setUp() {
    nowDate = Calendar.getInstance();
    contacts = new HashSet<Contact>();
    contacts.add(new MockContactImpl());
    contacts.add(new MockContactImpl());
  }
  
  @Test
  public void testConstructor() {
    try {
      new MeetingImpl(nowDate, contacts);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testConstructorSets() {
    Meeting meeting = new MeetingImpl(nowDate, contacts);

    assertThat(meeting.getId(), is(notNullValue()));
    assertEquals(meeting.getDate(), nowDate);
    assertEquals(meeting.getContacts(), contacts);
  }
  
  @Test
  public void testuniqueIds() {
    Meeting meetingOne = new MeetingImpl(nowDate, contacts);
    Meeting meetingTwo = new MeetingImpl(nowDate, contacts);
    
    assertThat(meetingOne.getId(), is(not(equalTo(meetingTwo.getId()))));
  }
}