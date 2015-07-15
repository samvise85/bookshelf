package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.Setting;
import it.samvise85.bookshelf.persist.AbstractPersistenceUnit;
import it.samvise85.bookshelf.persist.repository.SettingRepository;
import it.samvise85.bookshelf.utils.BookshelfConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingManagerImpl extends AbstractPersistenceUnit<Setting> implements SettingManager {
	
	@Autowired
	SettingRepository repository;
	
	public SettingManagerImpl() {
		super(Setting.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public SettingRepository getRepository() {
		return repository;
	}

	@Override
	public String getAppVersion() {
		return getSetting(BookshelfConstants.Settings.APP_VERSION);
	}

	@Override
	public void setAppVersion(String versionCode) {
		saveSetting(BookshelfConstants.Settings.APP_VERSION, versionCode);
	}

	@Override
	public Setting saveSetting(String key, String versionCode) {
		Setting setting = get(key);
		if(setting == null)
			setting = new Setting(key, versionCode);
		else
			setting.setValue(versionCode);
		return repository.save(setting);
	}

	@Override
	public String getSetting(String key) {
		Setting setting = get(key);
		if(setting != null) return setting.getValue();
		return null;
	}

	@Override
	public String getSettingOrDefault(String key, String defualt) {
		String value = getSetting(key);
		return value != null ? value : defualt;
	}

}
