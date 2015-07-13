package it.samvise85.bookshelf.persist.clauses;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

public abstract class ProjectionClause {
	public static enum ProjectionType {
		INCLUSION,
		EXCLUSION,
		NO_PROJECTION;
	}
	
	private Set<String> fields = new HashSet<String>(0);
	protected ProjectionType type = ProjectionType.NO_PROJECTION;

	public ProjectionClause() {}
	protected ProjectionClause(ProjectionType type) {
		this.type = type;
	}

	public ProjectionClause(String... fields) {
		this.fields.addAll(Arrays.asList(fields));
	}

	protected ProjectionClause(ProjectionType type, String... fields) {
		this.fields.addAll(Arrays.asList(fields));
	}

	public Set<String> getFields() {
		return fields;
	}

	public boolean add(String e) {
		return fields.add(e);
	}

	public boolean addAll(Collection<? extends String> c) {
		return fields.addAll(c);
	}

	public boolean contains(Object o) {
		return fields.contains(o);
	}

	public boolean isEmpty() {
		return fields.isEmpty();
	}

	public int size() {
		return fields.size();
	}
	
	public Projection toProjection(Class<?> entity) {
		ProjectionList list = Projections.projectionList();
		
		switch(type) {
		case INCLUSION:
			for(String field : getFields())
				list.add(Projections.property(field));
			break;
		case EXCLUSION:
			for(Field field : entity.getFields())
				if(!fields.contains(field.getName()))
					list.add(Projections.property(field.getName()));
			break;
		case NO_PROJECTION:
		default:
			return null;
		}
		return list;
	}
}
