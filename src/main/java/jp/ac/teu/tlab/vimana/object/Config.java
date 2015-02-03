package jp.ac.teu.tlab.vimana.object;

import java.io.File;
import java.io.IOException;
import static jp.ac.teu.tlab.vimana.base.FileAccessUtil.getByteData;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 *
 * @author Yuta YAMAGUCHI
 */
public class Config {
    private final JSONObject config;
    
    public Config (String filePath)
            throws IOException {
        
        byte[] data = getByteData(new File(filePath));
        config = JSONObject.fromObject(new String(data));
    }
    
    public String getCharset()  {
        try {
            return config.getString("charset");
        } catch (JSONException ex) {
            return "UTF-8";
        }
    }
    
    public String getGlobalClassName() {
        try {
            return config.getString("globalSettings");
        } catch (JSONException ex) {
            return "";
        }
    }
    
    public JSONArray getControllerClassNames() {
        try {
            return config.getJSONArray("controllers");
        } catch (JSONException ex) {
            return new JSONArray();
        }
    }
    
    public String getTemplateLayoutPath() {
        try {
            return config.getString("templateLayout");
        } catch (JSONException ex) {
            return "";
        }
    }
    
    public String getStaticContentPageLayoutPath() {
        try {
            return config.getString("staticContentPageLayout");
        } catch (JSONException ex) {
            return "";
        }
    }
    
    public String getDynamicContentPageLayoutPath() {
        try {
            return config.getString("dynamicContentPageLayout");
        } catch (JSONException ex) {
            return "";
        }
    }
    
    public String getPluginLoadPath() {
        try {
            return config.getString("pluginLoadAbsolutePath");
        } catch (JSONException ex) {
            return "";
        }
    }
}
