package it.samvise85.bookshelf.manager;

import java.util.Collection;

import it.samvise85.bookshelf.model.Setting;
import it.samvise85.bookshelf.persist.PersistOptions;

public interface SettingManager {

	Collection<Setting> getList(PersistOptions persistOptions);
	
	String getAppVersion();

	void setAppVersion(String versionCode);

	String getSetting(String key);

	Setting saveSetting(String versionCode, String key);

	String getSettingOrDefault(String key, String defualt);

}
