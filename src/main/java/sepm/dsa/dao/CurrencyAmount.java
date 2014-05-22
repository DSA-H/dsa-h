package sepm.dsa.dao;

import sepm.dsa.model.Currency;

public class CurrencyAmount {

    private Currency currency;
    private int amount;

    public CurrencyAmount(Currency currency, int amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public CurrencyAmount() {
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyAmount)) return false;

        CurrencyAmount that = (CurrencyAmount) o;

        if (amount != that.amount) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = currency != null ? currency.hashCode() : 0;
        result = 31 * result + amount;
        return result;
    }

    @Override
    public String toString() {
        return "CurrencyAmount{" +
                "currency=" + currency +
                ", amount=" + amount +
                '}';
    }
}
