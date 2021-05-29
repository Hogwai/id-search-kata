package com.hogwai.idsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
	private static Map<String, Integer> hashmap;

	public static void main(String[] args) {
		Instant start = null;
		Instant finish = null;
		long timeElapsed;
		File directoryPath = null;
		Properties properties = null;

		try {
			properties = readPropertiesFile("file.properties");
		} catch (IOException e) {
			e.printStackTrace();
		}
		directoryPath = new File(properties.getProperty("path"));

		hashmap = new HashMap<>();
		File[] files = directoryPath.listFiles();
		Pattern pattern = Pattern.compile("(?<=id=).*?(?=&|$)");

		start = Instant.now();
		for (File file : files) {
			searchIdsInFile(file, pattern);
		}

		hashmap.entrySet().stream().sorted((k1, k2) -> k1.getValue().compareTo(k2.getValue()))
				.forEach(k -> System.out.println(k.getKey() + ": " + k.getValue()));
		// allResults.limit(7).forEach(k -> System.out.println(k.getKey() + ": " + k.getValue()));
		finish = Instant.now();
		timeElapsed = Duration.between(start, finish).toMillis();
		System.out.format("Runtime: %d ms ", timeElapsed);
	}

	/**
	 * Search for id values and put them into a map
	 * 
	 * @param file
	 * @param pattern
	 */
	private static void searchIdsInFile(File file, Pattern pattern) {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					String id = matcher.group();
					if (hashmap.containsKey(id)) {
						hashmap.put(id, hashmap.get(id) + 1);
					} else {
						hashmap.put(id, 1);
					}
				}
			}
		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}
	}

	/**
	 * Read properties file containing the path to the log files directory
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private static Properties readPropertiesFile(String fileName) throws IOException {
		Properties prop = null;
		try (FileInputStream fis = new FileInputStream(fileName)) {
			prop = new Properties();
			prop.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
}
