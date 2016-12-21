package de.hapebe.cyhi.logical;

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

public class IntervalStats implements Jsonable {

	final TaskResultSeries attempts;
	final Map<IntervalType, TaskResultSeries> attemptsByType;

	final TaskResultSeries EMTPY = new TaskResultSeries();
	
	public IntervalStats() {
		attempts = new TaskResultSeries();
		attemptsByType = new HashMap<IntervalType, TaskResultSeries>();
	}
	
	public void clear() {
		attempts.clear();
		attemptsByType.clear();
	}
	
	public TaskResultSeries getAttempts() {
		return attempts;
	}

	public TaskResultSeries getAttemptsByType(IntervalType type) {
		TaskResultSeries trs = attemptsByType.get(type);
		if (trs == null) return EMTPY;
		return trs;
	}

	public void registerAttempt(TheoInterval i, boolean typeSuccess, boolean baseToneSuccess) {
		byte typeStatus = typeSuccess ? TaskResult.SUCCESS : TaskResult.FAILURE;
		byte baseToneStatus = baseToneSuccess ? TaskResult.SUCCESS : TaskResult.FAILURE;
		
		this.registerAttempt(new TaskResult(i, typeStatus, baseToneStatus));
	}

	private void registerAttempt(TaskResult tr) {
		LessonTask lt = tr.getLessonTask();
		
		if (lt instanceof TheoInterval) {
			getAttempts().add(tr); // mixed interval attempts
			
			TheoInterval intv = (TheoInterval)tr.getLessonTask();
			IntervalType type = intv.getType();
			TaskResultSeries attempts = attemptsByType.get(type);
			if (attempts == null) {
				attempts = new TaskResultSeries();
				attemptsByType.put(type, attempts);
			}
			attempts.add(tr); // interval attempts by type
		} else {
			System.err.println("Unexpected type of TaskResult for LessonTask: " + lt);
		}
	}
	
	public void add(IntervalStats other) {
		for (TaskResult tr : other.getAttempts()) {
			this.registerAttempt(tr);
		}
	}
	
	@Override
	public JsonObject toJSON() {
		JsonObjectBuilder b = Json.createObjectBuilder();

		b.add("type", getClass().getSimpleName());
		
		JsonArrayBuilder b2 = Json.createArrayBuilder();
		for (TaskResult tr : getAttempts()) {
			b2.add(tr.toJSON());
		}
		b.add("attempts", b2.build());
		
		return b.build();
	}

	@Override
	public void fromJSON(JsonObject o) {
		clear();
		
		String type = o.getString("type");
		if (!type.equals(getClass().getSimpleName())) {
			throw new IllegalArgumentException("Not a IntervalStats JSON object - type: " + type);
		}
		
        JsonArray jsonObjects = o.getJsonArray("attempts");
        for (JsonValue jv : jsonObjects) {
        	if (!(jv instanceof JsonObject)) {
        		// report & skip:
        		System.err.println("unexpected JSON data: " + jv.toString());
        		continue;
        	}
        	JsonObject jsonO = (JsonObject)jv;
        	
        	TaskResult trs = new TaskResult();
        	trs.fromJSON(jsonO);
        	
        	registerAttempt(trs);
        } // next object in JSON data
		
	}

	
}
