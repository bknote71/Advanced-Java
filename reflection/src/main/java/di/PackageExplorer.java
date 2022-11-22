package di;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class PackageExplorer {

    static final ClassLoader classLoader = ClassLoader.getSystemClassLoader(); // Application Class Loader

    public static Class[] getClassesInPackage(String packageName) throws URISyntaxException {
        File directory = getPackageDirectory(packageName);
        if (!directory.exists()) {
            System.out.println("존재하지 않는 디렉토리이다.");
            return null;
        }
        return getClassesInPackageDirectory(packageName, directory);
    }

    private static Class[] getClassesInPackageDirectory(String packageName, File directory) throws URISyntaxException {
        List<Class> classes = new ArrayList<>();

        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                classes.add(getClass(file.getName(), packageName));
            } else if (file.isDirectory()) {
                final Class[] classesInSubPackage = getClassesInPackage(packageName + "." + file.getName());
                Collections.addAll(classes, classesInSubPackage);
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private static Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }

    private static File getPackageDirectory(String packageName) throws URISyntaxException {
        final URL resource = classLoader.getResource(packageName.replace('.', '/'));
        final File file = new File(resource.toURI());
        return file;
    }

    public static Set<Class> getClassesInPackage2(String packageName) {
        InputStream stream = classLoader
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    public static void main(String[] args) throws URISyntaxException {
        final Class[] dis = PackageExplorer.getClassesInPackage("di");
        Arrays.stream(dis).forEach(System.out::println);
    }

}
