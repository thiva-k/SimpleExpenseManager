package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentMemoryAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentMemoryTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.DataBaseUtility;
import android.content.Context;


public class PersistentExpenseManager extends ExpenseManager {

    private DataBaseUtility DBUtil;

    public PersistentExpenseManager(Context context) {

        super(context);
        this.DBUtil = new DataBaseUtility(context);
        setup();
    }

    @Override
    public void setup() {
        TransactionDAO persistentMemoryTransactionDAO = new PersistentMemoryTransactionDAO( DBUtil );
        setTransactionsDAO( persistentMemoryTransactionDAO );

        AccountDAO persistentMemoryAccountDAO = new PersistentMemoryAccountDAO( DBUtil );
        setAccountsDAO( persistentMemoryAccountDAO );

    }
}
