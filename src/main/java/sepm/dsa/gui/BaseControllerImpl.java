package sepm.dsa.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import sepm.dsa.application.ReloadEvent;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class BaseControllerImpl implements BaseController, ApplicationListener<ReloadEvent> {
	private static final Logger log = LoggerFactory.getLogger(BaseControllerImpl.class);

	private boolean initialized = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialized = true;
	}

	@Override
	public void onApplicationEvent(ReloadEvent event) {
		if (initialized) {
			log.info("Reloading GUI controller: " + this.getClass().getName());
			this.reload();
		}
	}

    @Override
    public void unload() {
        initialized = false;
    }
}
