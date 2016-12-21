package de.hapebe.cyhi.io;

import javax.json.JsonObject;

public interface Jsonable {
	public JsonObject toJSON();
	
	public void fromJSON(JsonObject o);
}
