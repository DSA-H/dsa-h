package sepm.dsa.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class BaseControllerImpl implements BaseController {
	private static final Logger log = LoggerFactory.getLogger(BaseControllerImpl.class);

	private boolean initialized = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialized = true;
	}
}
