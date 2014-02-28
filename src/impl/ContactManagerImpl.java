package impl;

import interfaces.Contact;
import interfaces.ContactManager;
import interfaces.FutureMeeting;
import interfaces.PastMeeting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactManagerImpl implements ContactManager {
  
  private List<PastMeeting> pastMeetings = new ArrayList<PastMeeting>();
  private List<FutureMeeting> futureMeetings = new ArrayList<FutureMeeting>();
  private Set<Contact> contacts = new HashSet<Contact>();

  
  
}