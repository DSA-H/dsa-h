package sepm.dsa.interceptor;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbmConversationInterceptor implements MethodInterceptor {

    private final static Logger log = LoggerFactory.getLogger(HbmConversationInterceptor.class);

    private SessionFactory sessionFactory;
    private Session disconnectedSession = null;

    public Object invoke(MethodInvocation invocation) throws Throwable {

        log.info("invoke method '" + invocation.getMethod().toGenericString() + "'");

        Session currentSession = null;

        if (disconnectedSession == null) {
            log.info("OPEN new session");
            currentSession = sessionFactory.openSession();
            currentSession.setFlushMode(FlushMode.MANUAL);
        } else {
            log.info("REUSING session");
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
            disconnectedSession = currentSession;
        } else if (invocation.getMethod().getName().equals("cancel")) {
            log.info("cancel => rollback");
            currentSession.getTransaction().rollback();
            disconnectedSession = currentSession;
        } else if (invocation.getMethod().getName().equals("closeSession")) {
            log.info("closeSession");
            currentSession.close();
            disconnectedSession = null;
        } else {
            log.info("just continue");
            currentSession.getTransaction().commit();
            disconnectedSession = currentSession;
        }
        return returnVal;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
