package de.hapebe.cyhi.logical;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import de.hapebe.cyhi.io.Jsonable;
import de.hapebe.cyhi.musical.TheoChord;
import de.hapebe.cyhi.musical.TheoInterval;

public class TaskResult implements Jsonable {
	public final static byte FAILURE = 0;
	public final static byte SUCCESS = 1;
	public final static byte DID_NOT_ATTEMPT = -1;
	public final static byte NOT_APPLICABLE = -99;
	
	public final static Map<Byte, String> statusDict = new HashMap<Byte, String>();
	static {
		statusDict.put(FAILURE, "FAILURE");
		statusDict.put(SUCCESS, "SUCCESS");
		statusDict.put(DID_NOT_ATTEMPT, "DID_NOT_ATTEMPT");
		statusDict.put(NOT_APPLICABLE, "NOT_APPLICABLE");
	}
	
	public final static Map<String, Byte> statusReverseDict = new HashMap<String, Byte>();
	static {
		for (byte status : statusDict.keySet()) {
			String s = statusDict.get(status);
			statusReverseDict.put(s, status);
		}
	}
	
	
	LessonTask lt;
	long timestamp;
	byte typeSuccess = NOT_APPLICABLE;
	byte baseToneSuccess = NOT_APPLICABLE;
	byte inversionSuccess = NOT_APPLICABLE;
	
	
	/**
	 * this constructor is used when re-creating TaksResults from JSON data
	 */
	protected TaskResult() {
		super();
	}
	
	public TaskResult(LessonTask lt) {
		super();
		this.lt = lt;
	}

	public TaskResult(LessonTask lt, byte typeSuccess, byte baseToneSuccess) {
		this(lt);
		this.typeSuccess = typeSuccess;
		this.baseToneSuccess = baseToneSuccess;
	}
	
	public boolean isTypeAttempt() {
		return isTypeSuccess() || isTypeFailure();
	}

	public boolean isTypeSuccess() {
		return (typeSuccess == SUCCESS);
	}
	
	public boolean isTypeFailure() {
		return (typeSuccess == FAILURE);
	}
	
	public boolean isBaseToneAttempt() {
		return isBaseToneSuccess() || isBaseToneFailure();
	}

	public boolean isBaseToneSuccess() {
		return (baseToneSuccess == SUCCESS);
	}
	
	public boolean isBaseToneFailure() {
		return (baseToneSuccess == FAILURE);
	}
	
	public LessonTask getLessonTask() {
		return lt;
	}

	@Override
	public JsonObject toJSON() {
		JsonObjectBuilder b = Json.createObjectBuilder();

		b.add("time", timestamp);
		b.add("task", getLessonTask().toShortCode());
		b.add("typeResult", statusDict.get(typeSuccess));
		b.add("baseNoteResult", statusDict.get(baseToneSuccess));
		b.add("inversionResult", statusDict.get(inversionSuccess));
		
		return b.build();
	}

	@Override
	public void fromJSON(JsonObject o) {
		timestamp = o.getJsonNumber("time").longValue();
		typeSuccess = statusReverseDict.get(o.getString("typeResult"));
		baseToneSuccess = statusReverseDict.get(o.getString("baseNoteResult"));
		inversionSuccess = statusReverseDict.get(o.getString("inversionResult"));
		
		String task = o.getString("task");
		lt = null;
		if (task.startsWith(TheoInterval.class.getSimpleName())) {
			lt = new TheoInterval();
		} else if (task.startsWith(TheoChord.class.getSimpleName())) {
			lt = new TheoChord();
		} else {
			throw new IllegalArgumentException("Unexpected task shord code: " + task);
		}
		
		lt.fromShortCode(task);
	}
	
}
