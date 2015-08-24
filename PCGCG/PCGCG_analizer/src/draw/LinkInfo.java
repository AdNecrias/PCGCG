package draw;

import java.awt.Component;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class LinkInfo {
	
	public JPopupMenu menu = new JPopupMenu();
	
	public String text = "";
	public int value = 0;
	
	public LinkInfo() {
		this.menu = new JPopupMenu();
		this.text = "";
		this.value = 0;
	}
	
	public JMenuItem add(JMenuItem o) {
		return menu.add(o);
	}

	public void show(Component component, int x, int y, String info) {
		String text = info;
		if(info == null) {
			text = "Info: None";
		}
		((JMenuItem) menu.getComponent(0)).setText(text);
		menu.show(component, x, y);
	}
	
}

