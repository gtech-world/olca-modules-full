package org.openlca.cloud.error;

import javax.ws.rs.core.Response.Status;

import org.openlca.cloud.util.Strings;

public class UserNotFoundException extends ServerException {

	private static final long serialVersionUID = 3597855854783144681L;

	public UserNotFoundException(long id) {
		super(Status.NOT_FOUND, Strings.concat("No user with id '", id,
				"' found"));
	}

	public UserNotFoundException(String name) {
		super(Status.NOT_FOUND, Strings.concat("No user '", name, "' found"));
	}

}
