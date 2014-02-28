package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import impl.ContactImpl;
import interfaces.Contact;
import interfaces.ContactManager;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
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
    Contact sue = new ContactImpl("sue", "notes");
    
    try {
      contactManager.getFutureMeetingList(sue);
      fail();
    } catch (IllegalArgumentException e) {
      
    }
  }
}