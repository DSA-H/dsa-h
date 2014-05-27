package sepm.dsa.dao;

import sepm.dsa.model.ProductUnit;

import java.math.BigDecimal;

public class ProductUnitAmount {

    private ProductUnit productUnit;
    private BigDecimal amount;

    public ProductUnitAmount(ProductUnit productUnit, BigDecimal amount) {
        this.productUnit = productUnit;
        this.amount = amount;
    }

    public ProductUnitAmount() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductUnitAmount)) return false;

        ProductUnitAmount that = (ProductUnitAmount) o;

        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (productUnit != null ? !productUnit.equals(that.productUnit) : that.productUnit != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = productUnit != null ? productUnit.hashCode() : 0;
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductUnitAmount{" +
                "productUnit=" + productUnit +
                ", amount=" + amount +
                '}';
    }

    public void setProductUnit(ProductUnit productUnit) {
        this.productUnit = productUnit;
    }
}
