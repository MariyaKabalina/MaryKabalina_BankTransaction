import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileProcessor {
    private String inputDir;
    private String archiveDir;
    private String reportFilePath;
    private StringBuilder repBuilder;

    public FileProcessor(String inputDir, String archiveDir, String reportFilePath, StringBuilder repBuilder) {
        this.inputDir = inputDir;
        this.archiveDir = archiveDir;
        this.reportFilePath = reportFilePath;
        this.repBuilder = repBuilder;
    }

    public void parseFiles(AccountManager accountManager) throws IOException {
        repBuilder.setLength(0);
        File inputFolder = new File(inputDir);
        File[] files = inputFolder.listFiles();
        if (files == null) {
            System.out.println("Файлы для обработки не обнаружены" + inputDir);
            return;
        }

        Files.createDirectories(Paths.get(archiveDir));

        for (File file : files) {
            System.out.println("Обработка файла: " + file.getName());
            try (BufferedReader bfr = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = bfr.readLine()) != null) {
                    if (line.startsWith("№ счета с которого совершается перевод: ")) {
                        String fromAccount = line.replace("№ счета с которого совершается перевод: ", "").trim();
                        String toAccountLine = bfr.readLine();
                        if (toAccountLine == null || !toAccountLine.startsWith("№ счета на который совершается перевод: ")) {
                            System.out.println("Некорректный формат строки в файле: " + file.getName());
                            break;
                        }
                        String toAccount = toAccountLine.replace("№ счета на который совершается перевод: ", "").trim();
                        String amountLine = bfr.readLine();
                        if (amountLine == null || !amountLine.startsWith("Сумма перевода: ")) {
                            System.out.println("Некорректный формат строки в файле: " + file.getName());
                            break;
                        }
                        String valueStr = amountLine.replace("Сумма перевода: ", "").trim();

                        double value;
                        try {
                            value = Double.parseDouble(valueStr);
                        } catch (NumberFormatException e) {
                            System.out.println("Некорректное значение: " + valueStr + "в файле: " + file.getName());
                            continue;
                        }

                        accountManager.processTransfer(fromAccount, toAccount, value);

                        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        repBuilder.append(timestamp)
                                .append(" | ")
                                .append(file.getName())
                                .append(" | Перевод c: ").append(fromAccount)
                                .append(" на: ").append(toAccount).append(" ").append(value)
                                .append(" | успешно обработан\n");
                    } else {
                        System.out.println("Некорректная строка: " + line);
                    }
                }
            } catch (IOException e) {
                System.out.println("Ошибка при чтении файла: " + file.getName());
            }

            Files.move(file.toPath(), Paths.get(archiveDir, file.getName()), StandardCopyOption.REPLACE_EXISTING);
        }
        try {
            Files.write(Paths.get(reportFilePath), repBuilder.toString().getBytes());
            System.out.println("Отчет успешно сохранен в " + reportFilePath);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении отчета: " + e.getMessage());
        }
    }

}
