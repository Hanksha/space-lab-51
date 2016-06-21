package com.calderagames.spacelab.hud;

import java.util.ArrayList;

import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.graphics.Color;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.hud.Event.EventType;
import com.calderagames.spacelab.input.InputProcessor;


public class Menu {

	protected GameContent gc;

	//list of the menu items
	protected ArrayList<MenuItem> menuItems;
	//list of the sub menus
	protected ArrayList<Menu> subMenus;

	protected String name;

	//menu listener
	protected ArrayList<MenuListener> ml;

	protected int selectedIndex;
	protected int selectedSubMenuIndex = -1;

	protected Color selectedColor;
	protected Color unselectedColor;

	protected boolean showCursor;
	protected boolean alwaysShow;

	//set to true to avoid an input on a menu and its sub menu at same location
	private boolean skipMouse;

	public Menu(GameContent gc, String name) {
		this.gc = gc;
		this.name = name;
		menuItems = new ArrayList<MenuItem>();
		subMenus = new ArrayList<Menu>();
		ml = new ArrayList<MenuListener>();

		selectedColor = new Color(1f, 1f, 1f, 1f);
		unselectedColor = new Color(0.6f, 0.6f, 0.6f, 1f);
	}

	public void addMenuItem(MenuItem mi) {
		menuItems.add(mi);
	}

	public void addSubMenu(Menu m) {
		subMenus.add(m);
		m.setSelectedColor(selectedColor);
		m.setUnselectedColor(unselectedColor);
	}

	public void addMenuListener(MenuListener ml) {
		this.ml.add(ml);
	}

	public void handleKeyboardInput() {

		if(selectedSubMenuIndex != -1) {
			subMenus.get(selectedSubMenuIndex).handleKeyboardInput();
		}
		else {
			if(Keyboard.getEventKeyState()) {
				if(Keyboard.getEventKey() == Keyboard.KEY_UP) {
					selectedIndex--;
					if(selectedIndex < 0)
						selectedIndex = menuItems.size() - 1;

					Event e = new Event(EventType.VINCREMENT, menuItems.get(selectedIndex).getActionCommand(), -1);
					fireEvent(e);

					playSelectorMovedSound();
				}
				else if(Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
					selectedIndex++;
					if(selectedIndex > menuItems.size() - 1)
						selectedIndex = 0;

					Event e = new Event(EventType.VINCREMENT, menuItems.get(selectedIndex).getActionCommand(), 1);
					fireEvent(e);

					playSelectorMovedSound();
				}
				else if(Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
					Event e = new Event(EventType.HINCREMENT, menuItems.get(selectedIndex).getActionCommand(), -1);
					fireEvent(e);
				}
				else if(Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
					Event e = new Event(EventType.HINCREMENT, menuItems.get(selectedIndex).getActionCommand(), 1);
					fireEvent(e);
				}
				else if(Keyboard.getEventKey() == Keyboard.KEY_RETURN) {
					Event e = new Event(EventType.VALIDATION, menuItems.get(selectedIndex).getActionCommand(), 0);
					fireEvent(e);
					playItemSelectedSound();
				}
				else if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
					Event e = new Event(EventType.CANCELLATION, name, 0);
					fireEvent(e);
					playItemSelectedSound();
				}
			}
		}
	}

	public void handleMouseInput() {
		skipMouse = false;

		if(selectedSubMenuIndex != -1) {
			subMenus.get(selectedSubMenuIndex).handleMouseInput();
		}
		if(!skipMouse && (selectedSubMenuIndex == -1 || alwaysShow)) {
			for(int i = 0; i < menuItems.size(); i++) {
				MenuItem menuItem = menuItems.get(i);
				if(menuItem.getRectangle().contains(InputProcessor.getMouseX(), InputProcessor.getMouseY())) {
					if(selectedIndex != i) {
						selectedIndex = i;

						//playSelectorMovedSound();
					}
					if(!Mouse.getEventButtonState()) {
						if(Mouse.getEventButton() == 0) {
							Event e = new Event(EventType.VALIDATION, menuItem.getActionCommand(), 0);
							fireEvent(e);
							playItemSelectedSound();
						}
					}
				}

			}

			if(Mouse.getEventDWheel() < 0) {
				Event e = new Event(EventType.HINCREMENT, menuItems.get(selectedIndex).getActionCommand(), -1);
				fireEvent(e);
			}
			else if(Mouse.getEventDWheel() > 0) {
				Event e = new Event(EventType.HINCREMENT, menuItems.get(selectedIndex).getActionCommand(), 1);
				fireEvent(e);
			}
		}
	}

	public void handleControllerInput() {
		if(selectedSubMenuIndex != -1) {
			subMenus.get(selectedSubMenuIndex).handleControllerInput();
		}
		else {
			if(Controllers.isEventButton()) {
				if(Controllers.getEventButtonState()) {
					if(Controllers.getEventControlIndex() == InputProcessor.BUTTON_A) {
						Event e = new Event(EventType.VALIDATION, menuItems.get(selectedIndex).getActionCommand(), 0);
						fireEvent(e);
						playItemSelectedSound();
					}
					else if(Controllers.getEventControlIndex() == InputProcessor.BUTTON_START || Controllers.getEventControlIndex() == InputProcessor.BUTTON_B) {
						Event e = new Event(EventType.CANCELLATION, name, 0);
						fireEvent(e);
						playItemSelectedSound();
					}
				}
			}

			if(Controllers.isEventPovY()) {
				if(Controllers.getEventSource().getPovY() < 0) {
					selectedIndex--;
					if(selectedIndex < 0)
						selectedIndex = menuItems.size() - 1;

					Event e = new Event(EventType.VINCREMENT, menuItems.get(selectedIndex).getActionCommand(), -1);
					fireEvent(e);
					playSelectorMovedSound();
				}
				else if(Controllers.getEventSource().getPovY() > 0) {
					selectedIndex++;
					if(selectedIndex > menuItems.size() - 1)
						selectedIndex = 0;

					Event e = new Event(EventType.VINCREMENT, menuItems.get(selectedIndex).getActionCommand(), 1);
					fireEvent(e);
					playSelectorMovedSound();
				}
			}
			if(Controllers.isEventPovX()) {
				if(Controllers.getEventSource().getPovX() < 0) {
					Event e = new Event(EventType.HINCREMENT, menuItems.get(selectedIndex).getActionCommand(), -1);
					fireEvent(e);
				}
				else if(Controllers.getEventSource().getPovX() > 0) {
					Event e = new Event(EventType.HINCREMENT, menuItems.get(selectedIndex).getActionCommand(), 1);
					fireEvent(e);
				}
			}
		}
	}

	public void playSelectorMovedSound() {
		gc.getAS().playSoundEffect(0.8f, 0.4f, "none");
	}

	public void playItemSelectedSound() {
		gc.getAS().playSoundEffect(0.5f, 0.5f, "click");
	}

	public void update(double dt) {
		for(Menu m : subMenus)
			m.update(dt);
	}

	public void renderText(SpriteBatch sb) {
		if(selectedSubMenuIndex != -1) {
			subMenus.get(selectedSubMenuIndex).renderText(sb);
		}
		if(selectedSubMenuIndex == -1 || alwaysShow) {
			for(int i = 0; i < menuItems.size(); i++) {
				menuItems.get(i).render(sb, selectedIndex == i ? selectedColor : unselectedColor);
			}
		}
	}

	public void hideSubMenu() {
		if(selectedSubMenuIndex != -1)
			subMenus.get(selectedSubMenuIndex).hideSubMenu();

		selectedSubMenuIndex = -1;
		skipMouse = true;
	}

	public void showSubMenu(int index) {
		hideSubMenu();

		if(index >= 0 && index < subMenus.size())
			selectedSubMenuIndex = index;
	}

	private void fireEvent(Event event) {
		for(MenuListener l : ml) {
			l.actionPerformed(event);
		}
	}

	public void setShowCursor(boolean b) {
		showCursor = b;
	}

	public void setAlwaysShow(boolean b) {
		alwaysShow = b;
	}

	public void setSelectedColor(Color color) {
		selectedColor = color;
	}

	public void setUnselectedColor(Color color) {
		unselectedColor = color;
	}

	public Color getSelectedColor() {
		return selectedColor;
	}

	public Color getUnselectedColor() {
		return unselectedColor;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public int getSelectedSubMenuIndex() {
		return selectedSubMenuIndex;
	}

	public ArrayList<Menu> getSubMenus() {
		return subMenus;
	}
}
