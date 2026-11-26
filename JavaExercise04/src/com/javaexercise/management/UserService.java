package com.javaexercise.management;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ユーザー管理のビジネスルール(IDの重複禁止など)を担当するクラス
 */
public class UserService {
    private final List<User> users;
    private final UserRepositoryInterface repository;

    // 起動時にリポジトリからデータを読み込む
    public UserService(UserRepositoryInterface repository) {
        this.repository = repository;
        this.users = new ArrayList<>(repository.loadAll());
    }

    /**
     * ユーザーを追加する。
     * @throws IllegalArgumentException 同じIDのユーザーが既に存在する場合
     */
    public void addUser(User user) {
        if (findUserById(user.getId()).isPresent()) {
            throw new IllegalArgumentException("IDが重複しています: " + user.getId());
        }
        users.add(user);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public Optional<User> findUserById(int id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst();
    }

    /** 削除できたら true、該当ユーザーがいなければ false */
    public boolean deleteUserById(int id) {
        return users.removeIf(user -> user.getId() == id);
    }

    // 終了時にリポジトリへ保存する
    public void saveData() {
        repository.saveAll(users);
    }
}