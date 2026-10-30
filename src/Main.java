//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {

        System.out.println("1.Создание счетов");
        BankAccount ivan = new BankAccount(
                "Иван", "ACC-001",
                new Balance(100_000L, Currency.RUB),
                200_000L
        );
        BankAccount anna = new BankAccount(
                "Анна", "ACC-002",
                new Balance(5_000L, Currency.USD)
        );
        System.out.println(ivan);
        System.out.println(anna);

        System.out.println("\n2.Balance неизменяемый");
        Balance salary = new Balance(300_000L, Currency.RUB);
        Balance afterBonus = salary.add(new Balance(50_000L, Currency.RUB));
        System.out.println("salary = " + salary);
        System.out.println("afterBonus = " + afterBonus);

        System.out.println("\n3.Пополнение и снятие");
        ivan.deposit(salary);
        System.out.println("После пополнения на 3000 ₽: " + ivan);
        ivan.withdraw(new Balance(20_000L, Currency.RUB));
        System.out.println("После снятия 200 ₽: " + ivan);

        System.out.println("\n4.Переводы между счетами");
        System.out.println("Перевод 100 ₽ с Ивана на Анну:");
        ivan.transferTo(anna, new Balance(10_000L, Currency.RUB));
        System.out.println(ivan);
        System.out.println(anna);

        System.out.println("\nПеревод 5 $ с Анны на Ивана (конвертируется в рубли):");
        anna.transferTo(ivan, new Balance(500L, Currency.USD));
        System.out.println(ivan);
        System.out.println(anna);

        System.out.println("\n5.Конвертация валют");
        Balance dollars = new Balance(10_000L, Currency.USD);
        System.out.println(dollars + " = " + dollars.convertTo(Currency.RUB));
        System.out.println(dollars + " = " + dollars.convertTo(Currency.EUR));
        System.out.println(dollars + " = " + dollars.convertTo(Currency.CNY));

        System.out.println("\n6.Корнер-кейсы");
        runSafely("Отрицательный баланс",
                () -> new Balance(-1L, Currency.RUB));
        runSafely("Сложение разных валют",
                () -> new Balance(100L, Currency.RUB).add(new Balance(100L, Currency.USD)));
        runSafely("Снятие больше баланса",
                () -> ivan.withdraw(new Balance(500_000L, Currency.RUB)));
        runSafely("Снятие больше лимита",
                () -> ivan.withdraw(new Balance(350_000L, Currency.RUB)));
        runSafely("Перевод больше лимита",
                () -> ivan.transferTo(anna, new Balance(250_000L, Currency.RUB)));
        runSafely("Перевод самому себе",
                () -> ivan.transferTo(ivan, new Balance(100L, Currency.RUB)));
        runSafely("Перевод на null-счёт",
                () -> ivan.transferTo(null, new Balance(100L, Currency.RUB)));
        runSafely("Пополнение на null",
                () -> ivan.deposit(null));

        System.out.println("\n7.Блокировка счёта");
        anna.block();
        runSafely("Снятие с заблокированного счёта",
                () -> anna.withdraw(new Balance(100L, Currency.USD)));
        runSafely("Перевод на заблокированный счёт",
                () -> ivan.transferTo(anna, new Balance(100L, Currency.RUB)));
        anna.unblock();
        System.out.println("Счёт разблокирован: " + anna);

        System.out.println("\n8.equals / hashCode");
        Balance a = new Balance(10_000L, Currency.RUB);
        Balance b = new Balance(10_000L, Currency.RUB);
        Balance c = new Balance(10_000L, Currency.USD);

        System.out.println("a.equals(b) = " + a.equals(b) + "  (ожидалось true)");
        System.out.println("a.equals(c) = " + a.equals(c) + "  (ожидалось false — валюта разная)");
        System.out.println("a.hashCode() == b.hashCode() = " + (a.hashCode() == b.hashCode()));
    }


    private static void runSafely(String description, Runnable action) {
        try {
            action.run();
            System.out.println("OK: " + description + "— не ожидалось");
        } catch (RuntimeException e) {
            System.out.println("ИСКЛЮЧЕНИЕ: " + description + ": "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}