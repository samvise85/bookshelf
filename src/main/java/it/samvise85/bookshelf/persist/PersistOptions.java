package it.samvise85.bookshelf.persist;

import it.samvise85.bookshelf.persist.clauses.OrderClause;
import it.samvise85.bookshelf.persist.clauses.PaginationClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.clauses.SelectionClause;

import java.util.List;

public class PersistOptions {
	private ProjectionClause projection;
	private List<SelectionClause> selection;
	private List<OrderClause> order;
	private PaginationClause pagination;
	
	public PersistOptions() {}

	public PersistOptions(ProjectionClause projection,
			List<SelectionClause> selection) {
		this.projection = projection;
		this.selection = selection;
	}
	
	public PersistOptions(ProjectionClause projection,
			List<SelectionClause> selection, List<OrderClause> order) {
		this(projection, selection);
		this.order = order;
	}
	
	public PersistOptions(ProjectionClause projection,
			List<SelectionClause> selection, List<OrderClause> order,
			PaginationClause pagination) {
		this(projection, selection, order);
		this.pagination = pagination;
	}
	
	public ProjectionClause getProjection() {
		return projection;
	}
	public void setProjection(ProjectionClause projection) {
		this.projection = projection;
	}
	public List<SelectionClause> getSelection() {
		return selection;
	}
	public void setSelection(List<SelectionClause> selection) {
		this.selection = selection;
	}
	public List<OrderClause> getOrder() {
		return order;
	}
	public void setOrder(List<OrderClause> order) {
		this.order = order;
	}
	public PaginationClause getPagination() {
		return pagination;
	}
	public void setPagination(PaginationClause pagination) {
		this.pagination = pagination;
	}
	
}
