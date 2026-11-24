package com.javaexercise.management;

import java.util.List;
import java.util.Optional;

/**
 * ユーザーデータの保存・取得の窓口(永続化方法を隠蔽するインターフェース)
 */
public interface UserRepositoryInterface {

    /** ユーザーを追加する */
    void add(User user);

    /** 全ユーザーを登録順で返す */
    List<User> findAll();

    /** IDでユーザーを検索する(存在しなければ empty) */
    Optional<User> findById(int id);

    /** IDでユーザーを削除する。削除できたら true */
    boolean deleteById(int id);
}