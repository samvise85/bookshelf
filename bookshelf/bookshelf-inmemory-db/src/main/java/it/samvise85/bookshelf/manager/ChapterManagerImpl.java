package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.book.Chapter;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.NoProjectionClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.clauses.SimpleProjectionClause;
import it.samvise85.bookshelf.persist.inmemory.ChapterRepository;
import it.samvise85.bookshelf.persist.inmemory.InMemoryPersistenceUnit;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ChapterManagerImpl extends InMemoryPersistenceUnit<Chapter> implements ChapterManager {
	private static final SimpleProjectionClause BASIC_PROJECTION = new SimpleProjectionClause("id", "position", "book");

	private static final Logger log = Logger.getLogger(ChapterManagerImpl.class);
	
	@Autowired
	protected ChapterRepository repository;

	public ChapterManagerImpl() {
		super(Chapter.class);
	}

	@Override
	public List<Chapter> getList(PersistOptions options) {
		if(options != null) {
			String book = null;
			Pageable pagination = null;
			if(options.getSelection() != null && options.getSelection().size() == 1 &&
					options.getSelection().get(0).getField().equals("book"))
				book = (String) options.getSelection().get(0).getValue();
			if(options.getPagination() != null)
				pagination = createPageable(options.getPagination());
			if(book != null)
				return findChapters(book, pagination, options.getProjection());
		}
		return super.getList(options);
	}

	@Override
	public Chapter getChapterByBookAndPosition(String book, Integer position,
			ProjectionClause projection) {
		return repository.findFirstByBookAndPosition(book, position).setProjection(projection);
	}

	private List<Chapter> findChapters(String book, Pageable pagination, ProjectionClause projection) {
		if(pagination == null)
			return convertToList(repository.findByBookOrderByPositionAsc(book), projection);
		return convertToList(repository.findByBookOrderByPositionAsc(book, pagination), projection);
	}
	
	private Chapter findLastChapter(String book, ProjectionClause projection) {
		return repository.findFirstByBookOrderByPositionDesc(book);
	}

	@Override
	public Chapter update(Chapter updates) {
		return update(updates, true);
	}
	
	private Chapter update(Chapter updates, boolean updateOtherPositions) {
		Chapter chapterToUpdate = get(updates.getId(), NoProjectionClause.NO_PROJECTION);

		if(StringUtils.isNotEmpty(updates.getNumber()))
			chapterToUpdate.setNumber(updates.getNumber());
		if(updates.getCreation() != null)
			chapterToUpdate.setCreation(updates.getCreation());
		if(StringUtils.isNotEmpty(updates.getSection()))
			chapterToUpdate.setSection(updates.getSection());
		if(StringUtils.isNotEmpty(updates.getStream()))
			chapterToUpdate.setStream(updates.getStream());
		if(StringUtils.isNotEmpty(updates.getSynopsis()))
			chapterToUpdate.setSynopsis(updates.getSynopsis());
		if(StringUtils.isNotEmpty(updates.getTitle()))
			chapterToUpdate.setTitle(updates.getTitle());
		if(StringUtils.isNotEmpty(updates.getText()))
			chapterToUpdate.setText(updates.getText());
		
		if(updates.getPosition() == null || updates.getPosition() == 0)
			setLastChapterNumber(chapterToUpdate);
		else {
			updateOtherPositions = updateOtherPositions && true;
			chapterToUpdate.setPosition(updates.getPosition());
		}

		chapterToUpdate.setLastModification(new Date());	
		super.update(chapterToUpdate);

		if(updateOtherPositions) updateOtherChaptersPosition(chapterToUpdate);
		return chapterToUpdate;
	}

	@Override
	public Chapter create(Chapter objectToSave) {
		if(objectToSave.getId() == null)
			objectToSave.setId((objectToSave.getBook()+"_"+objectToSave.getNumber()).replaceAll("\\W+", "_"));
		objectToSave.setCreation(new Date());
		objectToSave.setText(nl2p(objectToSave.getText()));
		
		boolean updateOtherPositions = false;
		if(objectToSave.getPosition() == null || objectToSave.getPosition() == 0)
			setLastChapterNumber(objectToSave);
		else
			updateOtherPositions = true;
		
		Chapter newChapter = super.create(objectToSave);
		
		if(updateOtherPositions) updateOtherChaptersPosition(newChapter);
		return newChapter;
	}

	private void setLastChapterNumber(Chapter chapter) {
		log.debug("This is the last chapter");
		Chapter chap = findLastChapter(chapter.getBook(), BASIC_PROJECTION);
		int position = 1;
		if(chap != null)
			position = chap.getPosition()+1;
		chapter.setPosition(position);
	}
	
	private void updateOtherChaptersPosition(Chapter chapter) {
		log.debug("Updating other chapters position");
		List<Chapter> chapters = findChapters(chapter.getBook(), null, BASIC_PROJECTION);
		
		if(chapters != null) {
			for(Chapter ch : chapters) {
				if(!ch.getId().equals(chapter.getId()) && ch.getPosition().compareTo(chapter.getPosition()) >= 0) {
					ch.setPosition(ch.getPosition()+1);
					update(ch, false);
				}
			}
		}
	}

	private String nl2p(String text) {
		String escaped = StringEscapeUtils.escapeHtml4(text);
		return escaped.replaceAll("^(<p>)?(.*)(</p>)?$", "<p>$2</p>");
	}

	@Override
	public ChapterRepository getRepository() {
		return repository;
	}

}
