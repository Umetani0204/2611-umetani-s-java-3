package com.javaexercise.management;

import java.util.List;

/**
 * ユーザーデータの保存・読込を定義するインターフェース
 */
public interface UserRepositoryInterface {

    /** 全ユーザーを保存する */
    void saveAll(List<User> users);

    /** 全ユーザーを読み込む(読み込めない場合は空のリスト) */
    List<User> loadAll();
}