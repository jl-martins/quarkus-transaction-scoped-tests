package org.example;

import jakarta.annotation.PreDestroy;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.TransactionScoped;

import java.util.Objects;
import java.util.function.Consumer;

@TransactionScoped
public class TransactionScopedBean {
    private final TransactionManager txManager;
    private Consumer<Transaction> onPreDestroy;

    public TransactionScopedBean(TransactionManager txManager) {
        this.txManager = txManager;
    }

    @PreDestroy
    void preDestroy() throws SystemException {
        onPreDestroy.accept(txManager.getTransaction());
    }

    public void setOnPreDestroy(Consumer<Transaction> onPreDestroy) {
        this.onPreDestroy = Objects.requireNonNull(onPreDestroy, "onPreDestroy must not be null");
    }
}
