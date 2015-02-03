package jp.ac.teu.tlab.vimana.object.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import static jp.ac.teu.tlab.vimana.base.FileAccessUtil.getByteData;

/**
 *
 * @author Yuta YAMAGUCHI
 */
public class StaticFile {
    public static final String STATIC_PATH = "/public";
    private static final ConcurrentHashMap<String, StaticFile> fileMap = new ConcurrentHashMap<>();
    protected final String filePath;
    protected long timeStamp;
    protected byte[] fileData;
    
    protected StaticFile(String filePath) {
        this.filePath = filePath;
    }
    
    public static void setFile(String path, String filePath) {
        if (!fileMap.containsKey(path)) {
            fileMap.put(path, new StaticFile(filePath));
        }
    }
    
    public static void setFile(String path, byte[] fileData) {
        fileMap.put(path, new PseudoStaticFile(path, fileData));
    }
    
    public static StaticFile getStaticFile(String path) {
        return fileMap.get(path);
    }
    
    public static StaticFile getStaticFile(String path, String filePath) {
        
        setFile(path, filePath);
        
        return fileMap.get(path);
    }
    
    public File getFile()
            throws FileNotFoundException {
        
        File file = new File(filePath);
        
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException();
        }
        
        return new File(filePath);
    }
    
    public byte[] getFileData()
            throws FileNotFoundException, IOException {
        
        File file = new File(filePath);
        
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException();
        }
        
        if (timeStamp != file.lastModified()) {
            timeStamp = file.lastModified();
            fileData = getByteData(file);
        }
        
        return fileData;
    }
}