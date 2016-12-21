package de.hapebe.cyhi.logical;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import de.hapebe.cyhi.io.Jsonable;
import de.hapebe.cyhi.musical.ChordType;
import de.hapebe.cyhi.musical.IntervalType;
import de.hapebe.cyhi.musical.TheoChord;
import de.hapebe.cyhi.musical.TheoInterval;

public class StatsContainer implements Jsonable {
	
	IntervalStats intervalStats;
	ChordStats chordStats;
	
	public IntervalStats getIntervalStats() {
		return intervalStats;
	}

	public void setIntervalStats(IntervalStats intervalStats) {
		this.intervalStats = intervalStats;
	}

	public ChordStats getChordStats() {
		return chordStats;
	}

	public void setChordStats(ChordStats chordStats) {
		this.chordStats = chordStats;
	}


	/**
	 * add the contents of another StatsContainer to this one - e.g. the results of one lesson to the master / global stats.
	 * @param other a different instance of StatsContainer (should only be added once, and possibly cleared afterwards, for data purity's sake)
	 */
	public void add(StatsContainer other) {
		// intervals:
		if (this.getIntervalStats() != null) {
			if (other.getIntervalStats() != null) {
				this.getIntervalStats().add(other.getIntervalStats());
			}
		} else {
			if (other.getIntervalStats() != null) this.setIntervalStats(other.getIntervalStats());
		}
		
		// chords:
		if (this.getChordStats() != null) {
			if (other.getChordStats() != null) {
				this.getChordStats().add(other.getChordStats());
			}
		} else {
			if (other.getChordStats() != null) this.setChordStats(other.getChordStats());
		}
	}

	
	@Override
	public JsonObject toJSON() {
		JsonObjectBuilder b = Json.createObjectBuilder();

		b.add("type", getClass().getSimpleName());
		
		b.add(getIntervalStats().getClass().getSimpleName(), getIntervalStats().toJSON());
		b.add(getChordStats().getClass().getSimpleName(), getChordStats().toJSON());
		
		return b.build();
	}

	@Override
	public void fromJSON(JsonObject o) {
		setIntervalStats(null);
		setChordStats(null);
		
		String type = o.getString("type");
		if (!type.equals(getClass().getSimpleName())) {
			throw new IllegalArgumentException("Not a StatsContainer JSON object - type: " + type);
		}
		
		JsonObject intervals = o.getJsonObject(IntervalStats.class.getSimpleName());
		if (intervals != null) {
			intervalStats = new IntervalStats();
			intervalStats.fromJSON(intervals);
		}
		
		JsonObject chords = o.getJsonObject(ChordStats.class.getSimpleName());
		if (chords != null) {
			chordStats = new ChordStats();
			chordStats.fromJSON(chords);
		}
	}
	
	
}
