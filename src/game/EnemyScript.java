package game;

import java.util.ArrayList;

public class EnemyScript {

	public ArrayList<String> sub;

	public EnemyScript(ArrayList<String> al) {
		sub = al;		
	}
	
	public EnemyScript() {
		sub = new ArrayList<String>();
	}
	
	public String getValueAtPos(int position) {
		return sub.get(position);
	}
	
	public void getScriptFromFile(String fileName, String subName) {
		//TODO
	}

}
