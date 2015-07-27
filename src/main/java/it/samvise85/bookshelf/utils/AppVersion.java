package it.samvise85.bookshelf.utils;


public enum AppVersion {
	
	VOID(null),
	
	AGRAJAG("1.0.0"),
	BLART_VERSENWALT_III("1.3.0"),
	COLIN("1.4.0"),
	
	MILLIWAYS("LAST");
	
	private String versionCode;

	private AppVersion(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionCode() {
		return versionCode;
	}
	
	public int getMajor() {
		return getPart(0);
	}
	
	public int getMinor() {
		return getPart(1);
	}
	
	public int getPatch() {
		return getPart(2);
	}

	private int getPart(int part) {
		if(versionCode != null) {
			String[] split = versionCode.split("\\.");
			if(split != null && split.length >= part) {
				String string = split[part];
				string.replaceAll("[^0-9]", "");
				try {
					return Integer.parseInt(string);
				} catch(Exception e) {
					//DO NOTHING
				}
			}
		}
		return 0;
	}
	
	public static AppVersion findByVersionCode(String versionCode) {
		for(AppVersion v : AppVersion.values())
			if(v.getVersionCode() == versionCode //only for nulls! 
					|| (v.getVersionCode() != null && v.getVersionCode().equalsIgnoreCase(versionCode)))
				return v;
		return null;
	}
}
