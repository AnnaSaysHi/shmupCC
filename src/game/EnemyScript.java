package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class EnemyScript {

	public HashMap<String, ArrayList<String>> subs;
	public ArrayList<String> sub;

	public EnemyScript(ArrayList<String> al) {
		sub = al;		
	}
	
	public EnemyScript() {
		sub = new ArrayList<String>();
	}
	public EnemyScript(String filename) {
		sub = new ArrayList<String>();
		subs = new HashMap<String, ArrayList<String>>();
		getScriptFromFile(filename);
	}
	
	/*public String getValueAtPos(int position) {
		return sub.get(position);
	}*/
	public String getValueAtPos(String sub, int position) {
		if(subs.containsKey(sub)) {
			return subs.get(sub).get(position);
		}
		else return "0";
	}
	public int getScriptLength() {
		return sub.size();
	}
	public int getSubLength(String sub) {
		return subs.get(sub).size();
	}
	
	public void getScriptFromFile(String fileName, String subName) {
		//TODO
	}
	protected ArrayList<String> getSubFromFile(String fileName, String subName){
		ArrayList<String> toReturn = new ArrayList<String>();
		File f = new File(fileName);
		try {
			boolean parsing = false;
			Scanner s = new Scanner(f);
			Scanner lineGet;
			s.nextLine();
			while(s.hasNextLine()) {
				if(parsing) {
					String t = s.next();
					if(t.equals("ret")) {
						toReturn.add("10");
						break;
					}else toReturn.add(t);

				} else {
					lineGet = new Scanner(s.nextLine());
					if(lineGet.hasNext()) {
						if(lineGet.next().equals(subName)) {
							parsing = true;
							while(lineGet.hasNext()) {
								String u = lineGet.next();
								if(u.equals("ret")) {
									toReturn.add("10");
									parsing = false;
									break;
								}else toReturn.add(u);
							}
						}
						
					}
					lineGet.close();
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toReturn;
	}
	public void getScriptFromFile(String fileName) {
		File f = new File(fileName);
		try {
			Scanner s = new Scanner(f);
			ArrayList<String> subNames = new ArrayList<String>();
			Scanner subsGet = new Scanner(s.nextLine());
			while(subsGet.hasNext()){
				subNames.add(subsGet.next());
			}
			subsGet.close();
			s.close();
			for(String t : subNames) {
				ArrayList<String> al = getSubFromFile(fileName, t);
				//System.out.println(t);
				//System.out.println(al.toString());
				subs.put(t, al);
			}
			
			s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
