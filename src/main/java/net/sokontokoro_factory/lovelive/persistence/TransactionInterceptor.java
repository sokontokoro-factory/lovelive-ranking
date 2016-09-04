package net.sokontokoro_factory.lovelive.persistence;

import net.sokontokoro_factory.lovelive.exception.AppRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

@Interceptor
@Transactional
public class TransactionInterceptor {
    private static final Logger logger = LogManager.getLogger(TransactionInterceptor.class);

    @Inject
    private EntityManager em;

    /**
     * トランザクション処理を行う
     * rollbackが発生した場合、AppRuntimeExceptionをスローする
     *
     * @param invocationContext
     * @return
     * @throws Exception
     */
    @AroundInvoke
    public Object doTransaction(InvocationContext invocationContext) throws Exception {
        Object result = null;
        EntityTransaction transaction = em.getTransaction();
        try {
            /* トランザクション開始 */
            if (!transaction.isActive()) {
                transaction.begin();
                logger.info("START TRANSACTION");
            }

            /* トランザクション対象の処理(インターセプト先のメソッド)を実行 */
            result = invocationContext.proceed();

            /* コミット */
            if (transaction.isActive()) {
                transaction.commit();
                logger.info("COMMIT TRANSACTION");
            }
        } catch (PersistenceException e) {
            logger.catching(e);
            /* ロールバック */
            if (transaction.isActive()) {
                transaction.rollback();
                logger.info("ROLLBACK TRANSACTION");
            }
            throw new AppRuntimeException(e.getMessage(), e);
        }
        return result;
    }
}