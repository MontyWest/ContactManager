package impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class DomainObject implements Serializable{

  private static final long serialVersionUID = 1L;
  private static AtomicInteger idBank = new AtomicInteger();
  private final int id;
  
  public DomainObject() {
    this.id = idBank.incrementAndGet();
  }
  
  public int getId() {
    return id;
  }
  
  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    if (this.id > idBank.intValue()) {
      idBank.set(this.id);
    }
  }
}