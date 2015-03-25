package it.samvise85.bookshelf.persist.file;

import it.samvise85.bookshelf.model.Identifiable;
import it.samvise85.bookshelf.persist.exception.PersistException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class FileRetriever {

	private static final Logger log = Logger.getLogger(FileRetriever.class);
	
	//TODO externalize configuration
	private static final String DATA_PATH = "C:\\bookshelf\\data";
	private static final String TRASH_PATH = "C:\\bookshelf\\trash";
	private static final String DAT = "dat";
	private static final String EXTENSION = "." + DAT;
	
	private static File dataDir = new File(DATA_PATH);
	private static File trashDir = new File(TRASH_PATH);

	public static <T extends Identifiable> List<String> readList(Class<T> clazz) throws PersistException {
		File dir = getDataDirectory(clazz);
		Collection<File> listFiles = FileUtils.listFiles(dir, new String[] {DAT}, false);
		
		List<String> res = new ArrayList<String>(0);
		
		Iterator<File> iterator = listFiles.iterator();
		while(iterator.hasNext()) {
			try {
				 res.add(FileUtils.readFileToString(iterator.next()));
			} catch (IOException e) {
				throw new PersistException(e.getMessage(), e);
			}
		}
		return res;
	}
	
	public static <T extends Identifiable> String read(Class<T> clazz, String id) throws PersistException {
		File file = getFile(clazz, id);
		if(!file.exists()) {
			throw new PersistException("File " + file.getAbsolutePath() + " does not exists.");
		}
		
		try {
			return FileUtils.readFileToString(file);
		} catch (IOException e) {
			throw new PersistException(e.getMessage(), e);
		}
	}
	
	public static <T extends Identifiable> File save(Class<T> clazz, String id, String content) throws PersistException {
		File file = getFile(clazz, id);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new PersistException(e.getMessage(), e);
			}
		} else {
			throw new PersistException("File " + file.getAbsolutePath() + " aldready exists. Please use Update.");
		}
		
		try {
			FileUtils.writeStringToFile(file, content);
		} catch (IOException e) {
			throw new PersistException(e.getMessage(), e);
		}
		return file;
	}
	
	public static <T extends Identifiable> File delete(Class<T> clazz, String id) throws PersistException {
		File file = getFile(clazz, id);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new PersistException(e.getMessage(), e);
			}
		}
		
		try {
			return moveToTrash(clazz, file);
		} catch (IOException e) {
			throw new PersistException(e.getMessage(), e);
		}
	}

	public static <T extends Identifiable> File update(Class<T> clazz, String id, String content) {
		File orig = getFile(clazz, id);
		if(!orig.exists())
			throw new PersistException("File " + orig.getAbsolutePath() + " doesn't exists. Please use Save.");
		File dir = getDataDirectory(clazz);
		//copy file for backup
		File copy = null;
		try {
			copy = copyWithNewName(orig, dir);
		} catch (IOException e) {
			throw new PersistException(e.getMessage(), e);
		}
		//delete file
		orig.delete();
		
		//save
		File updated = null;
		try {
			updated = save(clazz, id, content);
		} catch(Exception e) {
			log.error("REVERT!");
			try {
				copyFile(copy, getDataDirectory(clazz), orig.getName());
				copy.delete();
			} catch (IOException e1) {
				log.error("Could not revert! Please do it manually. File: " + copy.getAbsolutePath());
			}
			throw new PersistException(e.getMessage(), e);
		}
		
		try {
			copy(copy, getTrashDirectory(clazz));
			copy.delete();
		} catch (IOException e) {
			throw new PersistException(e.getMessage(), e);
		}
		return updated;
	}

	static <T extends Identifiable> File getFile(Class<T> clazz, String id) {
		return new File(getDataDirectory(clazz), id + EXTENSION);
	}
	
	private static <T extends Identifiable> File getDataDirectory(Class<T> clazz) {
		return getDirectory(clazz, dataDir);
	}
	
	private static <T extends Identifiable> File getTrashDirectory(Class<T> clazz) {
		return getDirectory(clazz, trashDir);
	}
	
	private static <T extends Identifiable> File getDirectory(Class<T> clazz, File dir) {
		if(!dir.exists())
			dir.mkdirs();
		
		String folderName = clazz.getSimpleName();
		File classDir = new File(dir, folderName);
		
		if(!classDir.exists())
			classDir.mkdir();
		
		return classDir;
	}

	private static <T extends Identifiable> File moveToTrash(Class<T> clazz, File file) throws IOException {
		File trash = getTrashDirectory(clazz);
		File trashFile = copyWithNewName(file, trash);
		
		file.delete();
		return trashFile;
	}

	private static File copyWithNewName(File file, File newDir) throws IOException {
		return copyFile(file, newDir, createNewName(file));
	}

	private static String createNewName(File file) {
		return file.getName().replace(EXTENSION, "_" + new Date().getTime() + EXTENSION);
	}

	private static File copy(File file, File newDir) throws IOException {
		return copyFile(file, newDir, file.getName());
	}

	private static File copyFile(File file, File newDir, String newName) throws IOException {
		File newFile = new File(newDir, newName);
		FileUtils.copyFile(file, newFile, true);
		log.info("Copied " + file.getAbsoluteFile() + " to " + newFile.getAbsolutePath());
		return newFile;
	}
}
