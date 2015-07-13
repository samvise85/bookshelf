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

	public static OrderClause ASC(String field) {
		return new OrderClause(field, Order.ASC);
	}
	
	public static OrderClause DESC(String field) {
		return new OrderClause(field, Order.DESC);
	}
	
	public org.hibernate.criterion.Order toOrder() {
		switch (getOrder()) {
		case ASC:
			return org.hibernate.criterion.Order.asc(getField());
		case DESC:
			return org.hibernate.criterion.Order.asc(getField());
			default:
			return null;
		}
	}
}
