import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private String accountFile;
    private String inputDir;
    private String archiveDir;
    private String reportFile;

    public void loadConfig() throws IOException {
        Properties props = new Properties();
        String configPath = "config.properties";
        System.out.println("Загрузка файла: " + configPath);
        try (FileInputStream fs = new FileInputStream("config.properties")) {
            props.load(fs);
        }
        System.out.println("Загруженные параметры из config.properties: ");
        for (String key : props.stringPropertyNames()) {
            System.out.println(key + " = " + props.getProperty(key));
        }
        accountFile = props.getProperty("accountsFile");
        inputDir = props.getProperty("inputDir");
        archiveDir = props.getProperty("archiveDir");
        reportFile = props.getProperty("reportFile");
    }

    public String getAccountFile() {
        return accountFile;
    }

    public String getInputDir() {
        return inputDir;
    }

    public String getArchiveDir() {
        return archiveDir;
    }

    public String getReportFile() {
        return reportFile;
    }
}

