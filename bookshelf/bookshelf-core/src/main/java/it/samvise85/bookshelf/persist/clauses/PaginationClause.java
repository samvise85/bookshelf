package it.samvise85.bookshelf.persist.clauses;

public class PaginationClause {
	private int pageSize;
	private int page = 1;
	
	public PaginationClause() {}
	public PaginationClause(int pageSize, int page) {
		super();
		this.pageSize = pageSize;
		this.setPage(page);
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		if(page < 1) page = 1;
		this.page = page;
	}

}
