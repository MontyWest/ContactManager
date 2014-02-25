package impl;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class DomainObject {
  private static AtomicInteger idBank = new AtomicInteger();
  private final int id;
  
  public DomainObject() {
    this.id = idBank.incrementAndGet();
  }
  
  public int getId() {
    return id;
  }
}