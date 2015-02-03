package jp.ac.teu.tlab.vimana.object;

import jp.ac.teu.tlab.vimana.object.page.StaticContentPage;
import jp.ac.teu.tlab.vimana.object.page.DynamicContentPage;
import jp.ac.teu.tlab.vimana.object.page.ContentPage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.json.JSONObject;

/**
 *
 * @author Yuta YAMAGUCHI
 */
public class Navigation {
    private static final ArrayList<Navigation> navigations = new ArrayList<>();
    private final ConcurrentHashMap<String, ContentPage> pageMap = new ConcurrentHashMap<>();
    private String dynamicContentPath = "";
    protected String name = "";
    protected String jpName = "";
    
    public Navigation(String name, String jpName) {
        this.name = name;
        this.jpName = jpName;
        setThis();
    }
    
    public Navigation(String name, String jpName, String dynamicContentPath) {
        this.name = name;
        this.jpName = jpName;
        this.dynamicContentPath = dynamicContentPath;
        setThis();
    }
    
    public static ArrayList<Navigation> getNavigationList() {
        return navigations;
    }
    
    public String getName() {
        return name;
    }
    
    public String getJpName() {
        return jpName;
    }
    
    public JSONObject getJSONObject() {
        JSONObject res = new JSONObject();
        res.accumulate("name", name);
        res.accumulate("jp_name", jpName);
        return res;
    }
    
    public ContentPage getPage(String path)
            throws IOException {
        
        if (!pageMap.containsKey(path)) {
            if (dynamicContentPath.equals("")) {
                pageMap.put(path, new StaticContentPage(path));
            } else {
                pageMap.put(path, new DynamicContentPage(path, dynamicContentPath));
            }
        }
        
        return pageMap.get(path);
    }
    
    private void setThis() {
        navigations.add(this);
    }
}