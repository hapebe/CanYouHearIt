package de.hapebe.cyhi.logical;

public class TaskResult {
	public final static byte FAILURE = 0;
	public final static byte SUCCESS = 1;
	public final static byte DID_NOT_ATTEMPT = -1;
	public final static byte NOT_APPLICABLE = -2;
	
	LessonTask lt;
	long timestamp;
	byte typeSuccess = NOT_APPLICABLE;
	byte baseToneSuccess = NOT_APPLICABLE;
	byte inversionSuccess = NOT_APPLICABLE;
	
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
	
	
	
}
