package com.javaexercise.management;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * コンソールの入出力とメニュー制御を担当するクラス
 */
public class Main {
    // メニューの選択肢
    private static final String MENU_ADD = "1";
    private static final String MENU_LIST = "2";
    private static final String MENU_SEARCH = "3";
    private static final String MENU_DELETE = "4";
    private static final String MENU_EXIT = "5";
    private static final String EXIT_COMMAND = "終了";

    public static void main(String[] args) {
        UserRepositoryInterface repository = new CsvUserRepository();
        UserService service = new UserService(repository); // 起動時にCSV読込

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                showMenu();
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case MENU_ADD:
                        addUser(scanner, service);
                        break;
                    case MENU_LIST:
                        listUsers(service);
                        break;
                    case MENU_SEARCH:
                        searchUser(scanner, service);
                        break;
                    case MENU_DELETE:
                        deleteUser(scanner, service);
                        break;
                    case MENU_EXIT:
                    case EXIT_COMMAND:
                        running = false;
                        break;
                    default:
                        System.out.println("メニューの番号か「" + EXIT_COMMAND + "」を入力してください。");
                }
                System.out.println();
            }
        }

        service.saveData(); // 終了時にCSV保存
        System.out.println("終了します。");
    }

    private static void showMenu() {
        System.out.println("==== ユーザー管理システム ====");
        System.out.println(MENU_ADD + ". ユーザーを追加");
        System.out.println(MENU_LIST + ". ユーザー一覧を表示");
        System.out.println(MENU_SEARCH + ". ユーザーをIDで検索");
        System.out.println(MENU_DELETE + ". ユーザーをIDで削除");
        System.out.println(MENU_EXIT + ". 終了");
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

        User user;
        try {
            user = new User(id, name, email);
        } catch (IllegalArgumentException e) {
            System.out.println("名前とメールアドレスを入力してください。");
            return;
        }

        try {
            service.addUser(user);
            System.out.println("ユーザーを追加しました。");
        } catch (IllegalArgumentException e) {
            System.out.println("このIDは使用できません。別のIDを入力してください。");
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
        Optional<User> user = service.findUserById(id);
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
        if (service.deleteUserById(id)) {
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