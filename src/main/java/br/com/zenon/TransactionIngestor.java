package br.com.zenon;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TransactionIngestor {

    public List<Transaction> read(String fileName) {
        Path path = Path.of(fileName);
        try {
            List<String> lines = Files.readAllLines(path);
            return lines.stream()
                    .skip(1)
                    .limit(1000)
                    .map(this::parseTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }catch (Exception e){
            throw new RuntimeException("Erro ao ler arquivo" + fileName, e);
        }
    }

    private Optional<Transaction> parseTransaction(String line) {
        try {
            String[] chunks = line.split(",");
            int step = Integer.parseInt(chunks[0]);
            TransactionType transactionType = TransactionType.valueOf(chunks[1]);

            if(chunks[2] == null || chunks[2].isEmpty()) throw new IllegalArgumentException("O valor do amount nao pode ser nulo nem vazio");
            BigDecimal amount = new BigDecimal(chunks[2]);


            if(chunks[4] == null || chunks[4].isEmpty() || chunks[7] == null || chunks[7].isEmpty()) {
                throw new IllegalArgumentException("O valor do oldBalance nao pode ser nulo nem vazio");
            }
            if(chunks[5] == null || chunks[5].isEmpty() || chunks[8] == null || chunks[8].isEmpty()) {
                throw new IllegalArgumentException("O valor do newBalance nao pode ser nulo nem vazio");
            }

            var origin = new TransactionCustomer(chunks[3], new BigDecimal(chunks[4]), new BigDecimal(chunks[5]));
            var recipient = new TransactionCustomer(chunks[6], new BigDecimal(chunks[7]), new BigDecimal(chunks[8]));

            boolean isFraud = "1".equals(chunks[9]);
            boolean isFlaggedFraud = "1".equals(chunks[10]);

            IO.println(transactionType);
            return Optional.of(new Transaction(step, transactionType, amount, origin, recipient, isFraud, isFlaggedFraud));
        } catch (Exception e) {
            System.err.println("Erro ao fazer parse: " + line + " | " + e);
        }
        return Optional.empty();
    }
}
