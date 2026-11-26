package com.javaexercise.management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * CSVファイルでユーザーを保存・読込するクラス。
 * CSVの書式に関する処理(区切り文字を含む値の扱いなど)はここに閉じ込める。
 */
public class CsvUserRepository implements UserRepositoryInterface {
    private static final String FILE_PATH = "users.csv";
    private static final String CSV_HEADER = "id,name,email";

    // CSVの書式
    private static final char DELIMITER = ',';
    private static final char QUOTE = '"';

    // CSVの列の数と位置
    private static final int COLUMN_COUNT = 3;
    private static final int INDEX_ID = 0;
    private static final int INDEX_NAME = 1;
    private static final int INDEX_EMAIL = 2;

    private final String filePath = FILE_PATH;

    @Override
    public void saveAll(List<User> users) {
        try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write(CSV_HEADER + System.lineSeparator());
            for (User user : users) {
                writer.write(toCsvLine(user) + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("ファイルの保存に失敗しました: " + e.getMessage());
        }
    }

    @Override
    public List<User> loadAll() {
        List<User> users = new ArrayList<>();

        // 初回起動などでファイルが無い場合は空のリストを返す
        File file = new File(filePath);
        if (!file.exists()) {
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine(); // ヘッダー行を読み飛ばす
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                User user = parseLine(line);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            System.out.println("ファイルの読み込みに失敗しました: " + e.getMessage());
        }
        return users;
    }

    // User → CSV1行
    private String toCsvLine(User user) {
        return user.getId() + String.valueOf(DELIMITER)
                + escape(user.getName()) + DELIMITER
                + escape(user.getEmail());
    }

    // 区切り文字や引用符を含む値は "..." で囲む(引用符は2つ重ねる)
    private String escape(String value) {
        if (value.indexOf(DELIMITER) < 0 && value.indexOf(QUOTE) < 0) {
            return value;
        }
        String escaped = value.replace(String.valueOf(QUOTE), "" + QUOTE + QUOTE);
        return QUOTE + escaped + QUOTE;
    }

    // CSV1行 → User。不正な行は null を返す
    private User parseLine(String line) {
        List<String> columns = splitLine(line);
        if (columns.size() != COLUMN_COUNT) {
            System.out.println("形式が不正な行を無視しました: " + line);
            return null;
        }
        try {
            int id = Integer.parseInt(columns.get(INDEX_ID).trim());
            return new User(id, columns.get(INDEX_NAME), columns.get(INDEX_EMAIL));
        } catch (IllegalArgumentException e) {
            // IDが数値でない、または名前・メールが空の場合
            System.out.println("内容が不正な行を無視しました: " + line);
            return null;
        }
    }

    // 引用符を考慮して1行を列に分割する
    private List<String> splitLine(String line) {
        List<String> columns = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == QUOTE) {
                    boolean escapedQuote = i + 1 < line.length() && line.charAt(i + 1) == QUOTE;
                    if (escapedQuote) {
                        current.append(QUOTE);
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else if (c == QUOTE) {
                inQuotes = true;
            } else if (c == DELIMITER) {
                columns.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        columns.add(current.toString());
        return columns;
    }
}