package test;

import interfaces.Contact;

public class MockContactImpl implements Contact {
  
  public int getId() {
    return 0;
  }
  
  public String getName() {
    return null;
  }
  
  public String getNotes() {
    return null;
  }
  
  public void addNotes(String note) {
    
  }
}