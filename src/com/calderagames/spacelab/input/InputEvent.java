package com.calderagames.spacelab.input;

public class InputEvent {

	/**
	 * Name of the input event
	 */
	public String name;

	/**
	 * True for pressed, false for released
	 */
	public boolean state;

	/**
	 * Constructor for Input Event
	 * 
	 * @param name
	 *            of the input
	 * @param state
	 *            true for pressed false for released
	 */
	public InputEvent(String name, boolean state) {
		this.name = name;
		this.state = state;
	}
}
