package it.samvise85.bookshelf.persist.file;

import it.samvise85.bookshelf.model.Identifiable;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.PersistenceUnit;
import it.samvise85.bookshelf.persist.clauses.Order;
import it.samvise85.bookshelf.persist.clauses.OrderClause;
import it.samvise85.bookshelf.persist.clauses.PaginationClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.clauses.SelectionClause;
import it.samvise85.bookshelf.persist.exception.PersistException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.ReflectionUtils;

public class FilePersistenceUnit<T extends Identifiable> implements PersistenceUnit<T> {
	private Class<T> registeredClass;
	private static final List<OrderClause> DEFAULT_ORDER = Collections.singletonList(new OrderClause("id", Order.ASC));
	
	private static final Logger log = Logger.getLogger(FilePersistenceUnit.class);
	
	public FilePersistenceUnit(Class<T> clazz) {
		registerClass(clazz);
	}

	private void registerClass(Class<T> clazz) {
		this.registeredClass = clazz;
	}

	public T get(String id) {
		T object = FileMarshaller.unmarshall(registeredClass, FileRetriever.read(registeredClass, id));
		return object;
	}

	@Override
	public T get(String id, ProjectionClause projection) {
		T object = FileMarshaller.unmarshall(registeredClass, FileRetriever.read(registeredClass, id), projection);
		return object;
	}

	@Override
	public List<T> getList(PersistOptions options) {
		List<String> objects = FileRetriever.readList(registeredClass);

		List<T> list = new ArrayList<T>();
		for(String o : objects) {
			T object = FileMarshaller.unmarshall(registeredClass, o, (PersistOptions) options != null ? options.getProjection() : null);

			boolean select = true;
			if(options != null && options.getSelection() != null) {
				try {
					for(SelectionClause sel : options.getSelection())
						if(!(select = sel.select(object))) break;
				} catch(Exception e) {
					log.error(registeredClass.getSimpleName() + ": An error occurred during selection", e);
					select = false;
				}
			}
			if(select)
				list.add(object);
		}
		
		order(list, options != null && options.getOrder() != null ? options.getOrder() : DEFAULT_ORDER);
		if(options != null && options.getPagination() != null)
			list = crop(list, options.getPagination());
		return list;
	}

	private void order(List<T> list, final List<OrderClause> orderClauses) {
		Comparator<T> comparator = new Comparator<T>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public int compare(T o1, T o2) {
				int res = 0;
				Iterator<OrderClause> iterator = orderClauses.iterator();
				while(res == 0 && iterator.hasNext()) {
					try {
						OrderClause orderClause = iterator.next();
						int subComp = 0;
						Field field = registeredClass.getDeclaredField(orderClause.getField());
						ReflectionUtils.makeAccessible(field);
						if(field.get(o1) instanceof Comparable) {
							Comparable f1 = (Comparable) ReflectionUtils.getField(field, o1);
							Comparable f2 = (Comparable) ReflectionUtils.getField(field, o2);
							if(f1 == null && f2 == null) subComp = 0;
							else if(f1 == null) subComp = -1;
							else if(f2 == null) subComp = 1;
							else subComp = f1.compareTo(f1.getClass().cast(f2));
							
							if(orderClause.getOrder() == Order.DESC)
								subComp = -1 * subComp;
						}
						res = subComp;
					} catch (Exception e) {
						throw new PersistException(e.getMessage(), e);
					}
				}
				return res;
			}
		};

		Collections.sort(list, comparator);
	}
	
	private List<T> crop(List<T> list, PaginationClause pagination) {
		if(list != null && !list.isEmpty() && pagination.getPageSize() > 0) {
			//please note: toIndex is exclusive!
			int fromIndex = pagination.getPageSize()*(pagination.getPage()-1);
			int toIndex = pagination.getPageSize()*pagination.getPage(); 
			
			if(fromIndex >= list.size()) return Collections.emptyList();
			if(toIndex > list.size()) toIndex = list.size();
			
			list = list.subList(fromIndex, toIndex);
		}
		return list;
	}

	public T create(T objectToSave) {
		FileRetriever.save(objectToSave.getClass(), objectToSave.getId(), FileMarshaller.marshall(objectToSave));
		return get(objectToSave.getId());
	}

	public T update(T objectToUpdate) {
		FileRetriever.update(objectToUpdate.getClass(), objectToUpdate.getId(), FileMarshaller.marshall(objectToUpdate));
		return null;
	}

	public T delete(String id) {
		T object = get(id);
		FileRetriever.delete(registeredClass, id);
		return object;
	}

}
