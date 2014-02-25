package test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import impl.ContactImpl;
import interfaces.Contact;

import org.junit.Before;
import org.junit.Test;

public class ContactTest {

  @Before
  public void setUp() {

  }

  @Test
  public void testContactConstructorName() {
    try {
      new ContactImpl("name");
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testContactConstructorSetName() {
    Contact sue = new ContactImpl("sue");
    assertEquals("sue", sue.getName());
  }

  @Test
  public void testContactConstructorNameNotes() {
    try {
      new ContactImpl("name", "notes");
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testContactConstructorSetNameNotes() {
    Contact sue = new ContactImpl("sue", "notes1");
    assertEquals("sue", sue.getName());
    assertEquals("notes1",  sue.getNotes());
  }
  
  @Test
  public void testGetNotesNone() {
    Contact sue = new ContactImpl("sue");
    assertEquals("", sue.getNotes());
  }
  
  @Test
  public void testAddNotesSingle() {
    Contact sue = new ContactImpl("sue");
    sue.addNotes("notes1");
    assertEquals("notes1", sue.getNotes());
  }
  
  
  @Test
  public void testAddNotesMultiple() {
    Contact sue = new ContactImpl("sue", "notes1");
    sue.addNotes("notes2");
    sue.addNotes("notes3");
    String returned = sue.getNotes();
    
    assertTrue(returned.contains("notes1"));
    assertTrue(returned.contains("notes2"));
    assertTrue(returned.contains("notes3"));
  }
  
  @Test
  public void testContactUniqueIds() {
    Contact jim = new ContactImpl("jim");
    Contact mike = new ContactImpl("mike");
    System.out.print(jim.getId());
    
    assertThat(jim.getId(), is(not(equalTo(mike.getId()))));
  }
  
}