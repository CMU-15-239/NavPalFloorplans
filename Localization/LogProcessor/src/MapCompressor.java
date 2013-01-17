import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.TreeSet;


public class MapCompressor {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader mapReader = new BufferedReader(new FileReader("C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\Tech Report\\Evan NSH2 6.13\\map.map"));
		String sizeLine = mapReader.readLine();
		Scanner sizeScanner = new Scanner(sizeLine);
		int mapX = sizeScanner.nextInt();
		int mapY = sizeScanner.nextInt();
		sizeScanner.close();
		
		String line = mapReader.readLine();
		TreeSet<Integer> diffs = new TreeSet<Integer>();
		TreeSet<Integer> lefts = new TreeSet<Integer>();
		TreeSet<Integer> rights = new TreeSet<Integer>();
		int zcount = 0;
		for (int i = 0; i<mapY; i++)
		{						
			int left = Math.min(line.indexOf("#"), line.indexOf(" "));
			int right = Math.max(line.lastIndexOf("#"), line.lastIndexOf(" "));
			int diff = right - left;
			System.out.println(diff);
			if(diff != 0) {
				diffs.add(diff);
				lefts.add(left);
				rights.add(right);
			}			
			else
				zcount++;
			line = mapReader.readLine();
		}
		System.out.println("likely s:" + diffs.first() + "likely l:" + diffs.last());
		System.out.println("likely h:" + (mapY - zcount));
		
		mapReader = new BufferedReader(new FileReader("C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\Tech Report\\Evan NSH2 6.13\\map.map"));
		BufferedWriter compMap = new BufferedWriter(new FileWriter("C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\Tech Report\\Evan NSH2 6.13\\map-compressed.map"));
		mapReader.readLine(); //gets size line		
		compMap.write((rights.last() - lefts.higher(0))+ " " + (mapY-zcount)  + "\n");
		for(int i = 0; i < mapY; i++) {
			line = mapReader.readLine();
			int left = Math.min(line.indexOf("#"), line.indexOf(" "));
			int right = Math.max(line.lastIndexOf("#"), line.lastIndexOf(" "));
			int diff = right - left;
			System.out.println(diff);
			if(diff != 0) {
				String newLine = line.substring(lefts.higher(0), rights.last() + 1);
				compMap.write(newLine + "\n");
			}			
			
		}
		compMap.close();

	}

}
