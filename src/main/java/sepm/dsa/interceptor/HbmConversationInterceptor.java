package sepm.dsa.interceptor;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import sepm.dsa.application.ReloadEvent;

public class HbmConversationInterceptor implements MethodInterceptor, ApplicationEventPublisherAware {

    private final static Logger log = LoggerFactory.getLogger(HbmConversationInterceptor.class);

    private SessionFactory sessionFactory;
    private Session disconnectedSession = null;
	private ApplicationEventPublisher applicationEventPublisher;

    public Object invoke(MethodInvocation invocation) throws Throwable {
        String args = "[args:";
        for (Object arg : invocation.getArguments()) {
            args += (arg == null ? "null" : arg.toString()) + "; ";
        }
        args += "]";
        log.debug("invoke method '" + invocation.getMethod().toGenericString() + "' " + args);

        Session currentSession = null;

        if (disconnectedSession == null) {
            log.debug("OPEN new session");
            currentSession = sessionFactory.openSession();
            currentSession.setFlushMode(FlushMode.MANUAL);
        } else {
            log.debug("REUSING session");
            currentSession = disconnectedSession;
        }

        ManagedSessionContext.bind(currentSession);
	    if (! currentSession.getTransaction().isActive()) {
		    currentSession.beginTransaction();
	    }

        Object returnVal = invocation.proceed();

        ManagedSessionContext.unbind(sessionFactory);

        if (invocation.getMethod().getName().equals("save")) {
            log.info("save => flush, commit");
            currentSession.flush();
            currentSession.getTransaction().commit();
		    applicationEventPublisher.publishEvent(new ReloadEvent(this));
            disconnectedSession = currentSession;
        } else if (invocation.getMethod().getName().equals("cancel")) {
            log.debug("cancel => rollback");
            currentSession.getTransaction().rollback();
            disconnectedSession = currentSession;
        } else if (invocation.getMethod().getName().equals("closeSession")) {
            log.debug("closeSession");
            currentSession.close();
            disconnectedSession = null;
        } else {
            log.debug("just continue");
            currentSession.getTransaction().commit();
            disconnectedSession = currentSession;
        }
        return returnVal;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
}
