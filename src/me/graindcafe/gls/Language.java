package me.graindcafe.gls;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.util.config.Configuration;

/**
 * @author Graindcafe
 * 
 */
public class Language {

	/**
	 * Initialize a language and return it. Check if the given language is correct. Save the default language file
	 * @param log the Minecraft Logger
	 * @param languageName the name of the language to load
	 * @return The language or the default language
	 */
	public static Language initLanguage(Logger log, String languageName)
	{
		Language Language;
		// Save your default language
		// Should be first in case another language use it.
		DefaultLanguage.saveDefaultLanguage(DefaultLanguage.languageName);
		// Get the set language and load it
		if (languageName  != DefaultLanguage.languageName) {
			Language = new Language(languageName);
			// check the version
			if (Language.getVersion() != DefaultLanguage.Version)
				// 255 is a special return of getVersion, if the file was not loaded
				if (Language.getVersion() == 255)
					log.warning(Language.get("Warning.LanguageFileMissing"));
				else {
					// Put the missing sentences in the default language file header
					DefaultLanguage.checkLanguage(Language);
					log.warning(Language.get("Warning.LanguageFileOutdated"));
				}
			// check if all sentences are in the given language file, and put the missing ones in its header
			else if (!DefaultLanguage.checkLanguage(Language))
				log.warning(Language.get("Warning.LanguageFileOutdated"));
		} else {
			// If it's your defaultlanguage, don't load a configuration and unnecessary stuff
			Language = new DefaultLanguage();
		}
		// Display the author
		log.info(String.format(Language.get("Info.ChosenLanguage"), languageName, Language.getAuthor()));
		return Language;
	}
	/**
	 * The YAML file corresponding to this language
	 */
	protected Configuration File;
	/**
	 * The language that this is based on
	 */
	protected Language Default;

	/**
	 * Parsed strings
	 */
	protected HashMap<String, String> finalStrings;
	/**
	 * The default language folder. DON'T FORGET THE LAST SLASH
	 */
	protected static String languagesFolder="plugins/YOURPLUGIN/languages/";
	/**
	 * This language name
	 */
	protected String languageName;
	/**
	 * Useful for the Default Language that doesn't use File, Default and finalStrings
	 */
	protected Language() {

	}
	/**
	 * 
	 * @param LanguageName Should be the language's file name
	 */
	public Language(String LanguageName) {

		java.io.File f = new java.io.File(Language.languagesFolder + LanguageName);
		if (!f.exists()) {
			f = new java.io.File(Language.languagesFolder + LanguageName + ".yml");
		}

		if (f.exists()) {
			File = new Configuration(f);
			File.load();

			if (File.getString("Default", null) != null) {
				Default = new Language(File.getString("Default"));
			} else {
				Default = new DefaultLanguage();
			}
		} else {
			File = null;
			Default = new DefaultLanguage();
		}
		finalStrings = new HashMap<String, String>();
		languageName=File.getString("Name",LanguageName);

	}
	/**
	 * Get a locale specified by the giving key
	 * @param key The key of the sentence you want to have
	 * @return The sentence or null if none of default languages have it.
	 */
	public String get(String key) {
		if (finalStrings.containsKey(key)) {
			return finalStrings.get(key);
		} else {
			String finalString;
			if (File != null) {
				finalString = File.getString(key, null);
				if (finalString != null)
					finalString = parseColor(finalString);
				else
					finalString = Default.get(key);
			} else
				finalString = Default.get(key);
			finalStrings.put(key, finalString);
			return finalString;
		}
	}
	/**
	 * Get all contributors for this language
	 * @return Authors separated by comma
	 */
	public String getAuthor() {
		if (File != null)
			return File.getString("Author", "Anonymous") + (Default instanceof DefaultLanguage ? "" : ", " + Default.getAuthor());
		else
			return Default.getAuthor();
	}
	/**
	 * Get the default language 
	 * @return Default Language for this language
	 */
	protected Language getDefault() {
		return Default;
	}
	/**
	 * Get the file as a Configuration object
	 * @return Configuration
	 */
	protected Configuration getFile() {
		return File;
	}
	/**
	 * Get the language name
	 * @return Language name
	 */
	public String getName()
	{
		return languageName;
	}
	/**
	 * Get the version or 255 if the file is missing
	 * @return Version or 255 if the file is missing
	 */
	public byte getVersion() {
		if (File != null)
			return (byte) File.getInt("Version", Default instanceof DefaultLanguage ? 0 : Default.getVersion());
		else
			return (byte) 255;
	}
	/**
	 * Is this language a root ?
	 * @return true if this language is not completing/modifying another one.
	 */
	protected boolean isDefault() {
		return Default instanceof DefaultLanguage;
	}
	/**
	 * Replace &x in �x so that colors appear in game 
	 * @param s The string to parse
	 * @return The parsed string
	 */
	public String parseColor(String s) {
		return s.replaceAll("[&](\\w{1})", "\u00A7$1");
	}
}
