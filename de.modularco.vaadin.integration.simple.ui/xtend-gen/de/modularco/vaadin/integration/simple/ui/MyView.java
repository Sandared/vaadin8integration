package de.modularco.vaadin.integration.simple.ui;

import com.vaadin.navigator.View;

@SuppressWarnings("all")
public interface MyView extends View {
  public abstract String getName();
  
  public abstract String getPath();
}
