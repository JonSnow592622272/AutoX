package com.stardust.automyjsa.core.database;

import java.sql.ResultSet;

public interface StatementCallback {

    void handleEvent(Transaction transaction, DatabaseResultSet resultSet);
}
