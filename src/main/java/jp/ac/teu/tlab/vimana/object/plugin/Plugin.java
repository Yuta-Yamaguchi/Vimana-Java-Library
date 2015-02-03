package jp.ac.teu.tlab.vimana.object.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import static jp.ac.teu.tlab.vimana.object.file.StaticFile.setFile;

/**
 *
 * @author YUU
 */
public class Plugin {
    private static final String PLUGIN_PATH = "/public/pluigin/";
    private static final ArrayList<String> plugins = new ArrayList();
    private static final ConcurrentHashMap<String, PluginSrcFile> pluginSrcFileMap = new ConcurrentHashMap<>();
    
    
    private final String pluginName;
    private String concatJavaScriptFileData = "";
    private String concatStyleSheetFileData = "";
    
    public Plugin(String pluginName, File pluginDir) 
            throws IOException {
        
        this.pluginName = pluginName;
        
        if (!(plugins.contains(pluginName))) {
            plugins.add(pluginName);
        }
        
        loadFiles(pluginDir);
    }
    
    public static void clearPluginList() {
        plugins.clear();
    }
    
    public static ArrayList<String> getPluginList() {
        return plugins;
    }
    
    public String getPluginName() {
        return pluginName;
    }
    
    public String getConcatJavaScriptFileData() {
        return concatJavaScriptFileData;
    }
    
    public String getConcatStyleSheetFileData() {
        return concatStyleSheetFileData;
    }
    
    private void loadFiles(File file) throws IOException {
        if (file.exists() && file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                loadFiles(childFile);
            }
        } else if (file.exists() && file.isFile()) {
            if (file.getName().endsWith(".min.js")) {
                concatJavaScriptFileData += getJavaScriptFile(file).getFileData();
            } else if (file.getName().endsWith(".min.css")) {
                concatStyleSheetFileData += getStyleSheetFile(file).getFileData();
            } else {
                // other files
                if (file.getName().endsWith(".js") || file.getName().endsWith(".css")) {
                    return;
                }
                setFile(PLUGIN_PATH + pluginName + file.getName(), file.getAbsolutePath());
            }
        }
    }
    
    private JavaScriptFile getJavaScriptFile(File file) throws IOException {
        String absolutePath = file.getAbsolutePath();
        if (!(pluginSrcFileMap.containsKey(absolutePath))) {
            pluginSrcFileMap.put(absolutePath, new JavaScriptFile(file));
        }
        return (JavaScriptFile) pluginSrcFileMap.get(absolutePath);
    }
    
    private StyleSheetFile getStyleSheetFile(File file) throws IOException {
        String absolutePath = file.getAbsolutePath();
        if (!(pluginSrcFileMap.containsKey(absolutePath))) {
            pluginSrcFileMap.put(absolutePath, new StyleSheetFile(file));
        }
        return (StyleSheetFile) pluginSrcFileMap.get(absolutePath);
    } 
}
