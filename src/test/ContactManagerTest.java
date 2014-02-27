package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import interfaces.Contact;
import interfaces.ContactManager;

import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class ContactManagerTest {
  
  ContactManager contactManager;
  
  @Before
  public void setUp() {
    contactManager = new ContactManagerImpl();
  }
  
  @Test
  public void testAddNewContactNullNameException() {
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
  
}