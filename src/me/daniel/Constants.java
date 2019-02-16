package me.daniel;
import java.io.File;

public class Constants {
    public static final String DATA_DIR = System.getProperty("user.home") + File.separator + ".local" + File.separator + "share" + File.separator + "beno";
    public static final String BINARY_DIR = DATA_DIR + File.separator + "bin";
    public static final String CONFIGS_DIR = DATA_DIR + File.separator + "configs";
    public static final String OUTPUT_DIR = DATA_DIR + File.separator + "output";
}
