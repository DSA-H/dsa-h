package sepm.dsa.application;

import org.springframework.context.ApplicationEvent;

public class ReloadEvent extends ApplicationEvent {
	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the component that published the event (never {@code null})
	 */
	public ReloadEvent(Object source) {
		super(source);
	}
}
