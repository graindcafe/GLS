package me.graindcafe.gls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

public class DefaultLanguage extends Language {

	/**
	 * You
	 */
	private static String author = "YourName";
	/**
	 * The default language folder.
	 */
	protected static String languageFolder = "plugins/YourPlugin/languages/";
	/**
	 * This language name
	 */
	protected static String languageName = "english";
	/**
	 * The default sentences
	 */
	private static HashMap<String, String> Strings = new HashMap<String, String>() {
		private static final long serialVersionUID = 7261634788355469790L;

		{
			put("File.TheDefaultLanguageFile",
					"This is the plugin default language file \nYou should not edit it ! All changes will be undone !\nCreate another language file (custom.yml) \nand put 'Default: english' if your default language is english\n");
			put("File.DefaultLanguageFile",
					"This is your default language file \nYou should not edit it !\nCreate another language file (custom.yml) \nand put 'Default: english' if your default language is english\n");
			put("File.LanguageFileComplete", "Your language file is complete");
			put("File.TranslationsToDo",
					"Translations to do in this language file");
			put("Info.ChosenLanguage", "Chosen language : %s. Provided by %s.");
			put("Warning.LanguageFileMissing",
					"The specified language file is missing!");
			put("Warning.LanguageOutdated", "Your language file is outdated!");
		}
	};
	/**
	 * Your language version, use it as you want.
	 */
	public static byte version = 1;

	/**
	 * Check if a language contains all sentences that the Default has. If not,
	 * it adds the missing ones in head of the language file
	 * 
	 * @param l
	 *            Language to check
	 * @return if the language was complete
	 */
	public static boolean checkLanguage(Language l) {
		if (l == null)
			return false;
		while (!l.isDefault())
			l = l.getDefault();
		boolean valid = true;
		FileConfiguration lFile = l.getFile();
		String value;
		Stack<String> todo = new Stack<String>();
		if (lFile == null)
			return false;
		for (String key : Strings.keySet()) {
			if (key.startsWith("File."))
				continue;
			value = lFile.getString(key, null);
			if (value == null) {
				todo.push(key);
			}
		}
		String header = l.get("File.DefaultLanguageFile").replaceAll("\\n",
				"\n");
		if (lFile.getInt("Version", 0) != DefaultLanguage.version) {
			valid = false;
			header += Strings.get("Warning.LanguageFileOutdated") + "\n";
		}
		if (todo.isEmpty())
			header += l.get("File.LanguageFileComplete") + "\n";
		else {
			valid = false;
			header += l.get("File.TranslationsToDo") + "\n";
		}
		while (!todo.isEmpty())
			header += "- " + todo.peek() + ": " + Strings.get(todo.pop())
					+ "\n";
		lFile.options().header(header);
		save(lFile, l.getFileObject());
		return valid;
	}

	/**
	 * Save the default language to a YAML file
	 * 
	 * @param fileName
	 *            the name of this language
	 */
	public static void saveDefaultLanguage(String fileName) {
		File dir = new File(DefaultLanguage.languageFolder);
		if (!dir.exists()) {
			String[] languageFolders = DefaultLanguage.languageFolder
					.split("/");
			String tmplevelFolder = "";
			for (byte i = 0; i < languageFolders.length; i++) {
				tmplevelFolder = tmplevelFolder.concat(languageFolders[i]
						+ java.io.File.separatorChar);
				dir = new File(tmplevelFolder);
				dir.mkdir();
			}
		}
		if (!fileName.substring(fileName.length() - 3).equalsIgnoreCase("yml"))
			fileName += ".yml";
		YamlConfiguration f = new YamlConfiguration();
		f.set("Author", DefaultLanguage.author);
		f.set("Version", DefaultLanguage.version);
		for (Entry<String, String> e : Strings.entrySet()) {
			f.set(e.getKey(), e.getValue());
		}

		f.options()
				.header("# This is the plugin default language file \n# You should not edit it ! All changes will be undone !\n# Create another language file (custom.yml) \n# and put 'Default: english' if your default language is english\n");
		save(f, fileName);
	}

	protected static void save(FileConfiguration f, String fileName) {
		save(f,
				new File(DefaultLanguage.languageFolder
						+ fileName.toLowerCase()));
	}

	protected static void save(FileConfiguration f, File file) {
		try {

			Files.createParentDirs(file);

			String data = f.saveToString();

			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8");

			try {
				writer.write(data);
			} finally {
				writer.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Set the author of the default language
	 * 
	 * @param name
	 *            : his name
	 */
	public static void setAuthor(String name) {
		author = name;
	}

	/**
	 * Set the language folder, should be
	 * plugin.getDataFolder().getPath()+"/languages/" =
	 * plugins/YOURPLUGIN/languages/
	 * 
	 * @param languagesFolder
	 */
	public static void setLanguagesFolder(String languagesFolder) {
		if (!languagesFolder.endsWith("/"))
			languagesFolder += "/";
		DefaultLanguage.languageFolder = languagesFolder;
	}

	/**
	 * Set the default language.
	 * 
	 * @param locales
	 *            HashMap<String,String>
	 */
	public static void setLocales(Map<String, String> locales) {
		Strings = new HashMap<String, String>() {
			private static final long serialVersionUID = 5476813380526513072L;
			{
				put("File.TheDefaultLanguageFile",
						"# This is the plugin default language file \n# You should not edit it ! All changes will be undone !\n# Create another language file (custom.yml) \n# and put 'Default: english' if your default language is english\n");

				put("File.DefaultLanguageFile",
						"# This is your default language file \n# You should not edit it !\n# Create another language file (custom.yml) \n# and put 'Default: english' if your default language is english\n");

				put("File.TranslationsToDo",
						"# Your language file is complete\n");

				put("File.TranslationsToDo",
						"# Translations to do in this language file\n");

				put("Info.ChosenLanguage",
						"Chosen language : %s. Provided by %s.");

				put("Warning.LanguageFileMissing",
						"The specified language file is missing!");

				put("Warning.LanguageOutdated",
						"Your language file is outdated!");
			}
		};
		Strings.putAll(locales);
	}

	/**
	 * Set the name of the default language (mostly "English")
	 * 
	 * @param name
	 *            : the name
	 */
	public static void setName(String name) {
		languageName = name;
	}

	/**
	 * Set the version of your language file
	 * 
	 * @param version
	 */
	public static void setVersion(byte version) {
		DefaultLanguage.version = version;
	}

	/**
	 * Get the node from the map
	 * 
	 * @return The value or null
	 */
	@Override
	public String get(String key) {
		return Strings.get(key);
	}

	/**
	 * Get the author
	 */
	@Override
	public String getAuthor() {
		return DefaultLanguage.author;
	}

	/**
	 * Get the version
	 */
	@Override
	public byte getVersion() {
		return DefaultLanguage.version;
	}
}