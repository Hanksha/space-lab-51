package com.calderagames.spacelab.hud;

public class Event {

	public enum EventType {
		VALIDATION, CANCELLATION, HINCREMENT, VINCREMENT
	}

	private String actionCommand;
	private EventType type;
	private int value;

	public Event(EventType type, String actionCommand, int value) {
		this.type = type;
		this.actionCommand = actionCommand;
		this.value = value;
	}

	public String getActionCommand() {
		return actionCommand;
	}

	public EventType getType() {
		return type;
	}

	public int getValue() {
		return value;
	}
}
