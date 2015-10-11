package gbemu.util;

import gbemu.cpu.z80.Z80Registers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Adolph C.
 */
public class Utils {
	public static <K,V> Map<K,V> map(Object...args) {
		if(args.length % 2 != 0)
			throw new IllegalArgumentException("Length of arguments must be a multiple of 2");
		else if(args.length == 0)
			return Collections.emptyMap();
		Map<K, V> ret = new HashMap<>(args.length / 2);
		for(int idx = 0; idx < args.length; idx+=2) {
			//noinspection unchecked
			ret.put((K) args[idx], (V) args[idx + 1]);
		}
		return ret;
	}

	public static boolean inRange(int a, int lowest, int highest) {
		return a >= lowest && a <= highest;
	}

	public static String readResourceIntoString(String resource) {
		return readResourceIntoString(Utils.class, resource);
	}

	public static String readResourceIntoString(Class<?> parent, String resource) {
		try(BufferedReader reader =
					new BufferedReader(
							new InputStreamReader(parent.getResourceAsStream(resource)))) {
			return reader.lines().collect(Collectors.joining("\n"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String readFileIntoString(String file) {
		return readFileIntoString(new File(file));
	}

	public static String readFileIntoString(File file) {
		return readFileIntoString(file.toPath());
	}

	public static String readFileIntoString(Path path) {
		try {
			return Files.lines(path).collect(Collectors.joining("\n"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void dumpRegisters(Z80Registers r) {
		System.out.println("--- Register Dump ---");
		System.out.printf("AF = 0x%04x (%d)\n", r.getAF(), r.getAF());
		System.out.printf("\tA = 0x%02x (%d)\n", r.getA(), r.getA());
		System.out.printf("\tF = 0x%02x (%d)\n", r.getF(), r.getF());
		System.out.printf("\t\tzf = %b\n", r.getZFlag());
		System.out.printf("\t\tn = %b\n", r.getNFlag());
		System.out.printf("\t\th = %b\n", r.getHFlag());
		System.out.printf("\t\tcy = %b\n", r.getCFlag());
		System.out.printf("BC = 0x%04x (%d)\n", r.getBC(), r.getBC());
		System.out.printf("\tB = 0x%02x (%d)\n", r.getB(), r.getB());
		System.out.printf("\tC = 0x%02x (%d)\n", r.getC(), r.getC());
		System.out.printf("DE = 0x%04x (%d)\n", r.getDE(), r.getDE());
		System.out.printf("\tD = 0x%02x (%d)\n", r.getD(), r.getD());
		System.out.printf("\tE = 0x%02x (%d)\n", r.getE(), r.getE());
		System.out.printf("HL = 0x%04x (%d)\n", r.getHL(), r.getHL());
		System.out.printf("\tH = 0x%02x (%d)\n", r.getH(), r.getH());
		System.out.printf("\tL = 0x%02x (%d)\n", r.getL(), r.getL());
		System.out.println("---------------------");
	}
}
