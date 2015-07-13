package it.samvise85.bookshelf.persist.clauses;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class NoProjectionClause extends ProjectionClause {
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
