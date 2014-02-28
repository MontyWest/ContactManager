package impl;

import java.io.ObjectStreamException;
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
  
  /***
   * readResolve to reset the idBank after the object has been
   * deserialized. Protected ensures that this behaviour is
   * inherited by Contact and Meeting.
   * Statement is synchronized to idBank to ensure correct
   * behaviour if read process is concurrent.
   * 
   * @return this
   * @throws ObjectStreamException
   */
  protected Object readResolve() throws ObjectStreamException{
    synchronized(idBank) {
      if (this.id > idBank.intValue()) {
        idBank.set(this.id);
      }
    }
    return this;
  }
}