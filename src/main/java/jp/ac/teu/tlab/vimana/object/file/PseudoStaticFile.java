package jp.ac.teu.tlab.vimana.object.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author YUU
 */
public class PseudoStaticFile extends StaticFile {
    public static final String PSEUDO_PATH = "/public/pseudo";
    
    public PseudoStaticFile(String filePath, byte[] fileData) {
        super(filePath);
        super.fileData = fileData;
    }
    
    @Override
    public File getFile() {
        return new File(filePath);
    }
    
    @Override
    public byte[] getFileData()
            throws FileNotFoundException, IOException {
        
        return fileData;
    }
}