package game.sccl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ScriptCompiler {

	final String SCL_VERSION = "0.1";
	//HEADER
	short numSubroutines;
	ArrayList<ArrayList<String>> subs;
	ArrayList<Integer> subLengths;
	ArrayList<String> sub;
	ArrayList<Byte> output;
	
	ArrayList<String> subNames;
	ArrayList<Integer> subPositions;
	ArrayList<String> markerNames;
	ArrayList<Integer> markerPositions;
	
	
	
	
	public ScriptCompiler(String inputFile) {
		sub = new ArrayList<String>();
		subs = new ArrayList<ArrayList<String>>();
		getScriptFromFile(inputFile);
	}
	private void getSubAndMarkerNames(InputStream scriptStream) {		
		Scanner sc = new Scanner(scriptStream);
		subNames = new ArrayList<String>();
		subPositions = new ArrayList<Integer>();
		numSubroutines = 0;
		markerNames = new ArrayList<String>();
		markerPositions = new ArrayList<Integer>();
		int lineNum = 0;
		int instructionNum = 0;
		while(sc.hasNextLine()) {
			Scanner subsGet = new Scanner(sc.nextLine());
			subsGet.useDelimiter(Pattern.compile("[\\p{javaWhitespace}{(,)}]+"));
			lineNum++;
			try {
				if(subsGet.hasNext()) {
					String val = subsGet.next();
					if(val.equalsIgnoreCase("sub")) {
						String subName = null;
						if(subsGet.hasNext()) subName = subsGet.next();
						if(subName == null) throw new SCCLexception("Invalid subroutine declaration syntax at line " +lineNum);
						subNames.add(subName);
						subPositions.add(instructionNum);
						numSubroutines++;
					}
					else {
						if(val.matches("[\\w]+:")) {
							val = val.substring(0, val.length() - 1);
							if(markerNames.contains(val)) throw new SCCLexception("Multiple markers share the same name " +val);
							markerNames.add(val);
							markerPositions.add(instructionNum);
						}else instructionNum++;
						while(subsGet.hasNext()) {
							subsGet.next();
							instructionNum++;
						}
						
					}
				}	
			}catch(Exception e) {
				e.printStackTrace();
			}
			subsGet.close();
		}
		sc.close();
		
	}

	public void getScriptFromFile(String fileName) {
		try {
			InputStream scriptStream = null;
			try {			
				scriptStream = this.getClass().getResourceAsStream(fileName);
				subs.clear();
				getSubAndMarkerNames(scriptStream);

				for(String t : subNames) {
					ArrayList<String> al = getSubFromFile(fileName, t);
					//System.out.println(t);
					//System.out.println(al.toString());
					subs.add(al);
				}			
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				scriptStream.close();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	protected ArrayList<String> getSubFromFile(String fileName, String subName){
		ArrayList<String> toReturn = new ArrayList<String>();
		InputStream scriptStream = this.getClass().getResourceAsStream(fileName);
		
		boolean parsing = false;
		Scanner s = new Scanner(scriptStream);
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
		
		return toReturn;
	}

	//private void WriteSubToFile
	public static void main(String[] args) {
		if(args == null ||args.length == 0) {
			System.out.println("Usage: java ScriptCompiler input.sccs");
		}else {
			ScriptCompiler SCLC = new ScriptCompiler(args[0]);
		}

	}

}
