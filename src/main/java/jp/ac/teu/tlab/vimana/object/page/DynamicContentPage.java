package jp.ac.teu.tlab.vimana.object.page;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import jp.ac.teu.tlab.vimana.object.Content;
import static jp.ac.teu.tlab.vimana.base.Engine.dynamicContentPageMustache;

/**
 *
 * @author Yuta YAMAGUCHI
 */
public class DynamicContentPage extends ContentPage {
    private final String SYMBOLIC_PATH = "/public/symbolic";
    
    public DynamicContentPage(String path, String filePath)
            throws IOException {

        super.content = new Content(path, SYMBOLIC_PATH + path, filePath);
        
        StringWriter stringWriter = new StringWriter();
        dynamicContentPageMustache.execute(stringWriter, super.content).flush();
        
        super.page = stringWriter.toString();
    }
    
    @Override
    public void executeFlash(PrintWriter writer)
            throws IOException {
        
        dynamicContentPageMustache.execute(writer, super.content).flush();
    }
}
