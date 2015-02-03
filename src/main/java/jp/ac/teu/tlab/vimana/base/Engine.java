package jp.ac.teu.tlab.vimana.base;

import jp.ac.teu.tlab.vimana.object.Config;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jp.ac.teu.tlab.vimana.annotation.GET;
import jp.ac.teu.tlab.vimana.annotation.POST;
import jp.ac.teu.tlab.vimana.annotation.Path;
import static jp.ac.teu.tlab.vimana.base.Controller.getRealPath;
import static jp.ac.teu.tlab.vimana.base.Controller.setHttpServlet;
import static jp.ac.teu.tlab.vimana.base.FileAccessUtil.getContentType;
import static jp.ac.teu.tlab.vimana.base.FileAccessUtil.setMinetypes;
import jp.ac.teu.tlab.vimana.object.Navigation;
import jp.ac.teu.tlab.vimana.object.page.ContentPage;
import static jp.ac.teu.tlab.vimana.object.file.PseudoStaticFile.PSEUDO_PATH;
import jp.ac.teu.tlab.vimana.object.file.StaticFile;
import static jp.ac.teu.tlab.vimana.object.file.StaticFile.getStaticFile;
import static jp.ac.teu.tlab.vimana.object.file.StaticFile.STATIC_PATH;
import net.sf.json.JSONArray;

/**
 *
 * @author Yuta YAMAGUCHI
 */
@WebServlet(name = "Controller", urlPatterns = {"/*"})
public class Engine extends HttpServlet {
    private final String CONFIG_FILE = "/WEB-INF/config.json";
    private Config config;
    
    private Mustache templateMustache;
    public static Mustache staticContentPageMustache;
    public static Mustache dynamicContentPageMustache;
    
    private GlobalSettings globalSettings;
    private List<Controller> controllersList;
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        setHttpServlet(this);
        setMinetypes();
        
        controllersList = new ArrayList<>();
        
        try {
            config = new Config(getRealPath(CONFIG_FILE));
            
            MustacheFactory mustacheFactory = new DefaultMustacheFactory();
            templateMustache = mustacheFactory.compile(Controller.getRealPath(config.getTemplateLayoutPath()));
            staticContentPageMustache = mustacheFactory.compile(Controller.getRealPath(config.getStaticContentPageLayoutPath()));
            dynamicContentPageMustache = mustacheFactory.compile(Controller.getRealPath(config.getDynamicContentPageLayoutPath()));

            String globalSettingClassName = config.getGlobalClassName();
            if (!globalSettingClassName.isEmpty()) {
                Class globalSettingClass = Class.forName(globalSettingClassName);
                globalSettings = (GlobalSettings) globalSettingClass.newInstance();
            } else {
                globalSettings = new GlobalSettings() {};
            }
            
            JSONArray controllerClassNames = config.getControllerClassNames();
            for (int i = 0; i < controllerClassNames.size(); i++) {
                Class controllerClass = Class.forName(controllerClassNames.getString(i));
                controllersList.add((Controller) controllerClass.newInstance());
            }
            
            PluginLoader.loadPlugin(config.getPluginLoadPath());
            
            globalSettings.onStart();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |  IOException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding(config.getCharset());
        response.setContentType("text/html; charset=" + config.getCharset());
        
        String hpjaxHeader = request.getHeader("X-HPJAX");
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("")) {
            pathInfo = "/";
        }
        
        try {
            if (isPseudoStaticFile(pathInfo)) {
                outPutFileData(response, getStaticFile(pathInfo));
                return;
            }
            
            if (isStaticContents(pathInfo)) {
                outPutFileData(response, getStaticFile(pathInfo, getRealPath(pathInfo)));
                return;
            }
            
            for (Controller controller : controllersList) {
                Method[] methods = controller.getClass().getDeclaredMethods();
                
                for (Method method : methods) {
                    if(!hasGetAnnotation(method)) {
                        continue;
                    }

                    method.setAccessible(true);

                    Path path = method.getAnnotation(Path.class);
                    String[] values = path.value();
                    for (String value : values) {
                        if (isTargetPath(value, pathInfo)) {
                            if (method.getGenericReturnType() == Navigation.class) {
                                boolean isHpjax = false;
                                if (hpjaxHeader != null) {
                                    isHpjax = (hpjaxHeader.equals("true"));
                                }
                                
                                Navigation navigation = executeMethodGetNavigation(controller, method, request, response);
                                ContentPage page = navigation.getPage(pathInfo);

                                if (!isHpjax) {
                                    templateMustache.execute(response.getWriter(), page).flush();
                                } else {
                                    page.executeFlash(response.getWriter());
                                }
                                return;
                            } else {
                                executeMethod(controller, method, request, response);
                                return;
                            }
                        }
                    }
                }
            }

            globalSettings.onNotFoundError(response);
        } catch (FileNotFoundException ex) {
            globalSettings.onNotFoundError(response);
        } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException | IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            globalSettings.onError(response, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        request.setCharacterEncoding(config.getCharset());
        response.setContentType("text/html; charset=" + config.getCharset());
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }
        
        try {
            for (Controller controller : controllersList) {
                Method[] methods = controller.getClass().getDeclaredMethods();
                for (Method method : methods) {
                    if(!hasPostAnnotation(method)) {
                        continue;
                    }

                    method.setAccessible(true);

                    Path path = method.getAnnotation(Path.class);
                    String[] values = path.value();
                    for (String value : values) {
                        if (isTargetPath(value, pathInfo)) {
                            executeMethod(controller, method, request, response);
                            return;
                        }
                    }
                }
            }
            
            globalSettings.onBadRequestError(response);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
            globalSettings.onError(response, ex);
        }
    }
    
    private boolean isPseudoStaticFile(String path) {
        return path.startsWith(PSEUDO_PATH);
    }
    
    private boolean isStaticContents(String path) {
        return path.startsWith(STATIC_PATH);
    }
    
    private boolean hasGetAnnotation(Method method) {
        return method.getAnnotation(GET.class) != null;
    }
    
    private boolean hasPostAnnotation(Method method) {
        return method.getAnnotation(POST.class) != null;
    }
    
    private boolean isTargetPath(String annotationValue, String pathInfo) {
        if (annotationValue.endsWith("/*")) {
            annotationValue = annotationValue.substring(0, annotationValue.length()-2);
            if(pathInfo.indexOf(annotationValue) == 0){
                return true;
            }
        } else {
            if(annotationValue.equals(pathInfo)) {
                return true;
            }
        }
        
        return false;
    }
    
    private void outPutFileData(HttpServletResponse response, StaticFile staticFile)
            throws FileNotFoundException, IOException {
        
        if (staticFile == null) {
            throw new FileNotFoundException();
        }
        
        byte[] data = staticFile.getFileData();
        OutputStream outputStream = null;
        
        response.setContentType(getContentType(staticFile.getFile()) + "; charset=UTF-8");
        response.setHeader("Content-Disposition", "filename=\"" + staticFile.getFile().getName() + "\"");
        
        try {
            outputStream = response.getOutputStream();
            outputStream.write(data);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
    
    private Navigation executeMethodGetNavigation(Controller controller, Method method, HttpServletRequest request, HttpServletResponse response)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        
        Class<? extends Object>[] methodArgs = method.getParameterTypes();
        List<Object> args = new ArrayList<>();
        
        for (Class methodArg : methodArgs) {
            if (methodArg == HttpServletRequest.class) {
                args.add(request);
            } else if (methodArg == HttpServletResponse.class) {
                args.add(response);
            }
        }
        
        return (Navigation) method.invoke(controller, args.toArray(new Object[args.size()]));
    }

    private void executeMethod(Controller controller, Method method, HttpServletRequest request, HttpServletResponse response)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        
        Class<? extends Object>[] methodArgs = method.getParameterTypes();
        List<Object> args = new ArrayList<>();
        
        for (Class methodArg : methodArgs) {
            if (methodArg == HttpServletRequest.class) {
                args.add(request);
            } else if (methodArg == HttpServletResponse.class) {
                args.add(response);
            }
        }
        
        method.invoke(controller, args.toArray(new Object[args.size()]));
    }
}