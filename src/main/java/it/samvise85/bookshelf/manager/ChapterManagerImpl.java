package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.book.Chapter;
import it.samvise85.bookshelf.model.comment.Stream;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.NoProjectionClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.clauses.SimpleProjectionClause;
import it.samvise85.bookshelf.persist.database.ChapterRepository;
import it.samvise85.bookshelf.persist.database.DatabasePersistenceUnit;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ChapterManagerImpl extends DatabasePersistenceUnit<Chapter> implements ChapterManager {
	private static final SimpleProjectionClause BASIC_PROJECTION = new SimpleProjectionClause("id", "position", "book");

	private static final Logger log = Logger.getLogger(ChapterManagerImpl.class);
	
	@Autowired
	protected ChapterRepository repository;
	@Autowired
	protected StreamManager streamManager;

	public ChapterManagerImpl() {
		super(Chapter.class);
	}

	@Override
	public Chapter get(Serializable id) {
		return super.get(id);
	}

	@Override
	public Chapter get(Serializable id, ProjectionClause projection) {
		return super.get(id, projection);
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
		Chapter chapter = repository.findFirstByBookAndPosition(book, position);
		if(chapter == null) return null;
		return chapter.setProjection(projection);
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
		
		boolean last = false;
		if(updates.getPosition() == null || updates.getPosition() == -1) {
			setLastChapterNumber(chapterToUpdate);
			last = true;
		} else {
			updateOtherPositions = updateOtherPositions && updates.getPosition() != chapterToUpdate.getPosition();
			chapterToUpdate.setPosition(updates.getPosition());
		}

		chapterToUpdate.setLastModification(new Date());	
		super.update(chapterToUpdate);

		if(updateOtherPositions) updateChaptersPosition(chapterToUpdate, last);
		return chapterToUpdate;
	}

	@Override
	public Chapter create(Chapter objectToSave) {
		if(objectToSave.getId() == null)
			objectToSave.setId((objectToSave.getBook()+"_"+objectToSave.getNumber()).replaceAll("\\W+", "_"));
		objectToSave.setCreation(new Date());
		objectToSave.setText(objectToSave.getText());
		
		boolean updateOtherPositions = false;
		if(objectToSave.getPosition() == null || objectToSave.getPosition() == -1)
			setLastChapterNumber(objectToSave);
		else
			updateOtherPositions = true;
		
		Chapter newChapter = addStream(super.create(objectToSave));
		
		if(updateOtherPositions) updateChaptersPosition(newChapter, !updateOtherPositions);
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

	@Transactional(value=TxType.REQUIRES_NEW)
	private Chapter addStream(Chapter ch) {
		if(ch == null) return null;
		
		if(ch.getStream() == null) {
			Stream stream = streamManager.create();
			ch.setStream(stream.getId());
			super.update(ch);
		}
		return ch;
	}
	
	private Chapter updateChaptersPosition(Chapter chapter, boolean last) {
		log.debug("Updating other chapters position");
		List<Chapter> chapters = findChapters(chapter.getBook(), null, BASIC_PROJECTION);
		
		if(chapters != null) {
			int lastPosition = 0;
			Chapter lastChapter = null;
			for(Chapter ch : chapters) {
				Chapter currChapter = ch;
				if(ch.getId().equalsIgnoreCase(chapter.getId())) {
					chapter = ch;
					if(!last) {
						//the current updating chapter has the precedence on every other chapter
						chapter.setPosition(lastPosition);
						update(chapter, false);
						currChapter = lastChapter;
					}
				}
				if(currChapter != null) {
					currChapter.setPosition(++lastPosition);
					update(currChapter, false);
					lastChapter = currChapter;
				}
			}
		}
		return chapter;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ChapterRepository getRepository() {
		return repository;
	}
}
