import java.io.*;
import java.util.*;

public class BankTransactions {
    public static void main(String[] args) {
        new BankTransactions().run();
    }

    private void run() {
        try {
            ConfigLoader config = new ConfigLoader();
            config.loadConfig();
            System.out.println("Передача пути в LoadAccounts: " + config.getAccountFile());

            AccountManager accountManager = new AccountManager();
            System.out.println("Путь к конфигурации: " + config.getAccountFile());
            accountManager.loadAccounts(config.getAccountFile());

            StringBuilder repBuilder = new StringBuilder();
            FileProcessor proc = new FileProcessor(
                    config.getInputDir(),
                    config.getArchiveDir(),
                    config.getReportFile(),
                    repBuilder
            );

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Введите 1 для парсинга, 2 для просмотра отчета, 0 для выхода");
                String input = scanner.nextLine();
                if (input.equals("1")) {
                    proc.parseFiles(accountManager);
                } else if (input.equals("2")) {
                    File reportFile = new File(config.getReportFile());
                    if (reportFile.exists()) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(reportFile))) {
                            String line;
                            System.out.println("Отчет");
                            while ((line = reader.readLine()) != null) {
                                System.out.println(line);
                            }
                            System.out.println("Конец отчета");
                        }
                    } else {
                        System.out.println("Отчет не сформирован. Выполните обработку файла, выберите пункт 1");
                    }
                } else if (input.equals("0")) {
                    accountManager.saveAccounts();
                    System.out.println("Вы вышли из программы");
                    break;
                } else {
                    System.out.println("Введено неверное значение");
                }
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}





