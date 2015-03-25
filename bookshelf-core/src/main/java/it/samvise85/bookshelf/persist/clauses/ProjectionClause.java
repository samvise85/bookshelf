package it.samvise85.bookshelf.persist.clauses;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ProjectionClause {
	private Set<String> fields = new HashSet<String>(0);

	public ProjectionClause() {}

	public ProjectionClause(String... fields) {
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
	
}
