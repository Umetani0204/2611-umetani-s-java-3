package com.javaexercise.management;

/**
 * ユーザーを表すモデルクラス。
 * 「名前・メールアドレスは空にできない」というモデル自身のルールをここで保証する。
 */
public class User {
    private final int id;
    private final String name;
    private final String email;

    /**
     * @throws IllegalArgumentException 名前またはメールアドレスが null か空の場合
     */
    public User(int id, String name, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("名前は必須です。");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("メールアドレスは必須です。");
        }
        this.id = id;
        this.name = name.trim();
        this.email = email.trim();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | 名前: " + name + " | メール: " + email;
    }
}