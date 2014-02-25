package impl;

import interfaces.Contact;


public class ContactImpl extends DomainObject implements Contact {
//  private static AtomicInteger idBank = new AtomicInteger();
//  private final int id;
  private final String name;
  private String notes = "";

  public ContactImpl(String name) {
//    id = idBank.incrementAndGet();
    this.name = name;
  }
  
  public ContactImpl(String name, String note) {
//    id = idBank.incrementAndGet();
    this.name = name;
    this.addNotes(note);
  }
  
  @Override
  public String getNotes() {
    return notes;
  }

  @Override
  public void addNotes(String note) {
    if (!notes.equals("")) notes += "; ";
    notes += note;
  }

//  @Override
//  public int getId() {
//    return id;
//  }

  @Override
  public String getName() {
    return name;
  }

}