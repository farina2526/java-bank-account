import java.util.Objects;

public final class Balance {

    private final long amount;
    private final Currency currency;

    public Balance(long amount, Currency currency) {
        if (amount < 0) {
            throw new IllegalArgumentException("Сумма баланса не может быть отрицательной: " + amount);
        }
        if (currency == null) {
            throw new NullPointerException("Currency не может быть null");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public long getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Balance add(Balance other) {
        if (other == null) {
            throw new NullPointerException("other не может быть null");
        }
        requireSameCurrency(other);
        return new Balance(amount + other.amount, currency);
    }

    public Balance subtract(Balance other) {
        if (other == null) {
            throw new NullPointerException("other не может быть null");
        }
        requireSameCurrency(other);
        if (other.amount > amount) {
            throw new IllegalStateException(
                    "Недостаточно средств: на балансе " + this + ", списать " + other);
        }
        return new Balance(amount - other.amount, currency);
    }

    public Balance convertTo(Currency target) {
        if (target == null) {
            throw new NullPointerException("targetCurrency не может быть null");
        }
        if (target == currency) {
            return this;
        }
        long rubKopecks = toRubKopecks();
        long converted = rubKopecks * 100 / target.getPriceInRublesKopecks();
        return new Balance(converted, target);
    }

    public long toRubKopecks() {
        return amount * currency.getPriceInRublesKopecks() / 100;
    }

    private void requireSameCurrency(Balance other) {
        if (currency != other.currency) {
            throw new IllegalArgumentException(
                    "Операция возможна только в одной валюте: " + currency + " и " + other.currency);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Balance other)) return false;
        return amount == other.amount && currency == other.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return String.format("%d.%02d %s", amount / 100, amount % 100, currency.getSymbol());
    }
}
