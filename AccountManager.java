import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountManager {
    private Map<String, Double> accounts = new HashMap<>();
    private String accountsFilePath;

    public void loadAccounts(String accountFilePath) throws IOException {
        this.accountsFilePath = accountFilePath;
        Path path = Paths.get(accountFilePath);
        if (!Files.exists(path)) {
            System.out.println("Файл не найден " + accountFilePath);
            return;
        }
        List<String> lines = Files.readAllLines(path);
        for (int i = 0; i < lines.size(); i += 2) {
            if (i + 1 >= lines.size()) break;
            String line = lines.get(i).trim();
            String balanceLine = lines.get(i + 1).trim();
            String[] parts = line.split(":");
            if (parts.length == 2) {
                String accountNum = parts[0];
                Double balance = Double.parseDouble(balanceLine);
                accounts.put(accountNum, balance);
            }
        }
    }

    public void saveAccounts() throws IOException {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Double> entry : accounts.entrySet()) {
            lines.add(entry.getKey() + ":" + entry.getValue());
        }
        Files.write(Paths.get(accountsFilePath), lines);
    }

    public void processTransfer(String fromAccount, String toAccount, Double value) {
        Double fromBalance = accounts.getOrDefault(fromAccount, 0.0);
        if (fromBalance < value) {
            //System.out.println("Недостаточно средств для перевода: " + fromAccount);
            return;
        }
        accounts.put(fromAccount, accounts.get(fromAccount) - value);
        accounts.put(toAccount, accounts.get(toAccount) + value);
    }

    public Map<String, Double> getAccounts() {
        return accounts;
    }
}
