package impl;

import interfaces.Contact;
import interfaces.ContactManager;
import interfaces.FutureMeeting;
import interfaces.Meeting;
import interfaces.PastMeeting;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/***
 * Loads from file on construction, the default file is contacts.txt, but an alternate filename can
 * be passed as a parameter into the constructor. Encodes to same file when flush() is called.
 * 
 * Past meetings and Future meeting are separated in to two lists, however due to the passing
 * of time meetings held in the future meeting list may have a date in the past, to convert you must
 * call addMeetingNotes().
 * 
 * @author montywest
 *
 */
public class ContactManagerImpl implements ContactManager, Serializable {
  
  private static final long serialVersionUID = 4L;
  
  private List<PastMeeting> pastMeetings = new LinkedList<PastMeeting>();
  private List<FutureMeeting> futureMeetings = new LinkedList<FutureMeeting>();
  private Set<Contact> contacts = new HashSet<Contact>();
  private final String filename;
  
  /***
   * Default constructor loads from default file: contacts.txt.
   * This filename is then saved to object for later encoding.
   */
  public ContactManagerImpl(){
    loadFromFile("contacts.txt");
    this.filename = "contacts.txt";
  }
  
  /***
   * Loads from filename parameter
   * This filename is then saved to object for later encoding.
   * 
   * @param filename
   */
  public ContactManagerImpl(String filename) {
    loadFromFile(filename);
    this.filename = filename;
  }

  /***
   * Loads object from filename parameter.
   * If no file of that name exists then it will create one.
   * IF file is empty or corrupt, nothing will be loaded.
   * 
   * Creates a new object from the file then acts like a copy
   * constructor.
   * 
   * @param filename
   */
  private void loadFromFile(String filename) {
    File file = new File(filename);
    if(!file.isFile()) {
      try {
        file.createNewFile();
        System.out.println("");
        System.out.print("New file " + filename + " created.");
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      
      ObjectInputStream d = null;
      try {
          d = new ObjectInputStream(
                  new BufferedInputStream(
                          new FileInputStream(filename)));
      } catch (FileNotFoundException e) {
          System.out.println("");
          System.out.print("File " + filename + " not found. ");
      } catch (EOFException e) {
          System.out.println("");
          System.out.print("File " + filename + " empty or corrupt. ");
      } catch (IOException e) {
          e.printStackTrace();
      }
      if(!(d == null)) {
        ContactManagerImpl deserializedContactManager  = null;
        try {
            deserializedContactManager = (ContactManagerImpl) d.readObject();
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
        if(!(deserializedContactManager == null)) {
          this.pastMeetings = deserializedContactManager.getPastMeetings();
          this.futureMeetings = deserializedContactManager.getFutureMeetings();
          this.contacts = deserializedContactManager.getContacts();
          System.out.println("File " + filename + " loaded.");
          return;
        }
      }
      System.out.println("Nothing loaded.");
    }
  }
  
  public List<PastMeeting> getPastMeetings() {
    return pastMeetings;
  }

  public List<FutureMeeting> getFutureMeetings() {
    return futureMeetings;
  }

  public Set<Contact> getContacts() {
    return contacts;
  }

  public String getFilename() {
    return filename;
  }
  

  /***
   * @see "Adds a new FutureMeeting to the future meeting list."
   */
  @Override
  public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
    if (!areValidContacts(contacts) || isInPast(date)) {
      throw new IllegalArgumentException();
    }
    FutureMeeting newFutureMeeting = new MeetingImpl(date, contacts);
    this.futureMeetings.add(newFutureMeeting);
    return newFutureMeeting.getId();
  }
  
  /***
   * @see "Calls getMeeting() and then performs checks on meeting's validity."
   */
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
  
  /***
   * @see "Calls getMeeting() and then performs checks on meeting's validity."
   */
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
  
  /***
   * @see "Iterates throught both meeting lists and returns the one with the
   * matching id."
   */
  @Override
  public Meeting getMeeting(int id) {
    for (PastMeeting pm : pastMeetings) {
      if(pm.getId() == id) {
        return (Meeting) pm;
      }
    }
    
    for (FutureMeeting fm : futureMeetings) {
      if(fm.getId() == id) {
        return (Meeting) fm;
      }
    }
    
    return null;
  }
  
  /***
   * @see "Creates a new list and adds to it if a contact is found in a
   * meetings contact set when iterating through the future meeting list.
   * Meetings are then sorted by date, furthest in the past to furthest 
   * in the future."
   */
  @Override
  public List<Meeting> getFutureMeetingList(Contact contact) {
    if(!isValidContact(contact)) {
      throw new IllegalArgumentException();
    }
    
    List<Meeting> returnList = new LinkedList<Meeting>();
    for (FutureMeeting fm : futureMeetings) {
      Set<Contact> candidateContacts = fm.getContacts();
      if (candidateContacts.contains(contact)) {
        returnList.add((Meeting) fm);
      }
    }
    
    sortMeetingsByDate(returnList);
    
    return returnList;
  }
  
  /***
   * @see "Meetings are added to a new list if their date is on the same day as the param.
   * Iterates through both meeting lists as PastMeetings with future dates
   * may be in the past meetings list (due to the specification of addNewPastMeeting)
   * and future meetings with past dates may be in the future meeting list (due to the
   * passing of time).
   * Meetings are then sorted by time, earliest to latest."
   */
  @Override
  public List<Meeting> getFutureMeetingList(Calendar date) {
    
    List<Meeting> returnList = new LinkedList<Meeting>();
    
    for (PastMeeting pm : pastMeetings) {
      if(areSameDay(date, pm.getDate())) {
        returnList.add((Meeting) pm);
      }
    }
    
    for (FutureMeeting fm : futureMeetings) {
      if(areSameDay(date, fm.getDate())) {
        returnList.add((Meeting) fm);
      }
    }
    
    sortMeetingsByDate(returnList);
    
    return returnList;
  }
  
  /***
   * @see "Creates a new list and adds to it if a contact is found in a
   * meetings contact set when iterating through the past meeting list.
   * Meetings are then sorted by date, furthest in the past to furthest 
   * in the future."
   */
  @Override
  public List<PastMeeting> getPastMeetingList(Contact contact) {
    if(!isValidContact(contact)) {
      throw new IllegalArgumentException();
    }
    
    List<PastMeeting> returnList = new LinkedList<PastMeeting>();
    for (PastMeeting pm : pastMeetings) {
      Set<Contact> candidateContacts = pm.getContacts();
      if (candidateContacts.contains(contact)) {
        returnList.add(pm);
      }
    }
    
    sortMeetingsByDate(returnList);
    
    return returnList;
  }
  
  /***
   * @see "Adds new PastMeeting to the past meeting list.
   * A past meeting can be added with a future date, as per the interfaces
   * specification (which is different from addFutureMeeting())."
   */
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

  /***
   * @see "First gets meeting by id and then checks whether is a FutureMeeting or PastMeeting.
   * If it is a FutureMeeting it checks if the date is now in the past, if not it throws
   * an IllegalArgumentException. If it is it removes if from the future meeting list,
   * casts it to a PastMeeting and adds it to the past meeting list.
   * Lastly, it add notes to the newly cast meeting, or one found in the past meeting list."
   */
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
  
  /***
   * @see "Adds new contact to contact set."
   */
  @Override
  public void addNewContact(String name, String notes) {
    if (notes == null || name == null) {
      throw new NullPointerException();
    }
    
    Contact newContact = new ContactImpl(name, notes);
    contacts.add(newContact);
  }
  
  /***
   * @see "Iterates through the array of ids, on each iteration it iterates through
   * the set of contacts, and stops looping if there is an id match, if the
   * iterator gets to the end of the set then the id is invalid and an
   * IllegalArgumentException is thrown. Any matches are added to a new set and
   * then returned."
   */
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
  
  /***
   * @see "Iterated through contact set and adds contact to a new set if the contact name
   * contains (as a substring) the parameter. Then returns this new set."
   */
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
  
  /***
   * @see "Encodes the object to filename field using Java's serialization: ObjectOutputStream.
   * It will not make a new file if file is not found, as the constructor does this."
   */
  @Override
  public void flush() {

    ObjectOutputStream encode = null;
    try {
        encode = new ObjectOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(this.filename)));
    } catch (FileNotFoundException e) {
        System.err.println("encoding... " + e);
    } catch (IOException e1) {
        e1.printStackTrace();
    }
    try {
        encode.writeObject(this);
        System.out.println("Saved.");
    } catch (IOException e2) {
        e2.printStackTrace();
    }
    try {
        encode.close();
    } catch (IOException e3) {
        e3.printStackTrace();
    }
  }
  
  @Override
  public String toString() {
    String str = "\nFilename: " + filename + "\n" +  "\n### Contacts ###\n";
    Iterator<Contact> contactIt = contacts.iterator();
    while (contactIt.hasNext()) {
      str += contactIt.next() + "\n";
    }
    str += "\n### Past Meetings ###\n";
    for (PastMeeting pm : pastMeetings) {
      str += pm + "\n";
    }
    str += "\n### Future Meetings ###\n";
    for (FutureMeeting pm : futureMeetings) {
      str += pm + "\n";
    }
    return str;
  }
  
  
  /***
   * @param contacts
   * @return true if all contacts are in this objects contact set.
   */
  private boolean areValidContacts(Set<Contact> contacts) {
    return this.contacts.containsAll(contacts);
  }
  
  /***
   * @param contact
   * @return true is contact is in this objects contact set.
   */
  private boolean isValidContact(Contact contact) {
    return this.contacts.contains(contact);
  }
  
  /***
   * Regardless of date.
   * 
   * @param meeting
   * @return true if meeting is in futureMeetings list.
   */
  private boolean isFutureMeeting(Meeting meeting) {
    return futureMeetings.contains((FutureMeeting) meeting);
  }
  
  /***
   * Regardless of date.
   * 
   * @param meeting
   * @return true if meeting is in pastMeetings list.
   */
  private boolean isPastMeeting(Meeting meeting) {
    return pastMeetings.contains((PastMeeting) meeting);
  }
  
  /***
   * Generic to allow Meeting, FutureMeeting and PastMeeting.
   * Sorts meetings by date,  farthest in past to farthest in future.
   * 
   * @param meetingList
   */
  private static <T extends Meeting> void sortMeetingsByDate(List<T> meetingList) {
    Collections.sort(meetingList, new Comparator<T>() {
      public int compare(T m1, T m2) {
        return m1.getDate().compareTo(m2.getDate());
      }
    });
  }
  
  /***
   * If date is now (to the millisecond) then returns false.
   * 
   * @param date
   * @return true if date is strictly in past.
   */
  private static boolean isInPast(Calendar date) {
    return date.before(Calendar.getInstance());
  }
  
  /***
   * This ensures that if date is now (to the millisecond)
   * is counted as a future date. This is the negation of isInPast().
   * 
   * @param date
   * @return true if date is now or in future.
   */
  private static boolean isInFuture(Calendar date) {
    return !isInPast(date);
  }
  
  /***
   * @param d1 (Calendar)
   * @param d2 (Calendar)
   * @return true if d1 and d2 are on the same date.
   */
  private static boolean areSameDay(Calendar d1, Calendar d2) {
    boolean sameDay = d1.get(Calendar.DAY_OF_MONTH) == d2.get(Calendar.DAY_OF_MONTH);
    boolean sameMonth = d1.get(Calendar.MONTH) == d2.get(Calendar.MONTH);
    boolean sameYear = d1.get(Calendar.YEAR) == d2.get(Calendar.YEAR);
    return sameDay && sameMonth && sameYear;
  }
  
  
}