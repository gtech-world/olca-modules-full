package org.openlca.jsonld;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.openlca.core.model.ModelType;

/**
 * Reads JSON objects and linked binary files from some data source.
 */
public interface JsonStoreReader {

	/**
	 * Get the IDs of all data sets of the given type from the underlying data
	 * store.
	 */
	List<String> getRefIds(ModelType type);

	/**
	 * Get the JSON object of the data set of the given type and ID.
	 */
	default JsonObject get(ModelType type, String refId) {
		var path = ModelPath.jsonOf(type, refId);
		var json = getJson(path);
		return json != null && json.isJsonObject()
			? json.getAsJsonObject()
			: null;
	}

	/**
	 * Parse the content of the file that is stored under the given path as JSON.
	 */
	default JsonElement getJson(String path) {
		var bytes = getBytes(path);
		if (bytes == null)
			return null;
		var json = new String(bytes, StandardCharsets.UTF_8);
		return new Gson().fromJson(json, JsonElement.class);
	}

	/**
	 * Get the raw bytes of the JSON or binary file that is stored under the
	 * given path.
	 */
	byte[] getBytes(String path);

}
