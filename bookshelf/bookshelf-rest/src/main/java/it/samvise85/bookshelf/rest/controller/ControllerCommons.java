package it.samvise85.bookshelf.rest.controller;


public interface ControllerCommons {

	public interface Pagination {
		public static final Integer DEFAULT_PAGE_SIZE = 10;
		public static final Integer SMALL_PAGE_SIZE = 5;
		public static final Integer BIG_PAGE_SIZE = 20;
		public static final Integer MAX_PAGE_SIZE = Integer.MAX_VALUE;
	}
}
