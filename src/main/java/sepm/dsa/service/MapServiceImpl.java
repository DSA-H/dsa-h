package sepm.dsa.service;

import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.model.Location;
import sepm.dsa.model.TownSize;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service("mapService")
@Transactional(readOnly = true)
public class MapServiceImpl implements MapService {

    private static final Logger log = LoggerFactory.getLogger(MapServiceImpl.class);
    private LocationService locationService;
    private SaveCancelService saveCancelService;
    private File alternativeDir = new File("maps/alternative");
    private File activeDir = new File("maps/active");
    private File ressourceDir = new File("maps/ressource");

    private Properties getProperties() {
        try {
            Properties properties = PropertiesService.getProperties();
            Path path = Paths.get("resources/properties");
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            InputStream is = Files.newInputStream(Paths.get("resources/properties"));
            properties.load(is);
            is.close();
	        return properties;
        } catch (IOException e) {
            throw new DSARuntimeException("Probleme beim Laden der Properties Datei! \n" + e.getMessage());
        }
    }

    @Override
    public String colorToString(Color color) {
        log.debug("calling colorToString(" + color + ")");
        String result = "";
        String red = Integer.toHexString((int) (color.getRed() * 255));
        if (red.length() == 1) {
            red = "0" + red;
        }
        String green = Integer.toHexString((int) (color.getGreen() * 255));
        if (green.length() == 1) {
            green = "0" + green;
        }
        String blue = Integer.toHexString((int) (color.getBlue() * 255));
        if (blue.length() == 1) {
            blue = "0" + blue;
        }
        result = red + green + blue;
        log.trace("returning " + result + ")");
        return result;
    }

    @Override
    public Color stringToColor(String colorString) {
        log.debug("calling stringToColor(" + colorString + ")");

        Color result = new Color(
                (double) Integer.valueOf(colorString.substring(0, 2), 16) / 255,
                (double) Integer.valueOf(colorString.substring(2, 4), 16) / 255,
                (double) Integer.valueOf(colorString.substring(4, 6), 16) / 255,
                1.0);

        log.trace("returning " + result + ")");
        return result;
    }

    @Override
    public File chooseMap() {
        log.debug("calling chooseMap()");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Karte auswählen");
        if (alternativeDir.isDirectory() && alternativeDir.list().length > 0) {
            fileChooser.setInitialDirectory(new File("maps/alternative"));
        }
        List<String> extensions = new ArrayList<String>();
        extensions.add("*.jpg");
        extensions.add("*.png");
        extensions.add("*.gif");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", extensions),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File result = fileChooser.showOpenDialog(new Stage());
        if (result != null && result.length() > 11000000) {
            Dialogs.create()
                    .title("Fehler")
                    .masthead(null)
                    .message("Die Datei darf maximal 10 MB groß sein!")
                    .showWarning();
            return null;
        }
        log.trace("returning " + result + ")");
        return result;
    }

    @Override
    public void setWorldMap(File newMap) {
        log.debug("calling setWorldMap(" + newMap + ")");

        //check if old File exists
        File[] matchingFiles = activeDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("worldMap");
            }
        });

        //copy old file to temp
        if (matchingFiles != null && matchingFiles.length >= 1) {
            File oldMap = matchingFiles[0];
            String extOld = FilenameUtils.getExtension(oldMap.getAbsolutePath());
            File temp = new File(alternativeDir + "/lastWorldMapTemp." + extOld);
            try {
                FileUtils.copyFile(oldMap, temp);
                log.debug("copied old map to alternative (temp)");
                oldMap.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //copy new File to dir
        String extNew = FilenameUtils.getExtension(newMap.getAbsolutePath());
        File worldMap = new File(activeDir + "/worldMap." + extNew);
        try {
            FileUtils.copyFile(newMap, worldMap);
            log.debug("copied new map");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //rename temp
        matchingFiles = alternativeDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("lastWorldMapTemp");
            }
        });
        if (matchingFiles != null && matchingFiles.length >= 1) {
            File oldMap = matchingFiles[0];
            String extOld = FilenameUtils.getExtension(oldMap.getAbsolutePath());
            File dest = new File(alternativeDir + "/ehemaligeWeltkarte." + extOld);
            int k = 1;
            while (dest.exists() && !dest.isDirectory()) {
                dest = new File(alternativeDir + "/ehemaligeWeltkarte(" + k + ")." + extOld);
                k++;
            }
            try {
                FileUtils.copyFile(oldMap, dest);
                log.debug("copied temp to alternative");
                oldMap.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void setLocationMap(Location location, File newMap) {
        log.debug("calling setLocationMap(" + location + ", " + newMap + ")");

        //check if old File exists
        File[] matchingFiles = activeDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(location.getId() + "map");
            }
        });

        //copy old file to temp
        if (matchingFiles != null && matchingFiles.length >= 1) {
            File oldMap = matchingFiles[0];
            String extOld = FilenameUtils.getExtension(oldMap.getAbsolutePath());
            File temp = new File(alternativeDir + "/lastMapTemp." + extOld);
            try {
                FileUtils.copyFile(oldMap, temp);
                log.debug("copied old map to alternative (temp)");
                oldMap.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //copy new File to dir
        String extNew = FilenameUtils.getExtension(newMap.getAbsolutePath());
        File locMap = new File(activeDir + "/" + location.getId() + "map." + extNew);
        try {
            FileUtils.copyFile(newMap, locMap);
            log.debug("copied new map");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //rename temp
        matchingFiles = alternativeDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("lastMapTemp");
            }
        });
        if (matchingFiles != null && matchingFiles.length >= 1) {
            File oldMap = matchingFiles[0];
            String extOld = FilenameUtils.getExtension(oldMap.getAbsolutePath());
            File dest = new File(alternativeDir + "/" + location.getName() + "Karte." + extOld);
            int k = 1;
            while (dest.exists() && !dest.isDirectory()) {
                dest = new File(alternativeDir + "/" + location.getName() + "Karte(" + k + ")." + extOld);
                k++;
            }
            try {
                FileUtils.copyFile(oldMap, dest);
                log.debug("copied temp to alternative");
                oldMap.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void exportMap(String mapName) {
        log.debug("calling exportMap(" + mapName + ")");

        //choose File to export
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export-Datei auswählen");
        List<String> extensions = new ArrayList<String>();
        extensions.add("*.jpg");
        extensions.add("*.png");
        extensions.add("*.gif");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", extensions),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File exportFile = fileChooser.showSaveDialog(new Stage());

        if (exportFile == null) {
            return;
        }

        //look for world map
        File[] matchingFiles = activeDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(mapName);
            }
        });

        if (matchingFiles != null && matchingFiles.length >= 1) {
            File worldMap = matchingFiles[0];
            String extSource = FilenameUtils.getExtension(worldMap.getAbsolutePath());
            String extTarget = FilenameUtils.getExtension(exportFile.getAbsolutePath());
            if (exportFile.exists() && !exportFile.isDirectory()) {
                try {
                    if (extTarget.equals("")) {
                        FileUtils.copyFile(worldMap, new File(exportFile.getAbsolutePath() + "." + extSource));
                        exportFile.delete();
                    } else {
                        FileUtils.copyFile(worldMap, exportFile);
                    }
                    log.debug("exported Map " + mapName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public File getActiveDir() {
        log.debug("calling getActiveDir()");
        log.trace("returning " + activeDir);
        return activeDir;
    }

    public File getAlternativeDir() {
        log.debug("calling getAlternativeDir()");
        log.trace("returning " + alternativeDir);
        return alternativeDir;
    }

    public File getWorldMap() {
        log.debug("calling getWorldMap()");
        File result = null;
        File[] matchingFiles = activeDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("worldMap");
            }
        });
        if (matchingFiles != null && matchingFiles.length >= 1) {
            result =  matchingFiles[0];
            log.trace("returning " + result);
            return result;
        }
        log.trace("returning " + null);
        return null;
    }

    @Override
    public File getLocationMap(Location location) {
        log.debug("calling getLocationMap(" + location + ")");
        File[] matchingFiles = activeDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(location.getId() + "map");
            }
        });
        if (matchingFiles != null && matchingFiles.length >= 1) {
            log.trace("returning " + matchingFiles[0]);
            return matchingFiles[0];
        }
        log.trace("returning " + null);
        return null;
    }

    @Override
    public File getNoMapImage() {
        log.debug("calling getNoMapImage()");
        File[] matchingFiles = ressourceDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("noMapFound");
            }
        });
        if (matchingFiles != null && matchingFiles.length >= 1) {
            log.trace("returning " + matchingFiles[0]);
            return matchingFiles[0];
        }
        log.trace("returning " + null);
        return null;
    }

    @Override
    public Color getTraderColor() {
        log.debug("calling getTraderColor()");
        Color result = stringToColor(getProperties().getProperty("traderColor", "0000FF"));
        log.trace("returning " + result);
        return result;
    }

    @Override
    public Color getMovingTraderColor() {
        return stringToColor(getProperties().getProperty("movingTraderColor", "ADD8E6"));
    }

    @Override
    public Color getTavernColor() {
        return stringToColor(getProperties().getProperty("tavernColor", "FFFF00"));
    }

    @Override
    public Color getHighlightColor() {
        return stringToColor(getProperties().getProperty("highlightColor", "000000"));
    }

    @Override
    public Color getSelectionColor() {
        return stringToColor(getProperties().getProperty("selectionColor", "008000"));
    }

    @Override
    public Color getBorderColor() {
        return stringToColor(getProperties().getProperty("borderColor", "000000"));
    }

    @Override
    public Color getNameColor() {
        return stringToColor(getProperties().getProperty("nameColor", "000000"));
    }

    @Override
    public int getWorldIconSize() {
        return Integer.parseInt(getProperties().getProperty("worldIconSize", "20"));
    }

    @Override
    public double getLocationIconSize(Location location) {
        if (location.getIconSize() == null) {
            return 10;
        }
        return location.getIconSize();
    }

    @Override
    public double getTextSize() {
        return Double.parseDouble(getProperties().getProperty("textSize", "1.0"));
    }


    @Override
    public void setTraderColor(Color c) {
        try {
	        Properties properties = getProperties();
            properties.put("traderColor", colorToString(c));
            OutputStream os = Files.newOutputStream(Paths.get("resources/properties"));
            properties.store(os, "");
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMovingTraderColor(Color c) {
        try {
	        Properties properties = getProperties();
	        properties.put("movingTraderColor", colorToString(c));
            OutputStream os = Files.newOutputStream(Paths.get("resources/properties"));
            properties.store(os, "");
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTavernColor(Color c) {
        try {
	        Properties properties = getProperties();
	        properties.put("tavernColor", colorToString(c));
            OutputStream os = Files.newOutputStream(Paths.get("resources/properties"));
            properties.store(os, "");
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setHighlightColor(Color c) {
        try {
	        Properties properties = getProperties();
	        properties.put("highlightColor", colorToString(c));
            OutputStream os = Files.newOutputStream(Paths.get("resources/properties"));
            properties.store(os, "");
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSelectionColor(Color c) {
        try {
	        Properties properties = getProperties();
	        properties.put("selectionColor", colorToString(c));
            OutputStream os = Files.newOutputStream(Paths.get("resources/properties"));
            properties.store(os, "");
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setBorderColor(Color c) {
        try {
	        Properties properties = getProperties();
	        properties.put("borderColor", colorToString(c));
            OutputStream os = Files.newOutputStream(Paths.get("resources/properties"));
            properties.store(os, "");
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setNameColor(Color c) {
        try {
	        Properties properties = getProperties();
	        properties.put("nameColor", colorToString(c));
            OutputStream os = Files.newOutputStream(Paths.get("resources/properties"));
            properties.store(os, "");
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setWorldIconSize(int size) {
        try {
	        Properties properties = getProperties();
	        properties.put("worldIconSize", "" + size);
            OutputStream os = Files.newOutputStream(Paths.get("resources/properties"));
            properties.store(os, "");
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLocationIconSize(Location location, double size) {
        location.setIconSize(size);
        locationService.update(location);
        saveCancelService.save();
    }

    @Override
    public void setTextSize(double size) {
        try {
	        Properties properties = getProperties();
	        properties.put("textSize", "" + size);
            OutputStream os = Files.newOutputStream(Paths.get("resources/properties"));
            properties.store(os, "");
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setShowBiggerThan(TownSize townSize) {
        try {
            Properties properties = getProperties();
            properties.put("showBiggerThan", "" + townSize.getValue());
            OutputStream os = Files.newOutputStream(Paths.get("resources/properties"));
            properties.store(os, "");
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TownSize getShowBiggerThan(){
        log.debug("calling getShowBiggerThan()");
        TownSize townSize = TownSize.parse(Integer.parseInt(getProperties().getProperty("showBiggerThan", "0")));
        log.trace("returning " + townSize);
        return townSize;
    }

    @Override
    public void setShowConnections(boolean show){
        try {
            Properties properties = getProperties();
            properties.put("showConnections", "" + show);
            OutputStream os = Files.newOutputStream(Paths.get("resources/properties"));
            properties.store(os, "");
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean getShowConnections(){
        log.debug("calling getShowConnections()");
        boolean show = Boolean.parseBoolean(getProperties().getProperty("showConnections", "true"));
        log.trace("returning " + show);
        return show;
    }

    public void setLocationService(LocationService locationService) {
        log.trace("calling setLocationService(" + locationService + ")");
        this.locationService = locationService;
    }

    public void setSaveCancelService(SaveCancelService saveConcelService) {
        log.trace("calling setSaveCancelService(" + saveConcelService + ")");
        this.saveCancelService = saveConcelService;
    }
}
