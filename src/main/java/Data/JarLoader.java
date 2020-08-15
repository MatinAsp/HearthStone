package Data;

import java.net.URL;
import java.net.URLClassLoader;

public class JarLoader {
    public static Class loadClass(String className){
        Class ans = null;
        String pathToJar = GameConstants.getInstance().getString("pathToJar");
        try {
            URL[] urls = {new URL("jar:file:" + pathToJar + "!/")};
            URLClassLoader classLoader = URLClassLoader.newInstance(urls);
            ans = classLoader.loadClass(className);
        } catch (Exception e){
            e.printStackTrace();
        }
        return ans;
    }
}
