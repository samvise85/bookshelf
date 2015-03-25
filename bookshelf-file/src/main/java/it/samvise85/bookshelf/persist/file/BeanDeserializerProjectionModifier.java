package it.samvise85.bookshelf.persist.file;

import it.samvise85.bookshelf.persist.clauses.ProjectionClause;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

public class BeanDeserializerProjectionModifier extends BeanDeserializerModifier {

	private Class<?> type;
	private Set<String> maintainable;

	public BeanDeserializerProjectionModifier(Class<?> clazz, ProjectionClause projection) {
		type = clazz;
		maintainable = projection.getFields();
	}

	@Override
	public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc, BeanDeserializerBuilder builder) {
		if(!type.equals(beanDesc.getBeanClass())) {
			return builder;
		}

		Class<?> currClass = type;
		do {
			for(Field f : currClass.getDeclaredFields())
				if(!maintainable.contains(f.getName()))
					builder.addIgnorable(f.getName());
			currClass = currClass.getSuperclass();
		} while(currClass != null && !currClass.equals(Object.class));
			
		return builder;
	}

	@Override
	public List<BeanPropertyDefinition> updateProperties(DeserializationConfig config, BeanDescription beanDesc,
			List<BeanPropertyDefinition> propDefs) {
		if(!type.equals(beanDesc.getBeanClass())) {
			return propDefs;
		}

		List<BeanPropertyDefinition> newPropDefs = new ArrayList<>();
		for(BeanPropertyDefinition propDef : propDefs) {
			if(maintainable.contains(propDef.getName())) {
				newPropDefs.add(propDef);
			}
		}
		return newPropDefs;
	}
}