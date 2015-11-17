package gbemu.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Adolph C.
 */
public class GeneralDebug {
    private static FileWriter writer;
    private static boolean debugging = false;

    public static void init(String file) {
        init(new File(file));
    }

    public static void init(File file) {
        try {
            File parentFile = file.getParentFile();
            if(parentFile != null) {
                //noinspection ResultOfMethodCallIgnored
                parentFile.mkdirs();
            }
            writer = new FileWriter(file);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if(writer != null) writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
            debugging = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void print(String s) {
        if (!debugging) return;
        try {
            if(writer != null) writer.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void println(String s) {
        if (!debugging) return;
        print(s + "\n");
    }

    public static void printf(String s, Object...args) {
        if (!debugging) return;
        print(String.format(s, args));
    }

    public static void printfn(String s, Object...args) {
        if (!debugging) return;
        println(String.format(s, args));
    }
}
