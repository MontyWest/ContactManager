package impl;

import interfaces.Contact;

public class ContactImpl extends DomainObject implements Contact {

  private static final long serialVersionUID = 2L;
  private final String name;
  private String notes = "";

  public ContactImpl(String name) {
    this.name = name;
  }

  public ContactImpl(String name, String note) {
    this.name = name;
    this.addNotes(note);
  }

  @Override
  public String getNotes() {
    return notes;
  }

  @Override
  public void addNotes(String note) {
    if (!notes.equals(""))
      notes += "; ";
    notes += note;
  }

  @Override
  public String getName() {
    return name;
  }
}