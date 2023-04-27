package com.stardust.automyjsa.core.database;

public interface TransactionCallback {
    void handleEvent(Transaction transaction);
}
