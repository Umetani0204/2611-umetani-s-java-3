package com.javaexercise.management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * CSVファイルでユーザーを永続化するリポジトリ。
 * 起動時に読み込み、追加・削除のたびに自動保存する。
 */
public class CsvUserRepository implements UserRepositoryInterface {
    private static final String HEADER = "id,name,email";

    private final File file;
    // 登録順を保つため LinkedHashMap を使用
    private final Map<Integer, User> users = new LinkedHashMap<>();

    public CsvUserRepository(String filePath) {
        this.file = new File(filePath);
        load();
    }

    @Override
    public void add(User user) {
        users.put(user.getId(), user);
        save();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean deleteById(int id) {
        User removed = users.remove(id);
        if (removed == null) {
            return false;
        }
        save();
        return true;
    }

    // ファイルが無ければ何もしない(初回起動)
    private void load() {
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // ヘッダー行を読み飛ばす
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length != 3) {
                    continue; // 形式が不正な行は無視
                }
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    users.put(id, new User(id, parts[1].trim(), parts[2].trim()));
                } catch (NumberFormatException e) {
                    // IDが数値でない行は無視
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("CSVの読み込みに失敗しました: " + file.getPath(), e);
        }
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(HEADER + System.lineSeparator());
            for (User user : users.values()) {
                writer.write(user.getId() + "," + user.getName() + "," + user.getEmail()
                        + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new UncheckedIOException("CSVの保存に失敗しました: " + file.getPath(), e);
        }
    }
}