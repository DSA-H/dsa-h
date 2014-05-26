package sepm.dsa.service;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("mapService")
@Transactional(readOnly = true)
public class MapServiceImpl implements MapService {
	private static final Logger log = LoggerFactory.getLogger(MapServiceImpl.class);
	private File alternativeDir = new File("maps/alternative");
	private File activeDir = new File("maps/active");

	@Override
	public File chooseMap() {

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
		File newMap = fileChooser.showOpenDialog(new Stage());
		return newMap;
	}

	@Override
	public void setWorldMap(File newMap) {

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

	public File getActiveDir() { return activeDir; }

	public File getAlternativeDir() { return alternativeDir; }

	public File getWorldMap() {
		File[] matchingFiles = activeDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("worldMap");
			}
		});
		if (matchingFiles != null && matchingFiles.length >= 1) {
			return matchingFiles[0];
		}
		return null;
	}
}
