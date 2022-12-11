package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.DataBaseUtility;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentMemoryAccountDAO implements AccountDAO {

    private DataBaseUtility DBUtil;  // Utility Class to perform all Database related activities

    public PersistentMemoryAccountDAO( DataBaseUtility DBUtil ){
        this.DBUtil = DBUtil;
    }

    @Override
    public List<String> getAccountNumbersList() {
        return  DBUtil.getAccountNumbersList();
    }

    @Override
    public List<Account> getAccountsList() {
        return DBUtil.getAccountsList();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return DBUtil.getAccount(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        DBUtil.addAccount(account.getAccountNo(), account.getAccountHolderName(), account.getBankName(), account.getBalance());
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        DBUtil.removeAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        DBUtil.updateBalance(accountNo, expenseType, amount);
    }
}