package impl;

import interfaces.Contact;

import java.io.Serializable;

public class ContactImpl extends DomainObject implements Contact, Serializable {

  private static final long serialVersionUID = 2L;
  private final String name;
  private String notes = "";

  public ContactImpl() {
    name = null;
  }
  
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
  
  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof ContactImpl)) {
      return false;
    }
    ContactImpl contact = (ContactImpl) obj;
    return this.getId() == contact.getId();
  }
  
  @Override
  public String toString() {
    return "[Contact-> Id: " + getId() + ", Name: " + getName() + ", Notes: " + getNotes() + "]";
  }
}