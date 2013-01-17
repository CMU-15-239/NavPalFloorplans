import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


public class MoniSplitter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(new FileReader("C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\GSM SCANS\\GSM-Modem_NSH_COMBINED.txt"));
		FileWriter out = new FileWriter("monionly.txt");
		TreeSet<String> set = new TreeSet<String>();
		TreeSet<String> group = new TreeSet<String>();
		TreeMap<String, ArrayList<String>> map = new TreeMap<String, ArrayList<String>>();
		while(in.hasNextLine()) {
			String line = in.nextLine();
			if(line.startsWith("#MONI") && !line.contains("ARFCN")) {
				String[] parts = line.split("[ ]+");
				out.write(line + "\n");
				String chan = parts[5];
				String pow = parts[6];				
				set.add(chan);
				map.put(chan, new ArrayList<String>());
			}
		}
		
		in = new Scanner(new FileReader("C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\GSM SCANS\\GSM-Modem_NSH_COMBINED.txt"));
		boolean insertNew = false;
		int counter = 0;
		while(in.hasNextLine()) {
			String line = in.nextLine();
			if(line.startsWith("#MONI") && !line.contains("ARFCN")) {
				insertNew = true;
				String[] parts = line.split("[ ]+");
				String chan = parts[5];
				String pow = parts[6];
				group.remove(chan);
				ArrayList<String> powList = map.get(chan);
				powList.add(pow);
				map.put(chan, powList);
			}
			else {				
				if(insertNew) {
					insertNew = false;
					Iterator<String> i = group.iterator();
					while(i.hasNext()) {
						String chan = i.next();
						ArrayList<String> powList = map.get(chan);
						powList.add("-120");
						map.put(chan, powList);
					}
					counter++;
				}
				else {
					group = (TreeSet<String>) set.clone();
				}
			}
		}
		
		out.close();
		
		FileWriter out2 = new FileWriter("monionly-chart.txt");
		Set<String> channels = map.keySet();
		Iterator<String> i = channels.iterator();
		String anychan = "";
		while(i.hasNext()) {
			anychan = i.next();
			out2.write("C" + anychan + "\t");
		}
		out2.write("\n");
		int length = map.get(anychan).size();
		for(int q = 0; q < length; q++) {
			Iterator<String> y = channels.iterator();
			while(y.hasNext()) {
				out2.write(map.get(y.next()).get(q) + "\t");
			}
			out2.write("\n");
		}
		out2.close();
		
	}

}
