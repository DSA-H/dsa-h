package sepm.dsa.service;

import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.AssortmentNature;

public interface AssortmentNatureService {

    /**
     * Validates a Location
     *
     * @param assortmentNature must not be null
     * @throws sepm.dsa.exceptions.DSAValidationException if assortmentNature is not valid
     */
    public void validate(AssortmentNature assortmentNature) throws DSAValidationException;
}
