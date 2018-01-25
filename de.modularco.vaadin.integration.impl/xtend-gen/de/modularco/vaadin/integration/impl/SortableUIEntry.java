package de.modularco.vaadin.integration.impl;

import com.vaadin.ui.UI;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@Accessors
@SuppressWarnings("all")
public class SortableUIEntry {
  private int key;
  
  private UI ui;
  
  @Pure
  public int getKey() {
    return this.key;
  }
  
  public void setKey(final int key) {
    this.key = key;
  }
  
  @Pure
  public UI getUi() {
    return this.ui;
  }
  
  public void setUi(final UI ui) {
    this.ui = ui;
  }
}
