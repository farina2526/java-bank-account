public class BankAccount {

    private static final long NO_LIMIT = Long.MAX_VALUE;

    private final String owner;
    private final String accountNumber;
    private final long withdrawalLimitInRubKopecks;

    private Balance balance;
    private boolean blocked;

    public BankAccount(String owner, String accountNumber, Balance initialBalance,
                       long withdrawalLimitInRubKopecks) {
        if (owner == null) {
            throw new NullPointerException("owner не может быть null");
        }
        if (owner.isBlank()) {
            throw new IllegalArgumentException("owner не может быть пустым");
        }
        this.owner = owner;
        if (accountNumber == null) {
            throw new NullPointerException("accountNumber не может быть null");
        }
        if (accountNumber.isBlank()) {
            throw new IllegalArgumentException("accountNumber не может быть пустым");
        }
        this.accountNumber = accountNumber;
        if (initialBalance == null) {
            throw new NullPointerException("initialBalance не может быть null");
        }
        this.balance = initialBalance;
        if (withdrawalLimitInRubKopecks < 0) {
            throw new IllegalArgumentException("Лимит на вывод не может быть отрицательным");
        }
        this.withdrawalLimitInRubKopecks = withdrawalLimitInRubKopecks;
    }

    public BankAccount(String owner, String accountNumber, Balance initialBalance) {
        this(owner, accountNumber, initialBalance, NO_LIMIT);
    }

    public void deposit(Balance amount) {
        ensureNotBlocked();
        if (amount == null) {
            throw new NullPointerException("amount не может быть null");
        }
        Balance inAccountCurrency = amount.convertTo(balance.getCurrency());
        balance = balance.add(inAccountCurrency);
    }

    public void withdraw(Balance amount) {
        ensureNotBlocked();
        if (amount == null) {
            throw new NullPointerException("amount не может быть null");
        }
        Balance inAccountCurrency = amount.convertTo(balance.getCurrency());
        checkFunds(inAccountCurrency);
        checkWithdrawalLimit(inAccountCurrency);
        balance = balance.subtract(inAccountCurrency);
    }

    public void transferTo(BankAccount target, Balance amount) {
        if (target == null) {
            throw new NullPointerException("target не может быть null");
        }
        if (target == this) {
            throw new IllegalArgumentException("Нельзя перевести деньги самому себе");
        }
        ensureNotBlocked();
        target.ensureNotBlocked();
        if (amount == null) {
            throw new NullPointerException("amount не может быть null");
        }
        Balance inSourceCurrency = amount.convertTo(balance.getCurrency());
        checkFunds(inSourceCurrency);
        checkWithdrawalLimit(inSourceCurrency);
        Balance inTargetCurrency = inSourceCurrency.convertTo(target.balance.getCurrency());
        balance = balance.subtract(inSourceCurrency);
        target.balance = target.balance.add(inTargetCurrency);
    }

    public void block() {
        blocked = true;
    }

    public void unblock() {
        blocked = false;
    }

    public Balance getBalance() {
        return balance;
    }

    public String getOwner() {
        return owner;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public long getWithdrawalLimitInRubKopecks() {
        return withdrawalLimitInRubKopecks;
    }

    public boolean isBlocked() {
        return blocked;
    }

    private void checkFunds(Balance amountInAccountCurrency) {
        if (amountInAccountCurrency.getAmount() > balance.getAmount()) {
            throw new IllegalStateException(
                    "Недостаточно средств на счёте " + accountNumber + ": баланс " + balance);
        }
    }

    private void checkWithdrawalLimit(Balance amountInAccountCurrency) {
        long amountInRubKopecks = amountInAccountCurrency.toRubKopecks();
        if (amountInRubKopecks > withdrawalLimitInRubKopecks) {
            throw new IllegalStateException(
                    "Превышен лимит на вывод по счёту " + accountNumber
                            + ": максимум " + rub(withdrawalLimitInRubKopecks));
        }
    }

    private void ensureNotBlocked() {
        if (blocked) {
            throw new IllegalStateException("Счёт " + accountNumber + " заблокирован");
        }
    }

    private static String rub(long kopecks) {
        return String.format("%d.%02d ₽", kopecks / 100, kopecks % 100);
    }

    @Override
    public String toString() {
        String limit = (withdrawalLimitInRubKopecks == NO_LIMIT)
                ? "без лимита"
                : rub(withdrawalLimitInRubKopecks) + " за операцию";
        return String.format("BankAccount{%s (%s): %s, лимит вывода: %s, %s}",
                owner, accountNumber, balance, limit,
                blocked ? "ЗАБЛОКИРОВАН" : "активен");
    }
}
