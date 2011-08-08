package me.graindcafe.gls;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.util.config.Configuration;

public class DefaultLanguage extends Language {
	/**
	 * Your language version, use it as you want.
	 */
	public static final byte Version = 1;
	/**
	 * You
	 */
	private static final String author = "Graindcafe";
	/**
	 * This language name
	 */
	protected static final String languageName="english";
	/**
	 * The default sentences
	 */
	private static HashMap<String, String> Strings = new HashMap<String, String>() {
		private static final long serialVersionUID = 9166935722459443352L;
		{
			put("File.DefaultLanguageFile",
					"# This is your default language file \n# You should not edit it !\n# Create another language file (custom.yml) \n# and put 'Default: english' if your default language is english\n");
			put("File.LanguageFileComplete", "# Your language file is complete\n");
			put("File.TranslationsToDo", "# Translations to do in this language file\n");
			put("Info.ChosenLanguage", "Chosen language : %s. Provided by %s.");
			put("Warning.LanguageFileMissing", "The specified language file is missing!");
			put("Warning.LanguageOutdated", "Your language file is outdated!");
			put("Message.AnExample", ChatColor.GREEN + "This is a green example!.");
			put("Message.AnotherExample", ChatColor.RED + "This is another example, that is red");
		}
	};
	/**
	 * Check if a language contains all sentences that the Default has.
	 * If not, it add the missing ones in head of the language file 
	 * @param l Language to check
	 * @return if the language was complete
	 */
	public static boolean checkLanguage(Language l) {
		while (!l.isDefault())
			l = l.getDefault();
		boolean retour = true;
		Configuration lFile = l.getFile();
		String value;
		Stack<String> todo = new Stack<String>();
		for (String key : Strings.keySet()) {
			value = lFile.getString(key, null);
			if (value == null) {
				todo.push(key);
			}
		}
		String header = l.get("File.DefaultLanguageFile");
		if (lFile.getInt("Version", 0) != DefaultLanguage.Version) {
			retour = false;
			header += "# " + Strings.get("Warning.LanguageFileOutdated") + "\n";
		}
		if (todo.isEmpty())
			header += l.get("File.LanguageFileComplete");
		else {
			retour = false;
			header += l.get("File.TranslationsToDo");
		}
		while (!todo.isEmpty())
			header += "# - " + todo.peek() + ": " + Strings.get(todo.pop()) + "\n";
		lFile.setHeader(header);
		lFile.save();
		return retour;
	}
	/**
	 * Save the default language to a YAML file
	 * @param fileName the name of this language 
	 */
	public static void saveDefaultLanguage(String fileName) {
		File dir = new File(Language.languagesFolder);
		if (!dir.exists()) {
			String[] languageFolders = Language.languagesFolder.split("/");
			String tmplevelFolder = "";
			for (byte i = 0; i < languageFolders.length; i++) {
				tmplevelFolder = tmplevelFolder.concat(languageFolders[i] + java.io.File.separatorChar);
				dir = new File(tmplevelFolder);
				dir.mkdir();
			}
		}
		if (fileName.substring(fileName.length() - 3) != "yml")
			fileName += ".yml";
		Configuration f = new Configuration(new File(Language.languagesFolder + fileName));
		f.setProperty("Author", DefaultLanguage.author);
		f.setProperty("Version", DefaultLanguage.Version);
		for (Entry<String, String> e : Strings.entrySet()) {
			f.setProperty(e.getKey(), e.getValue());
		}

		f.setHeader("# This is the plugin default language file \n# You should not edit it ! All changes will be undone !\n# Create another language file (custom.yml) \n# and put 'Default: english' if your default language is english\n");
		f.save();
	}
	
	
	
	@Override
	public String get(String key) {
		return Strings.get(key);
	}

	@Override
	public String getAuthor() {
		return DefaultLanguage.author;
	}

	@Override
	public byte getVersion() {
		return DefaultLanguage.Version;
	}
}