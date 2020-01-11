package com.kitsunecode.mms.core.adapters.impl;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.Dialog;
import com.kitsunecode.mms.core.entities.exceptions.StartFailedException;
import com.kitsunecode.mms.core.entities.waifudata.WaifuData;
import com.kitsunecode.mms.core.settings.Settings;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Ship implements IWaifuAdapter {

	private static final String BASE_URL = "https://azurlane.koumakan.jp";

	private static final String LANGUAGE    = "waifu.language";
	private static final String NATIVE_LANG = "waifu.language.useNative";

	private static final String IMAGES_SELECTOR = ".adaptiveratioimg > a > img";
	private static final String TABLE_ROWS_JAP  = "div[title='Japanese Server'] > table:nth-child(3) > * tr";
	private static final String TABLE_ROWS_CN   = "div[title='Chinese Server'] > table:nth-child(3) > * tr";
	private static final String SKIN_NAMES      = ".azl_box_body > .tabber > div[title]";

	private static final String AUDIO_COL         = "td:nth-child(2) > a";
	private static final String EVENT_COL         = "td:nth-child(1)";
	private static final String DIALOG_NATIVE_COL = "td:nth-child(3)";
	private static final String DIALOG_TRANSL_COL = "td:nth-child(4)";

	private static final String lang = Settings.get(LANGUAGE, "Chinese");

	private String    name;
	private WaifuData data;
	private long      startTimeMillis;

	public Ship(String name) throws Exception {

		this.startTimeMillis = System.currentTimeMillis();
		this.name = name;

		try {
			if (IWaifuAdapter.hasSavedFile(this)) {
				data = IWaifuAdapter.getDataFromFile(this);
			} else {
				data = loadFromWiki();
				IWaifuAdapter.saveDataToFile(this);
			}
		} catch (HttpStatusException e) {
			String message = "Wiki status code: " + e.getStatusCode();
			if (e.getStatusCode() == 404) {
				message += ", probably this weapon does not exist";
			}
			throw new StartFailedException(message);
		} catch (Exception e) {
			throw new StartFailedException(e.getMessage());
		}
	}

	/**
	 * Loads data from Wiki, we MUST use it only once in a while
	 */
	private WaifuData loadFromWiki() throws IOException {
		System.out.println("Getting ship home page");
		Document mainDoc = Jsoup.connect(BASE_URL + "/" + name).get();
		System.out.println("Getting ship quotes");
		Document quotesDoc = Jsoup.connect(BASE_URL + "/" + name + "/Quotes").get();
		System.out.println("Parsing data...");

		return new WaifuData(loadDialogs(quotesDoc), loadSkinUrls(mainDoc));
	}

	private List<String> loadSkinNames(Document doc) {
		return Selector.select(SKIN_NAMES, doc)
				.stream()
				.map(e -> e.attr("title"))
				.collect(Collectors.toList());
	}

	private List<String> loadSkinUrls(Document doc) {
		return Selector.select(IMAGES_SELECTOR, doc).stream()
				.map(e -> e.attr("srcset"))
				.map(set -> Arrays.stream(set.split(","))
						.map(s -> BASE_URL + s.trim().split(" ")[0])
						.reduce((first, second) -> second)
						.orElse(null))
				.collect(Collectors.toList());
	}

	private List<Dialog> loadDialogs(Document doc) {
		List<Dialog> dialogList = new ArrayList<>();
		dialogList.addAll(loadDialogs(doc, TABLE_ROWS_CN, "Chinese"));
		dialogList.addAll(loadDialogs(doc, TABLE_ROWS_JAP, "Japanese"));
		return dialogList;
	}

	private List<Dialog> loadDialogs(Document doc, String selector, String lang) {
		List<Dialog> dialogList = new ArrayList<>();

		Elements rows = Selector.select(selector, doc);
		rows.remove(0);

		for (Element row : rows) {
			Element audioElem        = row.select(AUDIO_COL).first();
			String  audioUrl         = audioElem != null ? audioElem.attr("href") : "";
			String  eventText        = row.selectFirst(EVENT_COL).text().trim();
			if (eventText.contains("Idle")) {
				eventText = "Idle";
			}
			String  dialogText       = row.selectFirst(DIALOG_TRANSL_COL).text();
			String  dialogTextMative = row.selectFirst(DIALOG_NATIVE_COL).text();

			dialogList.add(new Dialog(lang, dialogText, eventText, audioUrl));
			dialogList.add(new Dialog(lang + " Native", dialogTextMative, eventText, audioUrl));
		}

		return dialogList;
	}

	@Override
	public WaifuData getWaifuData() {
		return this.data;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getShowableName() {
		return this.name;
	}

	@Override
	public int getSkinCount() {
		return this.data.getSkins().size();
	}

	@Override
	public String getSkin(int skinIndex) {
		return this.data.getSkins().get(skinIndex);
	}

	@Override
	public List<Dialog> getDialogs() {
		return data.getDialogs();
	}

	@Override
	public List<Dialog> getDialogs(String event) {
		return this.data.getDialogs().stream()
				.filter(d -> d.getEvent().equals(event))
				.filter(d -> d.getLanguage().equalsIgnoreCase(lang))
				.collect(Collectors.toList());
	}

	@Override
	public String onTouchEventKey() {
		return "Secretary (Touch)";
	}

	@Override
	public String onIdleEventKey() {
		return "Idle";
	}

	@Override
	public String onLoginEventKey() {
		return "Login";
	}

	@Override
	public long getUptime() {
		return System.currentTimeMillis() - startTimeMillis;
	}
}
