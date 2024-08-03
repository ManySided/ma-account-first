package ru.make.account.load;

import ru.make.account.load.model.stuff.LoadData;
import ru.make.account.load.util.AccessUtil;
import ru.make.account.load.util.AccountUtil;
import ru.make.account.load.util.LoadUtil;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Импорт данных");
        Scanner scanner = new Scanner(System.in);

        System.out.println("> пользователь:");
        var user = scanner.next();
        System.out.println("> пароль:");
        var password = scanner.next();
        System.out.println("> файл импорта:");
        var fileName = scanner.next();
        System.out.println("> host (keycloak):");
        var host = scanner.next();
        System.out.println("> port (keycloak):");
        var port = scanner.next();
        System.out.println("> realm (keycloak):");
        var realm = scanner.next();
        System.out.println("> host (back):");
        var hostBack = scanner.next();
        System.out.println("> port (back):");
        var portBack = scanner.next();
        System.out.println("> название счёта:");
        var accountName = scanner.next();
        System.out.println("> ID пользователя:");
        var userId = scanner.next();

        var loadData = new LoadData(hostBack, portBack, accountName);
        var accessUtil = new AccessUtil(host, port, user, password, realm, userId);
        var loadUtil = new LoadUtil(
                fileName,
                accessUtil,
                loadData,
                new AccountUtil(accessUtil, loadData));
        loadUtil.load();
    }
}