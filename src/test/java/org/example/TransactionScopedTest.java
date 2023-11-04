package org.example;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static jakarta.transaction.Status.STATUS_COMMITTED;
import static jakarta.transaction.Status.STATUS_ROLLEDBACK;

@QuarkusTest
class TransactionScopedTest {
    private final TransactionScopedBean txScopedBean;
    private final TransactionManager txManager;
    private Integer txStatus;

    TransactionScopedTest(TransactionScopedBean txScopedBean, TransactionManager txManager) {
        this.txScopedBean = txScopedBean;
        this.txManager = txManager;
    }

    @Test
    void preDestroyAfterCommit() {
        successfulTransaction();
        Assertions.assertEquals(txStatus, STATUS_COMMITTED);
    }

    @Test
    void preDestroyAfterRollback() throws SystemException {
        failedTransaction();
        Assertions.assertEquals(txStatus, STATUS_ROLLEDBACK);
    }

    @Transactional
    void successfulTransaction() {
        txScopedBean.setOnPreDestroy(this::setTxStatus);
    }

    @Transactional
    void failedTransaction() throws SystemException {
        txScopedBean.setOnPreDestroy(this::setTxStatus);
        txManager.setRollbackOnly();
    }

    private void setTxStatus(Transaction tx) {
        try {
            this.txStatus = tx.getStatus();
        } catch (SystemException e) {
            throw new RuntimeException(e);
        }
    }
}
