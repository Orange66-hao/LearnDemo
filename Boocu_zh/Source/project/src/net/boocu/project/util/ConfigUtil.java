package net.boocu.project.util;

import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
	public static String getConfig(String config) {
		return getConfig("application.properties", config);
	}

	public static String getConfig(String configfile, String config) {
		InputStream inputstream = ConfigUtil.class.getResourceAsStream("/"
				+ configfile);
		Properties properties = new Properties();
		try {
			properties.load(inputstream);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String s = properties.getProperty(config, null);
		return s;
	}

	public static String getConfig(String configfile, String configName,
			String defaultValue) {
		InputStream inputstream = ConfigUtil.class.getResourceAsStream("/"
				+ configfile);
		Properties properties = new Properties();
		try {
			properties.load(inputstream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String s = properties.getProperty(configName, defaultValue);
		return s;
	}

	public static void main(String[] args) {
		System.out.println("system.project_name="
				+ getConfig("system.project_name"));
	} 
}
