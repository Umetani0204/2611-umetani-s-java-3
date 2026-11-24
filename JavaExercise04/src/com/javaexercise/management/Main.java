package com.javaexercise.management;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * コンソールの入出力(表示と入力受付)だけを担当するクラス
 */
public class Main {
    private static final String CSV_PATH = "users.csv";

    public static void main(String[] args) {
        UserRepositoryInterface repository = new CsvUserRepository(CSV_PATH);
        UserService service = new UserService(repository);

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                showMenu();
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        addUser(scanner, service);
                        break;
                    case "2":
                        listUsers(service);
                        break;
                    case "3":
                        searchUser(scanner, service);
                        break;
                    case "4":
                        deleteUser(scanner, service);
                        break;
                    case "5":
                    case "終了":
                        System.out.println("終了します。");
                        return;
                    default:
                        System.out.println("1〜5の番号か「終了」を入力してください。");
                }
                System.out.println();
            }
        }
    }

    private static void showMenu() {
        System.out.println("==== ユーザー管理システム ====");
        System.out.println("1. ユーザーを追加");
        System.out.println("2. ユーザー一覧を表示");
        System.out.println("3. ユーザーをIDで検索");
        System.out.println("4. ユーザーをIDで削除");
        System.out.println("5. 終了");
        System.out.print("選択してください: ");
    }

    private static void addUser(Scanner scanner, UserService service) {
        System.out.println();
        System.out.println("--- ユーザー追加 ---");
        Integer id = readId(scanner);
        if (id == null) {
            return;
        }
        System.out.print("名前を入力してください: ");
        String name = scanner.nextLine();
        System.out.print("メールアドレスを入力してください: ");
        String email = scanner.nextLine();

        try {
            if (service.addUser(id, name, email)) {
                System.out.println("ユーザーを追加しました。");
            } else {
                System.out.println("そのIDは既に登録されています。");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void listUsers(UserService service) {
        System.out.println();
        System.out.println("--- 登録ユーザー一覧 ---");
        List<User> users = service.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("登録されているユーザーはいません。");
            return;
        }
        for (User user : users) {
            System.out.println(user);
        }
    }

    private static void searchUser(Scanner scanner, UserService service) {
        System.out.println();
        System.out.println("--- ユーザー検索 ---");
        Integer id = readId(scanner);
        if (id == null) {
            return;
        }
        Optional<User> user = service.findUser(id);
        if (user.isPresent()) {
            System.out.println(user.get());
        } else {
            System.out.println("該当するユーザーが見つかりません。");
        }
    }

    private static void deleteUser(Scanner scanner, UserService service) {
        System.out.println();
        System.out.println("--- ユーザー削除 ---");
        Integer id = readId(scanner);
        if (id == null) {
            return;
        }
        if (service.deleteUser(id)) {
            System.out.println("ユーザーを削除しました。");
        } else {
            System.out.println("該当するユーザーが見つかりません。");
        }
    }

    // 数値以外が入力されたら null を返す
    private static Integer readId(Scanner scanner) {
        System.out.print("IDを入力してください: ");
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("IDは数字で入力してください。");
            return null;
        }
    }
}