package jp.ac.teu.tlab.vimana.object.plugin;

import java.io.File;
import java.io.IOException;
import jp.ac.teu.tlab.vimana.base.FileAccessUtil;

/**
 *
 * @author YUU
 */
public class PluginSrcFile {
    private final String path;
    private String fileData = "";
    private long lastModified = 0;
    
    public PluginSrcFile (File file) throws IOException {
        path = file.getAbsolutePath();
        update(file);
    }
    
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
    
    public String getFileData() throws IOException {
        File file = new File(path);
        if (file.lastModified() > lastModified) {
            update(file);
        }
        return fileData;
    }
    
    private void update(File file) throws IOException {
        fileData = new String(FileAccessUtil.getByteData(file));
        lastModified = file.lastModified();
    }
}
