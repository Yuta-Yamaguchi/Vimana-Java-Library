package jp.ac.teu.tlab.vimana.base;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.ac.teu.tlab.vimana.object.plugin.Plugin;
import jp.ac.teu.tlab.vimana.object.file.PseudoStaticFile;
import static jp.ac.teu.tlab.vimana.object.file.StaticFile.setFile;

/**
 *
 * @author YUU
 */
public class PluginLoader {
    private static final String OUTPUT_JAVA_SCRIPT_FILE_NAME = "portal.plugins.min.js";
    private static final String OUTPUT_STYLE_SHEET_FILE_NAME = "portal.plugins.min.css";
    private static final long CYCLE = 60000; // 1m = (1000 * 60) * 1

    private static String concatJavaScriptFileData = "";
    private static String concatStyleSheetFileData = "";
    
    public static void loadPlugin(final String loadPath) throws IOException {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    // 初期化
                    concatJavaScriptFileData = "";
                    concatStyleSheetFileData = "";
                    Plugin.clearPluginList();

                    File loadDir = new File(loadPath);
                    if (loadDir.exists() && loadDir.isDirectory()) {
                        for (File pluginDir : loadDir.listFiles()) {
                            if (pluginDir.exists() && pluginDir.isDirectory()) {
                                Plugin plugin = new Plugin(pluginDir.getName(), pluginDir);
                                concatJavaScriptFileData += plugin.getConcatJavaScriptFileData();
                                concatStyleSheetFileData += plugin.getConcatStyleSheetFileData();
                            }
                        }
                    }
                    
                    setFile(PseudoStaticFile.PSEUDO_PATH + "/" + OUTPUT_JAVA_SCRIPT_FILE_NAME, concatJavaScriptFileData.getBytes());
                    setFile(PseudoStaticFile.PSEUDO_PATH + "/" + OUTPUT_STYLE_SHEET_FILE_NAME, concatStyleSheetFileData.getBytes());
                } catch (IOException ex) {
                    Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 0, CYCLE);
    }
}
