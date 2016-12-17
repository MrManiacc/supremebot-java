package util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Ahmed on 11/10/2016.
 */
public class Utils {

    public static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {

        return (OS.indexOf("win") >= 0);

    }

    public static boolean isMac() {

        return (OS.indexOf("mac") >= 0);

    }

    public static WebDriver preparePhantomJSDriver(){
        if (isWindows()){
            DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
            capabilities.setCapability("phantomjs.binary.path", "phantomjs.exe");
            try {
                return new PhantomJSDriver(capabilities);
            } catch (Exception e) {
                // TODO: 10/6/2016 Remind myself what exception this can throw and report it accordingly.
                e.printStackTrace();
            }
        }
        else if (isMac()){
            DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
            capabilities.setCapability("phantomjs.binary.path", "phantomjsOSX");
            try {
                return new PhantomJSDriver(capabilities);
            } catch (Exception e) {
                // TODO: 10/6/2016 Remind myself what exception this can throw and report it accordingly.
                e.printStackTrace();
            }
        }
        else {
            // TODO: 10/6/2016 Throw error and/or add support for Linux-32, Linux-64
        }

        return null;
    }

    static String readFile(String path, Charset encoding) {
        byte[] encoded = null;
        try {
            encoded = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(encoded, encoding);
    }

    public static void copyFileUsingFileStreams(File source, File dest)
            throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            input.close();
            output.close();
        }
    }

}
