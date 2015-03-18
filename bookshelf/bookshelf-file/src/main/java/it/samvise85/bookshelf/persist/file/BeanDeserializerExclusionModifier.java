package it.samvise85.bookshelf.persist.file;

import it.samvise85.bookshelf.persist.clauses.ExclusionClause;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

public class BeanDeserializerExclusionModifier extends BeanDeserializerModifier {

    private Class<?> type;
    private Set<String> ignorables;

    public BeanDeserializerExclusionModifier(Class<?> clazz,
    		ExclusionClause projection) {
		type = clazz;
		ignorables = projection.getFields();
	}

	@Override
    public BeanDeserializerBuilder updateBuilder(
            DeserializationConfig config, BeanDescription beanDesc,
            BeanDeserializerBuilder builder) {
        if(!type.equals(beanDesc.getBeanClass())) {
            return builder;
        }

        for(String ignorable : ignorables) {
            builder.addIgnorable(ignorable);                
        }

        return builder;
    }

    @Override
    public List<BeanPropertyDefinition> updateProperties(
            DeserializationConfig config, BeanDescription beanDesc,
            List<BeanPropertyDefinition> propDefs) {
        if(!type.equals(beanDesc.getBeanClass())) {
            return propDefs;
        }

        List<BeanPropertyDefinition> newPropDefs = new ArrayList<>();
        for(BeanPropertyDefinition propDef : propDefs) {
            if(!ignorables.contains(propDef.getName())) {
                newPropDefs.add(propDef);
            }
        }
        return newPropDefs;
    }
}