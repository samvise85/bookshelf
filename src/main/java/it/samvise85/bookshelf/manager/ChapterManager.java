package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.Chapter;
import it.samvise85.bookshelf.persist.PersistenceUnit;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;

public interface ChapterManager extends PersistenceUnit<Chapter> {

	Chapter getChapterByBookAndPosition(String book, Integer position, ProjectionClause projection);
}
