package it.samvise85.bookshelf.persist.file;

import it.samvise85.bookshelf.model.Identifiable;
import it.samvise85.bookshelf.model.user.User;
import it.samvise85.bookshelf.persist.clauses.ExclusionClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.clauses.SimpleProjectionClause;
import it.samvise85.bookshelf.persist.exception.JSONException;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;

public class FileMarshaller {
	private static User u;
	
	static {
		u = new User();
		u.setId("userId");
		u.setUsername("username");
	}
	
	public static <T extends Identifiable> String marshall(T object) {
		try {
			return new ObjectMapper().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new JSONException(e.getMessage(), e);
		}
	}

	public static <T extends Identifiable> T unmarshall(Class<T> clazz, String string, ProjectionClause projection) {
		if(projection != null) {
			if(projection instanceof SimpleProjectionClause)
				return unmarshall(clazz, string, (SimpleProjectionClause)projection);
			else if(projection instanceof ExclusionClause)
				return unmarshall(clazz, string, (ExclusionClause)projection);
		}
		return unmarshall(clazz, string);
	}
	
	public static <T extends Identifiable> T unmarshall(Class<T> clazz, String string, SimpleProjectionClause projection) {
		BeanDeserializerModifier modifier = null;
		if(projection != null)
			modifier = new BeanDeserializerProjectionModifier(clazz, projection);
		return unmarshall(clazz, string, modifier);
	}

	public static <T extends Identifiable> T unmarshall(Class<T> clazz, String string, ExclusionClause projection) {
		BeanDeserializerModifier modifier = null;
		if(projection != null) {
			modifier = new BeanDeserializerExclusionModifier(clazz, projection);
		}
		return unmarshall(clazz, string, modifier);
	}
	
	public static <T extends Identifiable> T unmarshall(Class<T> clazz, String string) {
		return unmarshall(clazz, string, (BeanDeserializerModifier)null);
	}

	private static <T extends Identifiable> T unmarshall(Class<T> clazz, String string, BeanDeserializerModifier modifier) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			
			if(modifier != null) { 
				DeserializerFactory dFactory = BeanDeserializerFactory.instance.withDeserializerModifier(modifier);		
				mapper = new ObjectMapper(null, null, new DefaultDeserializationContext.Impl(dFactory));
			}
			return mapper.readValue(string, clazz);
		} catch (JsonParseException e) {
			throw new JSONException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new JSONException(e.getMessage(), e);
		} catch (IOException e) {
			throw new JSONException(e.getMessage(), e);
		}
	}
}