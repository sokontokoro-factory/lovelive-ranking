package net.sokontokoro_factory.lovelive.controller.errorhandling;

import net.sokontokoro_factory.lovelive.exception.InvalidArgumentException;
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import org.json.JSONObject;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidArgumentExceptionMapper implements ExceptionMapper<InvalidArgumentException> {

	private static final String MESSAGE = "リソースが存在しません";
	
    @Override
    public Response toResponse(InvalidArgumentException exception) {

		JSONObject error = new JSONObject();
		error.put("code", "-");
		error.put("messsage", MESSAGE);
		error.put("messsage_system", exception.getMessage());

		JSONObject response = new JSONObject();
		response.put("error", error);

    	return Response.status(Status.NOT_FOUND)
    			.entity(response.toString())
    			.build();
    }
}