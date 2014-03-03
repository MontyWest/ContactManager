package impl;

import interfaces.Contact;
import interfaces.ContactManager;
import interfaces.FutureMeeting;
import interfaces.Meeting;
import interfaces.PastMeeting;

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
  
  private List<PastMeeting> pastMeetings = new LinkedList<PastMeeting>();
  private List<FutureMeeting> futureMeetings = new LinkedList<FutureMeeting>();
  private Set<Contact> contacts = new HashSet<Contact>();

  @Override
  public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
    if (!areValidContacts(contacts) || isInPast(date)) {
      throw new IllegalArgumentException();
    }
    FutureMeeting newFutureMeeting = new MeetingImpl(date, contacts);
    futureMeetings.add(newFutureMeeting);
    return newFutureMeeting.getId();
  }
  
  @Override
  public PastMeeting getPastMeeting(int id) {
    Meeting meeting = getMeeting(id);
    
    if (meeting == null) {
      return null;
    } else if (futureMeetings.contains((FutureMeeting) meeting)) {
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
    } else if (pastMeetings.contains((PastMeeting) meeting)) {
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
  
  
  private <T extends Meeting> void sortMeetingsByDate(List<T> meetingList) {
    Collections.sort(meetingList, new Comparator<T>() {
      
      public int compare(T m1, T m2) {
        return m1.getDate().compareTo(m2.getDate());
      }
    });
  }
  
}