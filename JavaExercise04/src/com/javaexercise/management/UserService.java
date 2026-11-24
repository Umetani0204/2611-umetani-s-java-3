package com.javaexercise.management;

import java.util.List;
import java.util.Optional;

/**
 * ユーザー管理の業務ロジック(入力チェック・重複チェック)を担当。
 * 保存方法は UserRepositoryInterface に任せるので、CSV以外にも差し替え可能。
 */
public class UserService {
    private final UserRepositoryInterface repository;

    public UserService(UserRepositoryInterface repository) {
        this.repository = repository;
    }

    /**
     * ユーザーを追加する。
     * @return 追加できたら true、IDが重複していたら false
     * @throws IllegalArgumentException 名前・メールが空、またはカンマを含む場合
     */
    public boolean addUser(int id, String name, String email) {
        validate("名前", name);
        validate("メールアドレス", email);
        if (repository.findById(id).isPresent()) {
            return false;
        }
        repository.add(new User(id, name.trim(), email.trim()));
        return true;
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public Optional<User> findUser(int id) {
        return repository.findById(id);
    }

    public boolean deleteUser(int id) {
        return repository.deleteById(id);
    }

    // CSVが壊れないよう、空文字とカンマを禁止
    private void validate(String label, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + "は空にできません。");
        }
        if (value.contains(",")) {
            throw new IllegalArgumentException(label + "にカンマ(,)は使用できません。");
        }
    }
}
