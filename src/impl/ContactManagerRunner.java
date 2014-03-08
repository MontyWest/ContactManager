package impl;

import interfaces.Contact;
import interfaces.ContactManager;
import interfaces.Meeting;
import interfaces.PastMeeting;

import java.io.File;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ContactManagerRunner {
  
  private static PrintStream o = System.out;
  private static Scanner in = new Scanner(System.in);
  private static ContactManager cm;
  private static String filename;
  
  public static void main(String[] args) {
    if(args.length != 0) {
      String filename = args[0];
      cm = new ContactManagerImpl(filename);
      ContactManagerRunner.filename = filename;
    } else {
      cm = new ContactManagerImpl();
      ContactManagerRunner.filename = "";
    }
    menuRouter();
  }
  
  private static void menuRouter() {
    String menu = "\n\n"
      + "### MENU ###\n"
      + "1. Add Contact\n"
      + "2. View Contacts\n"
      + "3. Add Meeting\n"
      + "4. View Past Meeting\n"
      + "5. View Future Meeting\n"
      + "6. View Meeting by Date\n"
      + "7. Add Notes to Meeting\n"
      + "8. Save and Print\n"
      + "9. Save and Exit\n"
      + "0. Delete File and Exit\n";
    o.print(menu);
    int choice = -1;
    o.println("");
    while (choice < 0 || choice > 9) {
      o.print("Enter one of the options: ");
      String str = in.nextLine();
      try {
        choice = Integer.parseInt(str);
      } catch(NumberFormatException e){
        o.print("Enter a number. ");
      }
    }
    switch (choice) {
    case 1:
      addContact();
      break;
    case 2:
      getContacts(false);
      break;
    case 3:
      addMeeting();
      break;
    case 4:
      getPastMeetings();
      break;
    case 5:
      getFutureMeetings();
      break;
    case 6:
      getMeetingsByDate();
      break;
    case 7:
      addNotes();
      break;
    case 8:
      save();
      print();
      break;
    case 9:
      save();
      exit();
      break;
    case 0:
      deleteFile();
      exit();
    }
    o.print("Press enter to return to menu.");
    in.nextLine();
    menuRouter();
  }
  
  private static void addContact() {
    o.println("");
    o.println("Adding contact...");
    o.print("Name: ");
    String name = in.nextLine();
    o.print("Notes: ");
    String notes = in.nextLine();
    try {
      cm.addNewContact(name, notes);
      o.print("Contact Added: " + cm.getContacts(name));
    } catch (NullPointerException e) {
      o.println("NullPointerException thrown.");
    }
    o.println("");
  }
  
  private static Set<Contact> getContacts(boolean loop) {
    o.println("");
    o.println("Getting contacts...");
    Set<Contact> contacts = new HashSet<Contact>();
    o.print("Enter contact id (y/n)? ");
    if(in.nextLine().equals("y")) {
      int id = 0;
      while(id == 0) {
        o.print("Enter contact id: ");
        try {
          id = Integer.parseInt(in.nextLine());
        } catch(NumberFormatException e){
          o.print("Not valid, ");
        }
      }
      try {
        contacts = cm.getContacts(id);
      } catch (IllegalArgumentException e) {
        o.println("No contact with that id.");
      }
    } else {
      o.print("Name: ");
      String name = in.nextLine();
      try {
        contacts = cm.getContacts(name);
      } catch (NullPointerException e) {
        o.println("Name is null, returning all.");
        contacts = cm.getContacts("");
      }
    }
    if(contacts.isEmpty()) {
      o.println("No contact found");
      if(loop) {
        contacts = getContacts(true);
      }
    } else {
      o.println(contacts);
      o.println("");
    }
    return contacts;
  }

  private static void addMeeting() {
    o.println("");
    if(!contactCheck()) return;
    o.println("Adding meeting...");
    Calendar date = getDate();
    Set<Contact> contacts = getContacts(true);
    if(isInPast(date)) {
      o.print("Meeting Notes: ");
      String notes = in.nextLine();
      try {
        cm.addNewPastMeeting(contacts, date, notes);
        o.println("Past Meeting Added.");
      } catch (NullPointerException e) {
        o.println("NullPointerException thrown.");
      } catch (IllegalArgumentException e) {
        o.println("IllegalArgumentException thrown.");
      }
    } else {
      try {
        int id = cm.addFutureMeeting(contacts, date);
        o.println("Future Meeting Added: " + cm.getFutureMeeting(id));
      } catch (NullPointerException e) {
        o.println("NullPointerException thrown.");
      } catch (IllegalArgumentException e) {
        o.println("IllegalArgumentException thrown.");
      }
    }
    o.println("");
  }

  private static List<PastMeeting> getPastMeetings() {
    o.println("");
    if(!contactCheck()) return null;
    o.println("Getting past meetings...");
    List<PastMeeting> pastMeetings = new LinkedList<PastMeeting>();
    o.print("Enter meeting id (y/n)? ");
    if(in.nextLine().equals("y")) {
      int id = 0;
      while(id == 0) {
        o.print("Enter meeting id: ");
        try {
          id = Integer.parseInt(in.nextLine());
        } catch(NumberFormatException e){
          o.print("Not valid, ");
        }
      }
      try {
        pastMeetings.add(cm.getPastMeeting(id));
      } catch (IllegalArgumentException e) {
        o.println("That meeting is in the future.");
      }
    } else {
      Set<Contact> contacts = getContacts(true);
      while (contacts.isEmpty()) {
        o.println("No contact found");
        contacts = getContacts(true);
      }
      Contact contact = contacts.iterator().next();
      try {
        pastMeetings = cm.getPastMeetingList(contact);
      } catch(IllegalArgumentException e) {
        o.println("IllegalArgumentException thrown.");
      }
    }
    o.println(pastMeetings);
    o.println("");
    return pastMeetings;
  }
  
  private static List<Meeting> getFutureMeetings() {
    o.println("");
    if(!contactCheck()) return null;
    o.println("Getting future meetings...");
    List<Meeting> futureMeetings = new LinkedList<Meeting>();
    o.print("Enter meeting id (y/n)? ");
    if(in.nextLine().equals("y")) {
      int id = 0;
      while(id == 0) {
        o.print("Enter meeting id: ");
        try {
          id = Integer.parseInt(in.nextLine());
        } catch(NumberFormatException e){
          o.print("Not valid, ");
        }
      }
      try {
        futureMeetings.add((Meeting)cm.getFutureMeeting(id));
      } catch (IllegalArgumentException e) {
        o.println("That meeting is in the past.");
      }
    } else {
      Set<Contact> contacts = getContacts(true);
      while (contacts.isEmpty()) {
        o.println("No contact found");
        contacts = getContacts(true);
      }
      Contact contact = contacts.iterator().next();
      try {
        futureMeetings = cm.getFutureMeetingList(contact);
      } catch(IllegalArgumentException e) {
        o.println("IllegalArgumentException thrown.");
      }
    }
    o.println(futureMeetings);
    o.println("");
    return futureMeetings;
  }
  
  private static List<Meeting> getMeetingsByDate() {
    o.println("");
    if(!contactCheck()) return null;
    o.println("Getting meetings...");
    List<Meeting> meetings = new LinkedList<Meeting>();
    Calendar date = getDate();
    meetings = cm.getFutureMeetingList(date);
    o.println(meetings);
    o.println("");
    return meetings;
  }
  
  private static void addNotes() {
    o.println("");
    if(!contactCheck()) return;
    o.println("Adding notes to meeting...");
    int id = 0;
    while(id == 0) {
      o.print("Enter meeting id: ");
      try {
        id = Integer.parseInt(in.nextLine());
      } catch(NumberFormatException e){
        o.print("Not valid, ");
      }
    }
    o.print("Notes: ");
    String notes = in.nextLine();
    try {
      cm.addMeetingNotes(id, notes);
      try {
        PastMeeting meeting = cm.getPastMeeting(id);
        o.println(meeting);
      } catch (IllegalArgumentException e) {
        o.println("Fault: Meeting not converted.");
      }
    } catch (NullPointerException e) {
      o.println("NullPointerException thrown.");
    } catch (IllegalArgumentException e) {
      o.println("Meeting doesn't exist.");
    } catch (IllegalStateException e) {
      o.println("Meeting hasn't happened yet.");
    }
  }

  private static void save() {
    o.println("");
    o.println("Saving...");
    cm.flush();
    o.println("");
  }
  
  private static void print() {
    o.println(cm);
  }
  
  private static void deleteFile() {
    o.println("Deleting " + filename + "...");
    if (!filename.equals("")) {
      File file = new File(filename);
      file.delete();
      o.println("Deleted.");
    } else {
      o.println("ERROR: You cannot delete the default file.");
    }
  }
  
  private static void exit() {
    o.println("Exiting...");
    System.exit(0);
  }
  
  private static boolean contactCheck() {
    if(cm.getContacts("").isEmpty()) {
      o.println("You must have added some contacts before doing this.");
      return false;
    }
    return true;
  }
  
  private static Calendar getDate() {
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    String dateStr = "00-00-0000";
    Calendar date = Calendar.getInstance();
    int hours = date.get(Calendar.HOUR_OF_DAY);
    while(dateStr.equals("00-00-0000")) {
        o.print("Enter date in format dd-mm-yyyy: ");
        dateStr = in.nextLine();
        try {
          date.setTime(sdf.parse(dateStr));
          date.add(Calendar.HOUR_OF_DAY, hours);
          date.add(Calendar.MINUTE, 59);
          //o.print(date);
        } catch (ParseException e) {
          o.println("Invalid Date.");
          dateStr = "00-00-0000";
        }
    }
    return date;
  }
  
  private static boolean isInPast(Calendar date) {
    return date.before(Calendar.getInstance());
  }
}