package fileOp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MFileManager {

	final static String FILE_NAME = "C:\\Temp\\input.txt";
	final static String OUTPUT_FILE_NAME = "C:\\Temp\\output.txt";
	final static Charset ENCODING = StandardCharsets.UTF_8;

	public static void main(String... args) throws IOException {
		MFileManager text = new MFileManager();

		// treat as a small file
		List<String> lines = text.readSmallTextFile(FILE_NAME);
		log(lines);
		lines.add("This is a line added in code.");
		text.writeSmallTextFile(lines, FILE_NAME);

		// treat as a large file - use some buffering
		text.readLargerTextFile(FILE_NAME);
		lines = Arrays.asList("Down to the Waterline", "Water of Love");
		text.writeLargerTextFile(OUTPUT_FILE_NAME, lines);
	}

	// For smaller files

	/**  Note: the javadoc of Files.readAllLines says it's intended for small files.
	 * But its implementation uses buffering, so it's likely good even for fairly large files.
	 */
	List<String> readSmallTextFile(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		return Files.readAllLines(path, ENCODING);
	}

	void writeSmallTextFile(List<String> lines, String fileName) throws IOException {
		Path path = Paths.get(fileName);
		Files.write(path, lines, ENCODING);
	}

	// For larger files

	void readLargerTextFile(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		try (Scanner scanner = new Scanner(path, ENCODING.name())) {
			while (scanner.hasNextLine()) {
				// process each line in some way
				log(scanner.nextLine());
			}
		}
	}

	void readLargerTextFileAlternate(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		try (BufferedReader reader = Files.newBufferedReader(path, ENCODING)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				// process each line in some way
				log(line);
			}
		}
	}

	void writeLargerTextFile(String fileName, List<String> lines) throws IOException {
		Path path = Paths.get(fileName);
		try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)) {
			for (String line : lines) {
				writer.write(line);
				writer.newLine();
			}
		}
	}

	private static void log(Object msg) {
		System.out.println(String.valueOf(msg));
	}

}
