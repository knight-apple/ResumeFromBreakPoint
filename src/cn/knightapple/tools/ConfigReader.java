package cn.knightapple.tools;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;
    private static Properties systemProperty;

    static {
        ini();
    }

    public static void ini() {
        try {
//            FileInputStream files = new FileInputStream(String.valueOf(SourceLoader.class.getResourceAsStream("cn/knightapple/resource/config.properties")));
            properties = new Properties();
            systemProperty = new Properties();
//            File systemFile = getSystemFile();
//            InputStream sis = new FileInputStream(systemFile);

            InputStream is = ClassLoader.getSystemResourceAsStream("cn/knightapple/config.properties");
            properties.load(is);
//            systemProperty.load(sis);

//            Properties systemProperty = System.getPropertie();
//            Iterator<Map.Entry<Object, Object>> iterator = systemProperty.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Map.Entry entry = iterator.next();
//                properties.setProperty((String) entry.getKey(), (String) entry.getValue());
//            }
//            iterator = properties.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Map.Entry entry = iterator.next();
//                systemProperty.setProperty((String) entry.getKey(), (String) entry.getValue());
//            }
//            store();
//            systemProperty.store(sos, "program.properties");
//            is.close();
//            sis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPropertie(String pro) {
        return properties.getProperty(pro);
    }

    public static Iterator<Map.Entry<Object, Object>> getProperties() {
        return properties.entrySet().iterator();
    }

//    public static void setProperties(String key, String value) {
//        properties.setProperty(key, value);
//        System.setProperties(properties);
//    }

//    public static void setSystemProperties(String key, String value) {
//        setProperties(key, value);
//        systemProperty.setProperty(key, value);
//    }

//    private static File getSystemFile() throws IOException {
//        String userDir = System.getProperty("user.home");
//        File propertiesDir = new File(userDir, ".chater");
//        if (!propertiesDir.exists()) {
//            propertiesDir.mkdir();
//        }
//        File propertiesFiles = new File(propertiesDir, "program.properties");
//        if (!propertiesFiles.exists()) {
//            propertiesFiles.createNewFile();
//        }
//        return propertiesFiles;
//    }

//    public static void reset() {
//        OutputStream temp = null;
//        try {
//            temp = new FileOutputStream(getSystemFile());
//            temp.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public static void store() throws IOException {
//        reset();
//        systemProperty.store(new FileOutputStream(getSystemFile()), "program.properties");
//    }
}