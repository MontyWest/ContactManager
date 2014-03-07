package test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import impl.ContactImpl;
import impl.ContactManagerImpl;
import interfaces.Contact;
import interfaces.ContactManager;
import interfaces.FutureMeeting;
import interfaces.Meeting;
import interfaces.PastMeeting;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/***
 * Order of tests is to ensure we never use a method's functionality
 * that hasn't been previously tested. This is purely for readability
 * as tests as carried out in a 'random' order.
 * 
 * @author montywest
 *
 */
public class ContactManagerTest {
  
  private static final String FILENAME = "test.txt";
  
  ContactManager contactManager;
  Calendar nowDate;
  Calendar pastDate;
  Calendar futureDate;

  
  @Before
  public void setUp() {
    contactManager = new ContactManagerImpl(FILENAME);
    nowDate = Calendar.getInstance();
    pastDate = Calendar.getInstance();
    pastDate.add(Calendar.YEAR, -1);
    futureDate = Calendar.getInstance();
    futureDate.add(Calendar.YEAR, 1);
  }
  
  @After
  public void cleanUp() {
    System.out.println(contactManager);
    File file = new File(FILENAME);
    file.delete();
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
    
    assertEquals(threeYearAgoDate, returnedMeetings.get(0).getDate());
    assertEquals(twoYearAgoDate, returnedMeetings.get(1).getDate());
    assertEquals(oneYearAgoDate, returnedMeetings.get(2).getDate());    
  }
  
  // ### General Meeting Return
  
  @Test
  public void testGetFutureMeetingListByDateReturnsFutureMeetings() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    Iterator<Contact> it = mikeSet.iterator();
    Contact mike = it.next();
    
    contactManager.addNewPastMeeting(mikeSet, pastDate, "past1");
    contactManager.addNewPastMeeting(mikeSet, pastDate, "past2");
    contactManager.addNewPastMeeting(mikeSet, pastDate, "past3");
    contactManager.addFutureMeeting(mikeSet, futureDate);
    contactManager.addFutureMeeting(mikeSet, futureDate);
    contactManager.addFutureMeeting(mikeSet, futureDate);
    
    List<Meeting> futureMeetings = contactManager.getFutureMeetingList(mike);
    List<Meeting> returnedFutureMeetings = contactManager.getFutureMeetingList(futureDate);
    
    assertEquals(3, returnedFutureMeetings.size());
    assertTrue(returnedFutureMeetings.contains(futureMeetings.get(0)));
    assertTrue(returnedFutureMeetings.contains(futureMeetings.get(1)));
    assertTrue(returnedFutureMeetings.contains(futureMeetings.get(2)));
  }
  
  @Test
  public void testGetFutureMeetingListByDateReturnsPastMeetings() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    Iterator<Contact> it = mikeSet.iterator();
    Contact mike = it.next();
    
    contactManager.addNewPastMeeting(mikeSet, pastDate, "past1");
    contactManager.addNewPastMeeting(mikeSet, pastDate, "past2");
    contactManager.addNewPastMeeting(mikeSet, pastDate, "past3");
    contactManager.addFutureMeeting(mikeSet, futureDate);
    contactManager.addFutureMeeting(mikeSet, futureDate);
    contactManager.addFutureMeeting(mikeSet, futureDate);
    
    List<PastMeeting> pastMeetings = contactManager.getPastMeetingList(mike);
    List<Meeting> returnedPastMeetings = contactManager.getFutureMeetingList(pastDate);
    
    assertEquals(3, returnedPastMeetings.size());
    assertTrue(returnedPastMeetings.contains((Meeting)pastMeetings.get(0)));
    assertTrue(returnedPastMeetings.contains((Meeting)pastMeetings.get(1)));
    assertTrue(returnedPastMeetings.contains((Meeting)pastMeetings.get(2)));
  }
  
  // ### Get Meeting by Id ###
  
  @Test
  public void testGetPastMeetingByIdReturnsNullIfNone() {
    PastMeeting returnedMeeting = contactManager.getPastMeeting(100);
    
    assertThat(returnedMeeting, is(nullValue()));
  }
  
  @Test
  public void testGetPastMeetingByIdThrowsException() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    int futureMeetingId = contactManager.addFutureMeeting(mikeSet, futureDate);
    
    try {
      contactManager.getPastMeeting(futureMeetingId);
      fail();
    } catch (IllegalArgumentException e) {
      
    }
  }
  
  @Test
  public void testGetPastMeetingByIdReturn() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    Iterator<Contact> it = mikeSet.iterator();
    Contact mike = it.next();
    
    contactManager.addNewPastMeeting(mikeSet, pastDate, "notes");
    
    List<PastMeeting> pastMeetingList = contactManager.getPastMeetingList(mike);
    PastMeeting pastMeeting = pastMeetingList.get(0);
    int pastMeetingId = pastMeeting.getId();
    
    PastMeeting returnedMeeting = contactManager.getPastMeeting(pastMeetingId);
    
    assertEquals(returnedMeeting, pastMeeting);
  }
  
  @Test
  public void testGetFutureMeetingByIdReturnsNullIfNone() {
    FutureMeeting returnedMeeting = contactManager.getFutureMeeting(100);
    
    assertThat(returnedMeeting, is(nullValue()));
  }
  
  @Test
  public void testGetFutureMeetingByIdThrowsException() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    Iterator<Contact> it = mikeSet.iterator();
    Contact mike = it.next();
    
    contactManager.addNewPastMeeting(mikeSet, pastDate, "notes");
    
    List<PastMeeting> pastMeetingList = contactManager.getPastMeetingList(mike);
    int pastMeetingId = pastMeetingList.get(0).getId();
    
    try {
      contactManager.getFutureMeeting(pastMeetingId);
      fail();
    } catch (IllegalArgumentException e) {
      
    }
  }
  
  @Test
  public void testGetFutureMeetingByIdReturn() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    Iterator<Contact> it = mikeSet.iterator();
    Contact mike = it.next();
    
    int futureMeetingId = contactManager.addFutureMeeting(mikeSet, futureDate);
    
    List<Meeting> futureMeetingList = contactManager.getFutureMeetingList(mike);
    FutureMeeting futureMeeting = (FutureMeeting) futureMeetingList.get(0);
    
    FutureMeeting returnedMeeting = contactManager.getFutureMeeting(futureMeetingId);
    
    assertEquals(returnedMeeting, futureMeeting);
  }
  
  @Test
  public void testGetMeetingByIdReturnsNullIfNone() {
    Meeting returnedMeeting = contactManager.getMeeting(100);
    
    assertThat(returnedMeeting, is(nullValue()));
  }
  
  @Test
  public void testGetMeetingByIdReturnsPastMeeting() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    Iterator<Contact> it = mikeSet.iterator();
    Contact mike = it.next();
    
    contactManager.addNewPastMeeting(mikeSet, pastDate, "notes");
    contactManager.addFutureMeeting(mikeSet, futureDate);
    
    List<PastMeeting> pastMeetingList = contactManager.getPastMeetingList(mike);
    Meeting pastMeeting = (Meeting) pastMeetingList.get(0);
    int pastMeetingId = pastMeeting.getId();
    
    Meeting returnedMeeting = contactManager.getMeeting(pastMeetingId);
    
    assertEquals(returnedMeeting, pastMeeting);
  }
  
  @Test
  public void testGetMeetingByIdReturnsFutureMeeting() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    Iterator<Contact> it = mikeSet.iterator();
    Contact mike = it.next();
    
    int futureMeetingId = contactManager.addFutureMeeting(mikeSet, futureDate);
    contactManager.addNewPastMeeting(mikeSet, pastDate, "notes");
    
    List<Meeting> futureMeetingList = contactManager.getFutureMeetingList(mike);
    Meeting futureMeeting = futureMeetingList.get(0);
    
    FutureMeeting returnedMeeting = contactManager.getFutureMeeting(futureMeetingId);
    
    assertEquals(returnedMeeting, futureMeeting);
  }
  
  // ### add Notes ###
  
  @Test
  public void testAddMeetingNotesThrowsNullPointerException() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    Iterator<Contact> it = mikeSet.iterator();
    Contact mike = it.next();
    
    contactManager.addNewPastMeeting(mikeSet, pastDate, "notes");
    List<PastMeeting> pastMeetingList = contactManager.getPastMeetingList(mike);
    int pastMeetingId = pastMeetingList.get(0).getId();
    
    try {
      contactManager.addMeetingNotes(pastMeetingId, null);
      fail();
    } catch (NullPointerException e) {
      
    }
  }
  
  @Test
  public void testAddMeetingNotesThrowsIllegalStateException() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    
    int futureMeetingId = contactManager.addFutureMeeting(mikeSet, futureDate);
    
    try {
      contactManager.addMeetingNotes(futureMeetingId, "notes");
      fail();
    } catch (IllegalStateException e) {
      
    }
  }
  
  @Test
  public void testAddMeetingNotesThrowsIllegalArgumentException() {
    try {
      contactManager.addMeetingNotes(100, "notes");
      fail();
    } catch (IllegalArgumentException e) {
      
    }
  }
  
  @Test
  public void testAddMeetingNotesAddNotesToPastMeeting() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    Iterator<Contact> it = mikeSet.iterator();
    Contact mike = it.next();
    
    contactManager.addNewPastMeeting(mikeSet, pastDate, "Notes 1");
    List<PastMeeting> pastMeetingList = contactManager.getPastMeetingList(mike);
    int pastMeetingId = pastMeetingList.get(0).getId();
    
    contactManager.addMeetingNotes(pastMeetingId, "Notes 2");
    
    PastMeeting pastMeeting = contactManager.getPastMeeting(pastMeetingId);
    
    assertTrue(pastMeeting.getNotes().contains("Notes 1"));
    assertTrue(pastMeeting.getNotes().contains("Notes 2"));
  }
  
  @Test
  public void testAddMeetingNotesConvertsFutureMeetingToPastMeetingAndAddsNotes() {
    contactManager.addNewContact("mike", "notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");

    Calendar halfSecondAwayDate = Calendar.getInstance();
    halfSecondAwayDate.add(Calendar.MILLISECOND, 500);
    
    try {
      int convertMeetingId = contactManager.addFutureMeeting(mikeSet, halfSecondAwayDate);
      try {
        Thread.sleep(600);
      } catch (InterruptedException e) {
        fail("600ms sleep interupted");
      }
      try {
        contactManager.addMeetingNotes(convertMeetingId, "Convert Meeting Notes");
      } catch (IllegalStateException e) {
        fail("addMeetingNotes() asserts that meeting is in the future, even though date is the past.");
      }
      try {
        PastMeeting pastMeeting = contactManager.getPastMeeting(convertMeetingId);
        assertTrue(pastMeeting.getNotes().contains("Convert Meeting Notes"));
      } catch (IllegalArgumentException e) {
        fail("Couldn't find converted meeting as past meeting, meeting not converted.");
      }
    } catch (IllegalArgumentException e) {
      fail("addFutureMeeting() took longer than 500ms to be called, hence meeting was in the 'past'.");
    }
  }
  
  @Test
  public void testFlush() {
    contactManager.addNewContact("mike", "mike notes");
    contactManager.addNewContact("sue", "sue notes");
    Set<Contact> contactsSet = contactManager.getContacts("");
    
    contactManager.addFutureMeeting(contactsSet, futureDate);
    
    contactManager.flush();
  }
  
  /**
   * Test assumes flush() uses java serialization, to a file called contacts.txt.
   */
  @Test
  public void testFlushSavesContacts() {
    contactManager.addNewContact("mike", "mike notes");
    contactManager.addNewContact("sue", "sue notes");
    Contact mike = contactManager.getContacts("mike").iterator().next();
    Contact sue = contactManager.getContacts("sue").iterator().next();

    contactManager.flush();
    
    ObjectInputStream d = null;
    try {
        d = new ObjectInputStream(
                new BufferedInputStream(
                        new FileInputStream(FILENAME)));
    } catch (IOException e) {
        e.printStackTrace();
    }
    ContactManager deserializedContactManager  = null;
    try {
        deserializedContactManager = (ContactManager) d.readObject();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }

    try {
        d.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    Contact deserializedMike = deserializedContactManager.getContacts("mike").iterator().next();
    Contact deserializedSue = deserializedContactManager.getContacts("sue").iterator().next();

    assertEquals(mike.getId(), deserializedMike.getId());
    assertEquals(mike.getNotes(), deserializedMike.getNotes());
    assertEquals(sue.getId(), deserializedSue.getId());
    assertEquals(sue.getNotes(), deserializedSue.getNotes());
  }
  
  /**
   * Test assumes flush() uses java serialization, to a file called contacts.txt.
   */
  @Test
  public void testFlushSavesPastMeetings() {
    contactManager.addNewContact("mike", "mike notes");
    contactManager.addNewContact("sue", "sue notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    Iterator<Contact> itMike = mikeSet.iterator();
    Contact mike = itMike.next();
    Set<Contact> sueSet = contactManager.getContacts("sue");
    Iterator<Contact> itSue = sueSet.iterator();
    Contact sue = itSue.next();
    
    contactManager.addNewPastMeeting(mikeSet, pastDate, "mike meeting");
    int mikeMeetingId = contactManager.getPastMeetingList(mike).get(0).getId();
    contactManager.addNewPastMeeting(sueSet, pastDate, "sue meeting");
    int sueMeetingId = contactManager.getPastMeetingList(sue).get(0).getId();
    
    contactManager.flush();
    
    ObjectInputStream d = null;
    try {
        d = new ObjectInputStream(
                new BufferedInputStream(
                        new FileInputStream(FILENAME)));
    } catch (IOException e) {
        e.printStackTrace();
    }
    ContactManager deserializedContactManager  = null;
    try {
        deserializedContactManager = (ContactManager) d.readObject();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }

    try {
        d.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    Contact deserializedMike = deserializedContactManager.getContacts("mike").iterator().next();
    Contact deserializedSue = deserializedContactManager.getContacts("sue").iterator().next();

    List<PastMeeting> mikePastMeetingList = deserializedContactManager.getPastMeetingList(deserializedMike);
    List<PastMeeting> suePastMeetingList = deserializedContactManager.getPastMeetingList(deserializedSue);
    
    assertEquals(1, mikePastMeetingList.size());
    assertEquals(pastDate, mikePastMeetingList.get(0).getDate());
    assertEquals("mike meeting", mikePastMeetingList.get(0).getNotes());
    assertEquals(mikeMeetingId, mikePastMeetingList.get(0).getId());
    
    assertEquals(1, suePastMeetingList.size());
    assertEquals(pastDate, suePastMeetingList.get(0).getDate());
    assertEquals("sue meeting", suePastMeetingList.get(0).getNotes());
    assertEquals(sueMeetingId, suePastMeetingList.get(0).getId());
  }
  
  /**
   * Test assumes flush() uses java serialization, to a file called contacts.txt.
   */
  @Test
  public void testFlushSavesFutureMeetings() {
    contactManager.addNewContact("mike", "mike notes");
    contactManager.addNewContact("sue", "sue notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    Set<Contact> sueSet = contactManager.getContacts("sue");
    
    int mikeMeetingId = contactManager.addFutureMeeting(mikeSet, futureDate);
    int sueMeetingId = contactManager.addFutureMeeting(sueSet, futureDate);
    
    contactManager.flush();
    
    ObjectInputStream d = null;
    try {
        d = new ObjectInputStream(
                new BufferedInputStream(
                        new FileInputStream(FILENAME)));
    } catch (IOException e) {
        e.printStackTrace();
    }
    ContactManager deserializedContactManager  = null;
    try {
        deserializedContactManager = (ContactManager) d.readObject();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }

    try {
        d.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    Contact deserializedMike = deserializedContactManager.getContacts("mike").iterator().next();
    Contact deserializedSue = deserializedContactManager.getContacts("sue").iterator().next();
    
    List<Meeting> mikePastMeetingList = deserializedContactManager.getFutureMeetingList(deserializedMike);
    List<Meeting> suePastMeetingList = deserializedContactManager.getFutureMeetingList(deserializedSue);
    
    assertEquals(1, mikePastMeetingList.size());
    assertEquals(futureDate, mikePastMeetingList.get(0).getDate());
    assertEquals(mikeMeetingId, mikePastMeetingList.get(0).getId());
    
    assertEquals(1, suePastMeetingList.size());
    assertEquals(futureDate, suePastMeetingList.get(0).getDate());
    assertEquals(sueMeetingId, suePastMeetingList.get(0).getId());
  }
  
  /**
   * Test assumes flush() uses java serialization, to a file called contacts.txt.
   */
  @Test
  public void testFlushPreservesUniqueContactIdGeneration() {
    
    //TODO: separate runtimes into threads? ATM static fields aren't reset, so test isn't complete.
    
    contactManager.addNewContact("mike", "mike notes");
    contactManager.addNewContact("sue", "sue notes");
    contactManager.addNewContact("kevin", "kevin notes");
    List<Integer> preFlushIds = new LinkedList<Integer>();
    preFlushIds.add(contactManager.getContacts("mike").iterator().next().getId());
    preFlushIds.add(contactManager.getContacts("sue").iterator().next().getId());
    preFlushIds.add(contactManager.getContacts("kevin").iterator().next().getId());

    contactManager.flush();
    
    ObjectInputStream d = null;
    try {
        d = new ObjectInputStream(
                new BufferedInputStream(
                        new FileInputStream(FILENAME)));
    } catch (IOException e) {
        e.printStackTrace();
    }
    ContactManager deserializedContactManager  = null;
    try {
        deserializedContactManager = (ContactManager) d.readObject();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }

    try {
        d.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    deserializedContactManager.addNewContact("molly", "molly notes");
    int postFlushId = deserializedContactManager.getContacts("molly").iterator().next().getId();
    
    assertFalse(preFlushIds.contains(postFlushId));
  }
  
  /**
   * Test assumes flush() uses java serialization, to a file called contacts.txt.
   */
  @Test
  public void testFlushPreservesUniqueMeetingIdGeneration() {
        
    contactManager.addNewContact("mike", "mike notes");
    Set<Contact> mikeSet = contactManager.getContacts("mike");
    List<Integer> preFlushIds = new LinkedList<Integer>();
    preFlushIds.add(contactManager.addFutureMeeting(mikeSet, futureDate));
    preFlushIds.add(contactManager.addFutureMeeting(mikeSet, futureDate));
    preFlushIds.add(contactManager.addFutureMeeting(mikeSet, futureDate));

    contactManager.flush();
    
    ObjectInputStream d = null;
    try {
        d = new ObjectInputStream(
                new BufferedInputStream(
                        new FileInputStream(FILENAME)));
    } catch (IOException e) {
        e.printStackTrace();
    }
    ContactManager deserializedContactManager  = null;
    try {
        deserializedContactManager = (ContactManager) d.readObject();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }

    try {
        d.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    Set<Contact> deserializedMikeSet = deserializedContactManager.getContacts("mike");
    int postFlushId = deserializedContactManager.addFutureMeeting(deserializedMikeSet, futureDate);
    
    assertFalse(preFlushIds.contains(postFlushId));
  }
  
  @Test
  public void testConstructorCreatesFileIfNotPresent() {
    
  }
  
  @Test
  public void testConstructorLoadsFromFile() {
    contactManager.addNewContact("mike", "mike notes");
    contactManager.addNewContact("sue", "sue notes");
    contactManager.addNewContact("kevin", "kevin notes");
    Contact kevin = contactManager.getContacts("kevin").iterator().next();
    
    ObjectOutputStream encode = null;
    try {
        encode = new ObjectOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream("specific_test.txt")));
    } catch (FileNotFoundException e) {
        System.err.println("encoding... " + e);
    } catch (IOException e1) {
        e1.printStackTrace();
    }

    try {
        encode.writeObject(contactManager);
    } catch (IOException e2) {
        e2.printStackTrace();
    }
    try {
        encode.close();
    } catch (IOException e3) {
        e3.printStackTrace();
    }
    
    ContactManager reconstructedCM = new ContactManagerImpl("specific_test.txt");
    
    assertEquals(3, reconstructedCM.getContacts("").size());
    assertEquals(1, reconstructedCM.getContacts("kevin").size());
    Contact reconstructedKevin = reconstructedCM.getContacts("kevin").iterator().next();
    assertEquals(reconstructedKevin.getId(), kevin.getId());
    assertEquals(reconstructedKevin.getName(), kevin.getName());
    assertEquals(reconstructedKevin.getNotes(), kevin.getNotes());
    (new File("specific_test.txt")).delete();
  }
}