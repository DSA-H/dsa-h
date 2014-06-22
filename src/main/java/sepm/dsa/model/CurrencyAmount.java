package sepm.dsa.model;

public class CurrencyAmount {

    private Currency currency;
    private Integer amount;

    public CurrencyAmount(Currency currency, Integer amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public CurrencyAmount() {
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyAmount)) return false;

        CurrencyAmount that = (CurrencyAmount) o;

        if (!amount.equals(that.amount)) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = currency != null ? currency.hashCode() : 0;
        result = 31 * result + amount.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CurrencyAmount{" +
                "currency=" + currency +
                ", amount=" + amount +
                '}';
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }
}
