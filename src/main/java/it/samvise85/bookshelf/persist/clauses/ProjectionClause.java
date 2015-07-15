package it.samvise85.bookshelf.persist.clauses;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

public class ProjectionClause {
	public static final ProjectionClause NO_PROJECTION = new NoProjectionClause();
	
	public static enum ProjectionType {
		INCLUSION,
		EXCLUSION,
		NO_PROJECTION;
	}
	
	private Set<String> fields = new HashSet<String>(0);
	protected ProjectionType type = ProjectionType.NO_PROJECTION;

	public static ProjectionClause createInclusionClause(String... fields) {
		return new ProjectionClause(ProjectionType.INCLUSION, fields);
	}
	public static ProjectionClause createExclusionClause(String... fields) {
		return new ProjectionClause(ProjectionType.EXCLUSION, fields);
	}
	public static ProjectionClause createNoProjectionClause(String... fields) {
		return NO_PROJECTION;
	}
	
	protected ProjectionClause(ProjectionType type) {
		this.type = type;
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
	
	public static <T> T returnNullOrValue(ProjectionClause projection, String fieldName, T fieldValue) {
		if(projection != null && 
				((projection.type==ProjectionType.EXCLUSION && projection.contains(fieldName)) ||
				((projection.type==ProjectionType.INCLUSION) && !projection.contains(fieldName)))) return null;
		return fieldValue;
	}
	
	public static class NoProjectionClause extends ProjectionClause {
		public static final ProjectionClause NO_PROJECTION = new NoProjectionClause();
		
		private NoProjectionClause() {
			super(ProjectionType.NO_PROJECTION);
		}

		@Override
		public Set<String> getFields() {
			return Collections.emptySet();
		}

		@Override
		public boolean add(String e) {
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends String> c) {
			return false;
		}

		@Override
		public boolean contains(Object o) {
			return false;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public int size() {
			return 0;
		}
	}

}
