import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class RobotLogCleaner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String baseDir = "C:\\Users\\Evan\\Documents\\College Stuff\\RI Summer Stuff\\Tech Report\\NSH 2 Aug9\\Run6\\";
		String robotLog = baseDir + "robotStatus2011_Aug09_1637.log";
		String output = baseDir + "robotStatus-cleaned.txt";
		
		Scanner robotInScanner = null;
		FileWriter out = null;
		
		try {
			robotInScanner = new Scanner(new FileReader(robotLog));
			out = new FileWriter(new File(output));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(robotInScanner.hasNextLine()) {
			String l = robotInScanner.nextLine();
			if(robotInScanner.hasNextLine())
			l = robotInScanner.nextLine();
			else
				break;
			l = robotInScanner.nextLine();
			l = robotInScanner.nextLine();
			try {
				out.write(l + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
