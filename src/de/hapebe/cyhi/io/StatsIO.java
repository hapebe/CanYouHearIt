package de.hapebe.cyhi.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.hapebe.cyhi.logical.ChordStats;
import de.hapebe.cyhi.logical.IntervalStats;
import de.hapebe.cyhi.logical.StatsContainer;

public class StatsIO {

	private static StatsIO instance;
	
	private StatsIO() {
		// TODO Auto-generated constructor stub
	}
	
	public static StatsIO getInstance() {
		if (instance == null) instance = new StatsIO();
		return instance;
	}

	public void saveStats(StatsContainer stats, String filename) {
		String workingDir = System.getProperty("user.dir");
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(workingDir  + "/" + filename);
			writer.write(prettyPrint(stats.toJSON()));
			writer.close();
		} catch (IOException e) {
			System.err.println("Couldn't write stats to " + filename + ":" + e.getMessage());
			e.printStackTrace();
		}
	}

	public StatsContainer loadStats(String filename) {
		StatsContainer retval = new StatsContainer();
		
		String workingDir = System.getProperty("user.dir");

		JsonObject o = null;
		try {
			o = (JsonObject)jsonFromFile(workingDir + "/" + filename);
		} catch (FileNotFoundException e) {
			System.err.println("(no stats exist for in " + filename + ".)");
		}
		
		if (o != null) {
			retval.fromJSON(o);
		} else {
			// we'll just create a fresh one:
			retval.setIntervalStats(new IntervalStats());
			retval.setChordStats(new ChordStats());
		}

		return retval;
	}

	/**
	 * pretty-prints a JsonP object
	 * 
	 * @param o JsonP object
	 * @return pretty JSON
	 */
	public static String prettyPrint(javax.json.JsonObject o) {
		com.google.gson.JsonParser gParser = new com.google.gson.JsonParser();
		com.google.gson.JsonObject json = gParser.parse(o.toString()).getAsJsonObject();

		Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = prettyGson.toJson(json);

		return prettyJson;
	}

	/**
	 * @param filename
	 * @return JsonStructure in the file
	 * @throws FileNotFoundException
	 */
	public static JsonStructure jsonFromFile(String filename) throws FileNotFoundException {
		JsonReader reader = Json.createReader(new FileReader(filename));
		JsonStructure jsonst = reader.read();
		return jsonst;
	}

	
}
