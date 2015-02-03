package jp.ac.teu.tlab.vimana.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.activation.MimetypesFileTypeMap;

/**
 *
 * @author Yuta YAMAGUCHI
 */
public class FileAccessUtil {
    private static final MimetypesFileTypeMap mimeTypeMap = new MimetypesFileTypeMap();
    
    public static void setMinetypes() {
        mimeTypeMap.addMimeTypes("text/plain txt");
        mimeTypeMap.addMimeTypes("text/html html htm xhtml");
        mimeTypeMap.addMimeTypes("text/javascript js");
        mimeTypeMap.addMimeTypes("text/css css");
        
        mimeTypeMap.addMimeTypes("image/png png");
        mimeTypeMap.addMimeTypes("image/jpeg jpeg");
        mimeTypeMap.addMimeTypes("image/jpeg jpg");
        mimeTypeMap.addMimeTypes("image/gif gif");
        mimeTypeMap.addMimeTypes("image/x-icon ico");
        
        mimeTypeMap.addMimeTypes("application/xml xml");
        mimeTypeMap.addMimeTypes("application/json json");
        mimeTypeMap.addMimeTypes("application/pdf pdf");
    }
    
    public static String getContentType(File file) {
        return mimeTypeMap.getContentType(file);
    }

    public static byte[] getByteData(File file)
            throws FileNotFoundException, IOException {
        
        FileInputStream input = new FileInputStream(file);
        byte[] buffer = new byte[input.available()];
        input.read(buffer);
        
        return buffer;
    }
}