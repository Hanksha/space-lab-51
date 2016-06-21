package com.calderagames.spacelab.entities;

public class GroupEvent {

	public enum GroupEventType {STOP, MOVE, LEADER_CHANGE, NEW_MEMBER};
	public DynamicEntity source;
	public GroupEventType type;
	public int targetRow, targetCol;
	
	public GroupEvent(DynamicEntity source, GroupEventType type) {
		this(source, type, -1, -1);
	}
	
	public GroupEvent(DynamicEntity source, GroupEventType type, int targetRow, int targetCol) {
		this.source = source;
		this.type = type;
		this.targetRow = targetRow;
		this.targetCol = targetCol;
	}
}
