package jp.ac.teu.tlab.vimana.object;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import static jp.ac.teu.tlab.vimana.base.Controller.getRealPath;
import static jp.ac.teu.tlab.vimana.base.FileAccessUtil.getByteData;
import static jp.ac.teu.tlab.vimana.object.file.StaticFile.setFile;

/**
 *
 * @author Yuta YAMAGUCHI
 */
public class Content {
    private static final String CONTENTS_DIRECTORY = "/views";
    private static final String[] EXTEND_FILE_NAMES = {".vimana.html", "/index.vimana.html"};
    public String id = ""; // Mustache's Access field
    public String content = ""; // Mustache's Access field
    public String src = ""; // Mustache's Access field
    
    // static content
    public Content(String path) throws IOException {
        
        this.id = path.replaceAll("/", "-");
        
        File file = getFile(path);
        
        if (file == null) {
            throw new FileNotFoundException();
        }
        
        this.content = getFileString(file);
    }
    
    // dynamic content
    public Content(String path, String src, String filePath)
            throws FileNotFoundException, IOException {
        
        this.id = path.replaceAll("/", "-");
        this.src = src;
        
        setFile(src, filePath);
    }
    
    @Override
    public String toString() {
        return content;
    }
    
    private File getFile(String path) {
        File file = new File(path);
        
        if (file.exists() && file.isFile()) {
            return file;
        } else {
            for (String extendFileName : EXTEND_FILE_NAMES) {
                file = new File(getRealPath(CONTENTS_DIRECTORY + path) + extendFileName);
                if (file.exists() && file.isFile()) {
                    return file;
                }
            }
        }
        
        return null;
    }
    
    private String getFileString(File file)
            throws FileNotFoundException, IOException {
        
        byte[] data = getByteData(file);
        
        return new String(data);
    }
}
