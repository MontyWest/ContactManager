package impl;

import interfaces.Contact;
import interfaces.ContactManager;
import interfaces.FutureMeeting;
import interfaces.Meeting;
import interfaces.PastMeeting;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ContactManagerImpl implements ContactManager, Serializable {
  
  private static final long serialVersionUID = 4L;
  
  private List<PastMeeting> pastMeetings = new LinkedList<PastMeeting>();
  private List<FutureMeeting> futureMeetings = new LinkedList<FutureMeeting>();
  private Set<Contact> contacts = new HashSet<Contact>();

  @Override
  public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
    if (!areValidContacts(contacts) || isInPast(date)) {
      throw new IllegalArgumentException();
    }
    FutureMeeting newFutureMeeting = new MeetingImpl(date, contacts);
    this.futureMeetings.add(newFutureMeeting);
    return newFutureMeeting.getId();
  }
  
  @Override
  public PastMeeting getPastMeeting(int id) {
    Meeting meeting = getMeeting(id);
    
    if (meeting == null) {
      return null;
    } else if (isFutureMeeting(meeting)) {
      throw new IllegalArgumentException();
    } else {
      return (PastMeeting) meeting;
    }
  }
  
  @Override
  public FutureMeeting getFutureMeeting(int id) {
    Meeting meeting = getMeeting(id);
    
    if (meeting == null) {
      return null;
    } else if (isPastMeeting(meeting)) {
      throw new IllegalArgumentException();
    } else {
      return (FutureMeeting) meeting;
    }
  }
  
  @Override
  public Meeting getMeeting(int id) {
    Iterator<PastMeeting> pastIt = pastMeetings.iterator();
    while (pastIt.hasNext()) {
      PastMeeting candidate = pastIt.next();
      if(candidate.getId() == id) {
        return (Meeting) candidate;
      }
    }
    
    Iterator<FutureMeeting> futureIt = futureMeetings.iterator();
    while (futureIt.hasNext()) {
      FutureMeeting candidate = futureIt.next();
      if(candidate.getId() == id) {
        return (Meeting) candidate;
      }
    }
    
    return null;
  }
  
  @Override
  public List<Meeting> getFutureMeetingList(Contact contact) {
    if(!isValidContact(contact)) {
      throw new IllegalArgumentException();
    }
    
    List<Meeting> returnList = new LinkedList<Meeting>();
    Iterator<FutureMeeting> futureIt = futureMeetings.iterator();
    while (futureIt.hasNext()) {
      FutureMeeting candidate = futureIt.next();
      Set<Contact> candidateContacts = candidate.getContacts();
      if (candidateContacts.contains(contact)) {
        returnList.add((Meeting) candidate);
      }
    }
    
    sortMeetingsByDate(returnList);
    
    return returnList;
  }
  
  /***
   * As all pastMeetings are definitely in the past, if date param
   * is in future there's no need to search through pastMeetings.
   * However as time moves forward, futureMeetings may have Meetings
   * with a date in the past, hence the need to always search futureMeetings
   * 
   * @param date 
   * @returns list of meeting on same day as date
   */
  @Override
  public List<Meeting> getFutureMeetingList(Calendar date) {
    
    List<Meeting> returnList = new LinkedList<Meeting>();
    
    if (isInPast(date)) {
      Iterator<PastMeeting> pastIt = pastMeetings.iterator();
      while (pastIt.hasNext()) {
        PastMeeting candidate = pastIt.next();
        if(areSameDay(date, candidate.getDate())) {
          returnList.add((Meeting) candidate);
        }
      }
    }
    
    Iterator<FutureMeeting> futureIt = futureMeetings.iterator();
    while (futureIt.hasNext()) {
      FutureMeeting candidate = futureIt.next();
      if(areSameDay(date, candidate.getDate())) {
        returnList.add((Meeting) candidate);
      }
    }
    
    sortMeetingsByDate(returnList);
    
    return returnList;
  }
  
  @Override
  public List<PastMeeting> getPastMeetingList(Contact contact) {
    if(!isValidContact(contact)) {
      throw new IllegalArgumentException();
    }
    
    List<PastMeeting> returnList = new LinkedList<PastMeeting>();
    Iterator<PastMeeting> pastIt = pastMeetings.iterator();
    while (pastIt.hasNext()) {
      PastMeeting candidate = pastIt.next();
      Set<Contact> candidateContacts = candidate.getContacts();
      if (candidateContacts.contains(contact)) {
        returnList.add(candidate);
      }
    }
    
    sortMeetingsByDate(returnList);
    
    return returnList;
  }
  
  @Override
  public void addNewPastMeeting(Set<Contact> contacts, Calendar date, String text) {
    if (contacts == null || date == null || text == null) {
      throw new NullPointerException();
    }
    if (contacts.size() == 0 || !areValidContacts(contacts)) {
      throw new IllegalArgumentException();
    }
    PastMeeting newPastMeeting = new MeetingImpl(date, contacts);
    newPastMeeting.addNotes(text);
    this.pastMeetings.add(newPastMeeting);
  }

  @Override
  public void addMeetingNotes(int id, String text) {
    if(text == null) {
      throw new NullPointerException();
    }
    
    Meeting meeting = getMeeting(id);
    if(meeting == null) {
      throw new IllegalArgumentException();
    } else if (isFutureMeeting(meeting)) {
      if(isInFuture(meeting.getDate())) {
        throw new IllegalStateException();
      }
      futureMeetings.remove((FutureMeeting) meeting);
      pastMeetings.add((PastMeeting) meeting);
    }
    PastMeeting pastMeeting = (PastMeeting) meeting;
    pastMeeting.addNotes(text);
  }
  
  @Override
  public void addNewContact(String name, String notes) {
    if (notes == null || name == null) {
      throw new NullPointerException();
    }
    
    Contact newContact = new ContactImpl(name, notes);
    contacts.add(newContact);
  }
  
  @Override
  public Set<Contact> getContacts(int... ids) {
   
    Set<Contact> returnSet = new HashSet<Contact>();
    for (int id : ids) {
      Iterator<Contact> it = contacts.iterator();
      Contact candidate;
      do {
         if(!it.hasNext()) {
           throw new IllegalArgumentException();
         }
      } while((candidate = it.next()).getId() != id);
      returnSet.add(candidate);
    }
    return returnSet;
  }
  
  @Override
  public Set<Contact> getContacts(String name) {
    if(name == null) {
      throw new NullPointerException();
    }
    
    Set<Contact> returnSet = new HashSet<Contact>();
    Iterator<Contact> it = contacts.iterator();
    while(it.hasNext()) {
      Contact candidate = it.next();
      if (candidate.getName().contains(name)) {
        returnSet.add(candidate);
      }
    }
    return returnSet;
  }
  
  @Override
  public void flush() {
    final String FILENAME = "contacts.txt"; //in object?
    
    XMLEncoder encode = null;
    try {
        encode = new XMLEncoder(
                new BufferedOutputStream(
                        new FileOutputStream(FILENAME)));
    } catch (FileNotFoundException e) {
        System.err.println("encoding... " + e);
    }

    encode.writeObject(this);
    encode.close();

  }
  
  /***
   * If date is now then returns false.
   * 
   * @param date
   * @return true if date is strictly in past
   */
  private boolean isInPast(Calendar date) {
    return date.before(Calendar.getInstance());
  }
  
  /***
   * This ensures that if date is now (to the millisecond)
   * is counted as a future date.
   * 
   * @param date
   * @return true if date is now or in future
   */
  private boolean isInFuture(Calendar date) {
    return !isInPast(date);
  }
  
  /***
   * 
   * @param d1 (Calendar)
   * @param d2 (Calendar)
   * @return true if d1 and d2 are on the same day
   */
  private boolean areSameDay(Calendar d1, Calendar d2) {
    boolean sameDay = d1.get(Calendar.DAY_OF_MONTH) == d2.get(Calendar.DAY_OF_MONTH);
    boolean sameMonth = d1.get(Calendar.MONTH) == d2.get(Calendar.MONTH);
    boolean sameYear = d1.get(Calendar.YEAR) == d2.get(Calendar.YEAR);
    return sameDay && sameMonth && sameYear;
  }
  
  /***
   * 
   * @param contacts
   * @return true if all contacts are in ContactManagers 
   *         contact set
   */
  private boolean areValidContacts(Set<Contact> contacts) {
    return this.contacts.containsAll(contacts);
  }
  
  /***
   * 
   * @param contact
   * @return true is contact is in contact set
   */
  private boolean isValidContact(Contact contact) {
    return this.contacts.contains(contact);
  }
  
  /***
   * Regardless of date.
   * 
   * @param meeting
   * @return true is meeting is in futureMeetings list
   */
  private boolean isFutureMeeting(Meeting meeting) {
    return futureMeetings.contains((FutureMeeting) meeting);
  }
  
  /***
   * Regardless of date.
   * 
   * @param meeting
   * @return true is meeting is in pastMeetings list
   */
  private boolean isPastMeeting(Meeting meeting) {
    return pastMeetings.contains((PastMeeting) meeting);
  }
  
  /***
   * Generic to allow Meeting, FutureMeeting and PastMeeting.
   * Sorts meetings by date,  farthest in past to farthest 
   * in future.
   * 
   * @param meetingList
   */
  private <T extends Meeting> void sortMeetingsByDate(List<T> meetingList) {
    Collections.sort(meetingList, new Comparator<T>() {
      
      public int compare(T m1, T m2) {
        return m1.getDate().compareTo(m2.getDate());
      }
    });
  }
  
}