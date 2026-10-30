// 
// Decompiled by Procyon v0.5.36
// 

package core.praya.agarthalib.builder.message;

import com.praya.agarthalib.utility.TextUtil;
import java.util.Map;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import core.praya.agarthalib.enums.main.JsonOption;
import java.util.HashMap;
import java.util.List;
import com.praya.agarthalib.utility.JsonUtil;
import com.praya.agarthalib.modern.Paper26HoverItemPayload;

public class JsonBuild extends JsonUtil
{
    private String rawText;
    private String color;
    private String insertion;
    private List<String> listText;
    private HashMap<Integer, JsonOption> mapClickAction;
    private HashMap<Integer, JsonOption> mapHoverAction;
    private HashMap<Integer, String> mapClickValue;
    private HashMap<Integer, String> mapHoverValue;
    
    public JsonBuild() {
        this.rawText = null;
        this.color = null;
        this.insertion = null;
        this.listText = new ArrayList<String>();
        this.mapClickAction = new HashMap<Integer, JsonOption>();
        this.mapHoverAction = new HashMap<Integer, JsonOption>();
        this.mapClickValue = new HashMap<Integer, String>();
        this.mapHoverValue = new HashMap<Integer, String>();
    }
    
    public JsonBuild(final String rawText) {
        this.rawText = null;
        this.color = null;
        this.insertion = null;
        this.listText = new ArrayList<String>();
        this.mapClickAction = new HashMap<Integer, JsonOption>();
        this.mapHoverAction = new HashMap<Integer, JsonOption>();
        this.mapClickValue = new HashMap<Integer, String>();
        this.mapHoverValue = new HashMap<Integer, String>();
        this.rawText = rawText;
    }
    
    public JsonBuild(final String rawText, final String color) {
        this.rawText = null;
        this.color = null;
        this.insertion = null;
        this.listText = new ArrayList<String>();
        this.mapClickAction = new HashMap<Integer, JsonOption>();
        this.mapHoverAction = new HashMap<Integer, JsonOption>();
        this.mapClickValue = new HashMap<Integer, String>();
        this.mapHoverValue = new HashMap<Integer, String>();
        this.rawText = rawText;
        this.color = color;
    }
    
    public JsonBuild(final String rawText, final String color, final String insertion) {
        this.rawText = null;
        this.color = null;
        this.insertion = null;
        this.listText = new ArrayList<String>();
        this.mapClickAction = new HashMap<Integer, JsonOption>();
        this.mapHoverAction = new HashMap<Integer, JsonOption>();
        this.mapClickValue = new HashMap<Integer, String>();
        this.mapHoverValue = new HashMap<Integer, String>();
        this.rawText = rawText;
        this.color = color;
        this.insertion = insertion;
    }
    
    public final JsonBuild setRawText(final String rawText) {
        this.rawText = rawText;
        return this;
    }
    
    public final JsonBuild setColor(final String color) {
        this.color = color;
        return this;
    }
    
    public final JsonBuild setInsertion(final String insertion) {
        this.insertion = insertion;
        return this;
    }
    
    public final JsonBuild addText(final String text) {
        this.listText.add(text);
        return this;
    }
    
    public final JsonBuild addClickEvent(final JsonOption jsonOption, final String value) {
        if (this.isAvailable() && JsonUtil.isJsonClick(jsonOption)) {
            this.mapClickAction.put(this.getIndex(), jsonOption);
            this.mapClickValue.put(this.getIndex(), value);
        }
        return this;
    }
    
    public final JsonBuild addClickEvent(final String text, final JsonOption jsonOption, final String value) {
        if (JsonUtil.isJsonClick(jsonOption)) {
            this.addText(text);
            this.mapClickAction.put(this.getIndex(), jsonOption);
            this.mapClickValue.put(this.getIndex(), value);
        }
        return this;
    }
    
    public final JsonBuild addHoverEvent(final JsonOption jsonOption, final String value) {
        if (this.isAvailable() && JsonUtil.isJsonHover(jsonOption)) {
            this.mapHoverAction.put(this.getIndex(), jsonOption);
            this.mapHoverValue.put(this.getIndex(), value);
        }
        return this;
    }
    
    public final JsonBuild addHoverEvent(final String text, final JsonOption jsonOption, final String value) {
        if (JsonUtil.isJsonHover(jsonOption)) {
            this.addText(text);
            this.mapHoverAction.put(this.getIndex(), jsonOption);
            this.mapHoverValue.put(this.getIndex(), value);
        }
        return this;
    }
    
    public final String getRawText() {
        return this.rawText;
    }
    
    public final String getColor() {
        return this.color;
    }
    
    public final String getInsertion() {
        return this.insertion;
    }
    
    public final int getSize() {
        return this.listText.size();
    }
    
    public final String getText(final int index) {
        return (index < this.getSize() && index >= 0) ? this.listText.get(index) : null;
    }
    
    public final JsonOption getClickAction(final int index) {
        return this.mapClickAction.get(index);
    }
    
    public final String getClickValue(final int index) {
        return this.mapClickValue.get(index);
    }
    
    public final JsonOption getHoverAction(final int index) {
        return this.mapHoverAction.get(index);
    }
    
    public final String getHoverValue(final int index) {
        return this.mapHoverValue.get(index);
    }
    
    public final boolean hasText(final int index) {
        return index < this.getSize() && index >= 0;
    }
    
    public final boolean hasRawText() {
        return this.rawText != null;
    }
    
    public final boolean hasColor() {
        return this.color != null;
    }
    
    public final boolean hasInsertion() {
        return this.insertion != null;
    }
    
    public final boolean hasClick(final int index) {
        return this.hasText(index) && this.mapClickAction.containsKey(index);
    }
    
    public final boolean hasHover(final int index) {
        return this.hasText(index) && this.mapHoverAction.containsKey(index);
    }
    
    public final String read() {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        if (this.hasRawText()) {
            map.put("text", colorizeLegacy(this.getRawText()));
        }
        else {
            map.put("text", "");
        }
        if (this.hasColor()) {
            map.put("color", colorizeLegacy(this.getColor()));
        }
        if (this.hasInsertion()) {
            map.put("insertion", colorizeLegacy(this.getInsertion()));
        }
        if (this.isAvailable()) {
            final List<Object> extra = new ArrayList<Object>();
            for (int i = 0; i < this.getSize(); ++i) {
                final HashMap<String, Object> mapExtra = new HashMap<String, Object>();
                final String text = this.getText(i);
                mapExtra.put("text", colorizeLegacy(text));
                if (this.hasClick(i)) {
                    final HashMap<String, String> mapClick = new HashMap<String, String>();
                    mapClick.put("action", this.getClickAction(i).getText());
                    mapClick.put("value", colorizeLegacy(this.getClickValue(i)));
                    mapExtra.put("clickEvent", mapClick);
                }
                if (this.hasHover(i)) {
                    final JsonOption action = this.getHoverAction(i);
                    final String value = this.getHoverValue(i);
                    final HashMap<String, Object> mapHover = new HashMap<String, Object>();
                    mapHover.put("action", action.getText());
                    if (action == JsonOption.HOVER_SHOW_ITEM
                            && Paper26HoverItemPayload.isMarker(value)) {
                        // Only our versioned, validated marker uses modern
                        // show_item.contents. Keep historic value-based DSL
                        // unchanged for other types and old item strings.
                        final Object contents = Paper26HoverItemPayload.contents(value);
                        if (contents != null) {
                            mapHover.put("contents", contents);
                            mapExtra.put("hoverEvent", mapHover);
                        }
                    }
                    else {
                        mapHover.put("value", colorizeLegacy(value));
                        mapExtra.put("hoverEvent", mapHover);
                    }
                }
                extra.add(mapExtra);
            }
            if (!extra.isEmpty()) {
                map.put("extra", extra);
            }
        }
        final JSONObject jsonObject = new JSONObject((Map)map);
        // Colorize legacy message fields before serialization. Do not mutate
        // the item's raw data components when rendering hover JSON.
        return jsonObject.toString();
    }
    
    private static String colorizeLegacy(final String text) {
        return text == null ? null : TextUtil.colorful(text);
    }

    private final int getIndex() {
        return this.listText.size() - 1;
    }
    
    private final boolean isAvailable() {
        return !this.listText.isEmpty();
    }
}
