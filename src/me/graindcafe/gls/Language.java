package me.graindcafe.gls;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Graindcafe
 * 
 */
public class Language {

	/**
	 * Initialize a language and return it. Check if the given language is
	 * correct. Save the default language file.
	 * 
	 * @param languageName
	 *            the name of the language to load
	 * @return The language or the default language
	 */
	public static Language init(String languageName) {
		Language lang;
		// Save your default language
		// Should be first in case another language use it.
		DefaultLanguage.saveDefaultLanguage(DefaultLanguage.languageName);
		// Get the set language and load it
		if (languageName != DefaultLanguage.languageName) {
			lang = new Language(languageName);
			// check the version
			if (lang.getVersion() != DefaultLanguage.version)
				// 255 is a special return of getVersion, if the file was not
				// loaded
				if (lang.getVersion() == Byte.MAX_VALUE)
					lang.flag |=2;
				else {
					// The language file is outdated
					lang.flag|=4;
				}
			// check if all sentences are in the given language file, and put
			// the missing ones in its header
			if (!DefaultLanguage.checkLanguage(lang))
				lang.flag|=8;
		} else {
			// If it's your defaultlanguage, don't load a configuration and
			// unnecessary stuff
			lang = new DefaultLanguage();
		}
		return lang;
	}
	/**
	 * The language that this is based on
	 */
	protected Language Default;
	/**
	 * The YAML file corresponding to this language
	 */
	protected FileConfiguration File;
	protected File FileObject;
	/**
	 * Parsed strings
	 */
	protected HashMap<String, String> finalStrings;
	/**
	 * A flag indicating the state of file
	 */
	private byte flag=0;
	/**
	 * This language name
	 */
	protected String languageName;
	/**
	 * Prefixes for messages
	 */
	protected TreeMap<String,String> prefixes;
	/**
	 * Useful for the Default Language that doesn't use File, Default and
	 * finalStrings
	 */
	protected Language() {

	}

	/**
	 * Parse a language file
	 * @param languageName
	 *            Should be the language's file name
	 */
	protected Language(String languageName) {
		if (!languageName.substring(languageName.length() - 3)
				.equalsIgnoreCase("yml"))
			languageName += ".yml";
		// try to get the file as it is given 
		File f = new File(DefaultLanguage.languageFolder
				+ languageName);
		if (!f.exists()) {
			// no ? Maybe a problem of cases
			File dir = new File(DefaultLanguage.languageFolder);
			if (dir.exists()) {
				// List all files and try to get one corresponding with
				// languageName
				for (File file : dir.listFiles()) {
					if (file.getName().equalsIgnoreCase(languageName)) {
						f = file;
						break;
					}
				}
			}

		}
		this.FileObject=f;
		// strip the extension
		languageName = languageName.substring(0,languageName.length() - 4);
		if (f.exists()) {
			File = new YamlConfiguration();
			try {
				File.load(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			this.languageName = File.getString("Name", languageName);
			if (File.getString("Default", null) != null) {
				Default = new Language(File.getString("Default"));
			} else {
				Default = new DefaultLanguage();
			}
		} else {
			File = null;
			Default = new DefaultLanguage();
			this.languageName = languageName;
		}
		finalStrings = new HashMap<String, String>();
		prefixes = new TreeMap<String, String>();

	}
	/**
	 * Get a locale specified by the giving key
	 * 
	 * @param key
	 *            The key of the sentence you want to have
	 * @return The sentence or null if none of default languages have it.
	 */
	public String get(String key) {
		String finalString=finalStrings.get(key);
		if (finalString==null) {
			if (File != null) {
				finalString = File.getString(key, null);
				if (finalString != null)
					finalString = parseColor(finalString);
				else
					finalString = Default.get(key);
			} else
				finalString = Default.get(key);
			for(String start : prefixes.keySet())
				if(key.startsWith(start))
					finalString= prefixes.get(start)+finalString;
			finalStrings.put(key, finalString);
		}
		return finalString;
	} 
	/**
	 * Get all contributors for this language
	 * 
	 * @return Authors separated by comma
	 */
	public String getAuthor() {
		if (File != null)
			return File.getString("Author", "Anonymous")
					+ (Default instanceof DefaultLanguage ? "" : ", "
							+ Default.getAuthor());
		else
			return Default.getAuthor();
	}

	/**
	 * Get the default language
	 * 
	 * @return Default Language for this language
	 */
	protected Language getDefault() {
		return Default;
	}

	/**
	 * Get the file as a Configuration object
	 * 
	 * @return Configuration
	 */
	protected FileConfiguration getFile() {
		return File;
	}

	public File getFileObject()
	{
		return this.FileObject;
	}

	/**
	 * Get the language name
	 * 
	 * @return Language name
	 */
	public String getName() {
		return languageName;
	}

	/**
	 * Get the version or 255 if the file is missing
	 * 
	 * @return Version or 255 if the file is missing
	 */
	public byte getVersion() {
		if (File != null)
			return (byte) File.getInt(
					"Version",
					Default instanceof DefaultLanguage ? 0 : Default
							.getVersion());
		else
			return (byte) Byte.MAX_VALUE;
	}
	/**
	 * Read the flag to knoow if the file had missing nodes
	 * @return whether the language file had missing nodes
	 */
	public boolean hasMissingNode()
	{
		return (flag & 8)==8;
	}
	/**
	 * Is this language a root ?
	 * 
	 * @return true if this language is not completing/modifying another one.
	 */
	protected boolean isDefault() {
		return Default instanceof DefaultLanguage;
	}

	/**
	 * Read the flag to get if the specified file was found
	 * @return whether the language file was found
	 */
	public boolean isLoaded()
	{
		return (flag & 2)==2;
	}

	/**
	 * Read the flag to know if the specified file was up to date or outdated
	 * Depending of the version of the file and the version of the default language
	 * @return whether the language file was outdated
	 */
	public boolean isOutdated()
	{
		return (flag & 4)==4;
	}

	/**
	 * Replace &x in §x so that colors appear in game
	 * 
	 * @param s
	 *            The string to parse
	 * @return The parsed string
	 */
	public String parseColor(String s) {
		return s.replaceAll("[&](\\w{1})", "\u00A7$1");
	}
	/**
	 * Add a prefix to all nodes beginning with nodeBegin
	 * @param nodeBegin
	 * @param prefix
	 * @return If there was at least a node matching 
	 */
	public boolean setPrefix(String nodeBegin, String prefix)
	{
		if(finalStrings==null)
			return false;
		TreeMap<String,String> toAdd=new TreeMap<String,String>();
		for(Entry<String,String> entry : finalStrings.entrySet())
			if(entry.getKey().startsWith(nodeBegin))
				toAdd.put(entry.getKey(), prefix+entry.getValue());
		finalStrings.putAll(toAdd);
		prefixes.put(nodeBegin, prefix);
		return !toAdd.isEmpty();
	}
}
