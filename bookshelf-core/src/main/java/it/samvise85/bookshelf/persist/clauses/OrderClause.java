package it.samvise85.bookshelf.persist.clauses;

public class OrderClause {
	private String field;
	private Order order;
	
	public OrderClause(String field, Order order) {
		super();
		this.field = field;
		this.order = order;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
	
}
