package sepm.dsa.gui;

import sepm.dsa.model.TraderCategory;
import sepm.dsa.service.TraderCategoryService;

public class EditTraderCategoryController {
    private TraderCategoryService traderCategoryService;
    private static TraderCategory traderCategory;

    public static void setTraderCategory(TraderCategory traderCategory) {
        EditTraderCategoryController.traderCategory = traderCategory;
    }

    public static TraderCategory getTraderCategory() {
        return traderCategory;
    }

    public void setTraderCategoryService(TraderCategoryService traderCategoryService) {
        this.traderCategoryService = traderCategoryService;
    }

    public TraderCategoryService getTraderCategoryService() {
        return traderCategoryService;
    }
}
