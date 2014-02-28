package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import impl.ContactImpl;
import impl.ContactManagerImpl;
import interfaces.Contact;
import interfaces.ContactManager;
import interfaces.Meeting;
import interfaces.PastMeeting;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/***
 * Order of tests is to ensure we never use a method's functionality
 * that hasn't been previously tested.
 * 
 * @author montywest
 *
 */
public class ContactManagerTest {
  
  ContactManager contactManager;
  Calendar nowDate;
  Calendar pastDate;
  Calendar futureDate;

  
  @Before
  public void setUp() {
    contactManager = new ContactManagerImpl();
    nowDate = Calendar.getInstance();
    pastDate = Calendar.getInstance();
    pastDate.add(Calendar.YEAR, -1);
    futureDate = Calendar.getInstance();
    futureDate.add(Calendar.YEAR, 1);
  }
  
  @Test
  public void testAddNewContactNullNameThrowsException() {
    try {
      contactManager.addNewContact(null, "notes");
      fail();
    } catch(NullPointerException e) {
      
    }
  }
  
  @Test
  public void testAddNewContactNullNotesThrowsException() {
    try {
      contactManager.addNewContact("name", null);
      fail();
    } catch(NullPointerException e) {
      
    }
  }
  
  @Test
  public void testGetContactsFromName() {
    contactManager.addNewContact("mike", "notes");
    contactManager.addNewContact("kevin", "notes");
    
    Set<Contact> expectMike = contactManager.getContacts("mike");
    Set<Contact> expectBoth = contactManager.getContacts("ke");
    Set<Contact> expectNone = contactManager.getContacts("sue");
    
    assertEquals(1, expectMike.size());
    assertEquals("mike", ((Contact) expectMike.toArray()[0]).getName());

    assertEquals(2, expectBoth.size());
    assertEquals(0, expectNone.size());
  }
  
  @Test
  public void testGetContactsFromNameThrowsException() {
    try {
      contactManager.getContacts((String) null);
      fail();
    } catch(NullPointerException e) {
      
    }
  }
  
  @Test
  public void testGetContactsFromIds() {
    contactManager.addNewContact("mollie", "notes");
    contactManager.addNewContact("mike", "notes");
    
    Set<Contact> expectedContacts = contactManager.getContacts("m");
    
    Iterator<Contact> it = expectedContacts.iterator();
    int firstId = it.next().getId();
    int secondId = it.next().getId();
    
    Set<Contact> returnedContacts = contactManager.getContacts(firstId, secondId);
    
    assertEquals(expectedContacts, returnedContacts);
  }
  
  @Test
  public void testGetContactsFromIdsNotFoundThrowsException() {
    contactManager.addNewContact("mike", "notes");
    try {
      contactManager.getContacts(3, 8);
      fail();
    } catch (IllegalArgumentException e) {
      
    }
  }
  
//#### Future Meetings ####

  @Test
  public void testAddFutureMeetingContactNotFoundThrowsException() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> outsideContactSet = new HashSet<Contact>();
    outsideContactSet.add(new ContactImpl("sue", "notes"));
    
    try {
      contactManager.addFutureMeeting(outsideContactSet, futureDate);
      fail();
    } catch (IllegalArgumentException e) {
      
    }
  }
  
  @Test
  public void testAddFutureMeetingPastDateThrowsException() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mike = contactManager.getContacts("mike");
    try {
      contactManager.addFutureMeeting(mike, pastDate);
      fail();
    } catch (IllegalArgumentException e) {
      
    }
  }
  
  @Test
  public void testGetFutureMeetingListContactNotFoundThrowsException() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    contactManager.addFutureMeeting(mikeSet, futureDate);
    
    Contact sue = new ContactImpl("sue", "notes");
    try {
      contactManager.getFutureMeetingList(sue);
      fail();
    } catch (IllegalArgumentException e) {
      
    }
  }
  
  @Test
  public void testGetFutureMeetingListReturnsMeeting() {
    contactManager.addNewContact("mike", "notes");
    contactManager.addNewContact("sue", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    Set<Contact> sueSet = contactManager.getContacts("sue");
    Set<Contact> bothSet = contactManager.getContacts("");
    Iterator<Contact> it = mikeSet.iterator();
    Contact mike = it.next();
    
    contactManager.addFutureMeeting(sueSet, futureDate);
    contactManager.addFutureMeeting(bothSet, futureDate);
    List<Meeting> returnedMeetings = contactManager.getFutureMeetingList(mike);
    
    assertEquals(1, returnedMeetings.size());
    assertTrue(returnedMeetings.get(0).getContacts().contains(mike));
  }
  
  @Test
  public void testGetFutureMeetingListSorted() {
    Calendar oneYearDate = Calendar.getInstance();
    oneYearDate.add(Calendar.YEAR, 1);
    Calendar twoYearDate = Calendar.getInstance();
    twoYearDate.add(Calendar.YEAR, 2);
    Calendar threeYearDate = Calendar.getInstance();
    threeYearDate.add(Calendar.YEAR, 3);
    
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    Iterator<Contact> it = mikeSet.iterator();
    Contact mike = it.next();
    
    contactManager.addFutureMeeting(mikeSet, threeYearDate);
    contactManager.addFutureMeeting(mikeSet, oneYearDate);
    contactManager.addFutureMeeting(mikeSet, twoYearDate);
    List<Meeting> returnedMeetings = contactManager.getFutureMeetingList(mike);
    
    assertEquals(oneYearDate, returnedMeetings.get(0).getDate());
    assertEquals(twoYearDate, returnedMeetings.get(1).getDate());
    assertEquals(threeYearDate, returnedMeetings.get(2).getDate());    
  }
  
  @Test
  public void testAddFutureDateReturnsId() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    Iterator<Contact> it = mikeSet.iterator();
    Contact mike = it.next();
    
    int mikeMeetingId = contactManager.addFutureMeeting(mikeSet, futureDate);
    List<Meeting> returnedMeetings = contactManager.getFutureMeetingList(mike);
    
    assertEquals(mikeMeetingId, returnedMeetings.get(0).getId());
  }

  // #### Past Meetings ####
  
  @Test
  public void testAddNewPastMeetingContactNotFoundThrowsException() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> outsideContactSet = new HashSet<Contact>();
    outsideContactSet.add(new ContactImpl("sue", "notes"));
    
    try {
      contactManager.addNewPastMeeting(outsideContactSet, pastDate, "text");
      fail();
    } catch (IllegalArgumentException e) {
      
    }
  }
  
  @Test
  public void testAddNewPastMeetingContactNullContactThrowsException() {
    try {
      contactManager.addNewPastMeeting(null, pastDate, "text");
      fail();
    } catch (NullPointerException e) {
      
    }
  }
  
  @Test
  public void testAddNewPastMeetingContactNullDateThrowsException() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mike = contactManager.getContacts("mike");
    try {
      contactManager.addNewPastMeeting(mike, null, "text");
      fail();
    } catch (NullPointerException e) {
      
    }
  }
  
  @Test
  public void testAddNewPastMeetingContactNullTextThrowsException() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mike = contactManager.getContacts("mike");
    try {
      contactManager.addNewPastMeeting(mike, pastDate, null);
      fail();
    } catch (NullPointerException e) {
      
    }
  }
  
  //@Test
  //public void testAddNewPastMeetingFutureDateThrowsException() {
  //  contactManager.addNewContact("mike", "notes");
  //  Set<Contact> mike = contactManager.getContacts("mike");
  //  try {
  //    contactManager.addFutureMeeting(mike, futureDate, "text");
  //    fail();
  //  } catch (IllegalArgumentException e) {
  //    
  //  }
  //}
  
  @Test
  public void testGetPastMeetingListContactNotFoundThrowsException() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    contactManager.addNewPastMeeting(mikeSet, pastDate, "text");
    
    Contact sue = new ContactImpl("sue", "notes");
    try {
      contactManager.getPastMeetingList(sue);
      fail();
    } catch (IllegalArgumentException e) {
      
    }
  }
  
  @Test
  public void testGetPastMeetingListReturnsMeeting() {
    contactManager.addNewContact("mike", "notes");
    contactManager.addNewContact("sue", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    Set<Contact> sueSet = contactManager.getContacts("sue");
    Set<Contact> bothSet = contactManager.getContacts("");
    Iterator<Contact> it = mikeSet.iterator();
    Contact mike = it.next();
    
    contactManager.addNewPastMeeting(sueSet, pastDate, "text");
    contactManager.addNewPastMeeting(bothSet, pastDate, "text");
    List<PastMeeting> returnedMeetings = contactManager.getPastMeetingList(mike);
    
    assertEquals(1, returnedMeetings.size());
    assertTrue(returnedMeetings.get(0).getContacts().contains(mike));
  }
  
  @Test
  public void testGetPastMeetingListSorted() {
    Calendar oneYearAgoDate = Calendar.getInstance();
    oneYearAgoDate.add(Calendar.YEAR, -1);
    Calendar twoYearAgoDate = Calendar.getInstance();
    twoYearAgoDate.add(Calendar.YEAR, -2);
    Calendar threeYearAgoDate = Calendar.getInstance();
    threeYearAgoDate.add(Calendar.YEAR, -3);
    
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    Iterator<Contact> it = mikeSet.iterator();
    Contact mike = it.next();
    
    contactManager.addNewPastMeeting(mikeSet, threeYearAgoDate, "text");
    contactManager.addNewPastMeeting(mikeSet, oneYearAgoDate,"text");
    contactManager.addNewPastMeeting(mikeSet, twoYearAgoDate,"text");
    List<PastMeeting> returnedMeetings = contactManager.getPastMeetingList(mike);
    
    assertEquals(oneYearAgoDate, returnedMeetings.get(0).getDate());
    assertEquals(twoYearAgoDate, returnedMeetings.get(1).getDate());
    assertEquals(threeYearAgoDate, returnedMeetings.get(2).getDate());    
  }
}