package sepm.dsa.gui;

import sepm.dsa.model.Region;

import java.util.List;

/**
 * Modell for the Table view in regionlist.fxml
 * Created by Jotschi on 12.05.2014.
 */
public class RegionTableModel {
    private Region region;

    public String getRegion() {
        return region.getName();
    }

    public String getBorder() {
        // todo String list of bordering regions
    }


}
