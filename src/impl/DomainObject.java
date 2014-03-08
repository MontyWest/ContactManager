package impl;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class DomainObject implements Serializable {

  private static final long serialVersionUID = 1L;
  private static AtomicInteger idBank = new AtomicInteger();
  private final int id;
  
  /***
   * Assignment of ids is done during construction, by incrementing an
   * Atomic Integer (to assure correct behavior in a concurrent system)
   * This Integer will hold the last id assigned, and will increment before
   * assigning the next.
   */
  public DomainObject() {
    this.id = idBank.incrementAndGet();
  }
  
  public int getId() {
    return id;
  }
  
  /***
   * readResolve to reset the idBank after the object has been deserialized. 
   * Statement is synchronized to idBank to ensure correct behaviour if 
   * read process is concurrent.
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