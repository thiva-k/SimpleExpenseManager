package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model;

// Utility Class to perform all Database related activities which implements SQLiteOpenHelper

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DataBaseUtility extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "200655N";
    public static int VERSION_NAME = 1;

    public static final String ACCOUNT_DETAILS = "AccountDetails";
    public static final String ACCOUNT_NUMBER = "AccountNo";
    public static final String BANK_NAME = "BankName";
    public static final String ACCOUNT_HOLDER_NAME = "AccountHolderName";
    public static final String BALANCE = "Balance";
    public static final String TRANSACTION_DETAILS = "TransactionDetails";
    public static final String TRANSACTION_ID = "TransactionId";
    public static final String DATE = "Date";
    public static final String ACCOUNT_NO = "AccountNo";
    public static final String EXPENSE_TYPE = "ExpenseType";
    public static final String AMOUNT = "Amount";


    public DataBaseUtility(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NAME);
        this.context=context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String createAccountDetails = "CREATE TABLE IF NOT EXISTS "+ ACCOUNT_DETAILS +" ( "
                + ACCOUNT_NUMBER + " TEXT PRIMARY KEY,"
                + BANK_NAME + " TEXT, "
                + ACCOUNT_HOLDER_NAME + " TEXT,"
                + BALANCE + " NUMERIC " + ");";
        db.execSQL(createAccountDetails);

        String createTransactionDetails = "CREATE TABLE IF NOT EXISTS " + TRANSACTION_DETAILS + " ( "
                + TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DATE + " TEXT, "
                + ACCOUNT_NO + " TEXT, "
                + EXPENSE_TYPE + " TEXT,"
                + AMOUNT + " NUMERIC, FOREIGN KEY ("+ACCOUNT_NO+") REFERENCES "+ ACCOUNT_DETAILS +"("+ACCOUNT_NUMBER+"));";

        db.execSQL(createTransactionDetails);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_DETAILS);
        database.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_DETAILS);
    }

    public List<String> getAccountNumbersList(){

        SQLiteDatabase database = this.getReadableDatabase();
        String query = " SELECT " + ACCOUNT_NUMBER + ", " + BANK_NAME + ", " + ACCOUNT_HOLDER_NAME + ", " + BALANCE + " FROM " + ACCOUNT_DETAILS + " ;" ;
        Cursor cursor =database.rawQuery(query,null);

        List<String> accountNumbersList = new ArrayList<String>();

        if (cursor.moveToFirst()) {
            do {
                accountNumbersList.add(cursor.getString(0));

            } while (cursor.moveToNext());
        }
        return accountNumbersList;
    }

    public List<Account> getAccountsList(){

        SQLiteDatabase database = this.getReadableDatabase();
        String query = " SELECT " + ACCOUNT_NUMBER + ", " + BANK_NAME + ", " + ACCOUNT_HOLDER_NAME + ", " + BALANCE + " FROM " + ACCOUNT_DETAILS + " ;" ;
        Cursor cursor = database.rawQuery(query,null);

        List<Account> accountsList = new ArrayList<Account>();

        if (cursor.moveToFirst()) {
            do {
                accountsList.add(new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3)));
            } while (cursor.moveToNext());
        }
        return accountsList;

    }

    public Account getAccount(String accountNo){

        SQLiteDatabase database = this.getReadableDatabase();
        String query = " SELECT " + ACCOUNT_NUMBER + ", " + BANK_NAME + ", " + ACCOUNT_HOLDER_NAME + ", " + BALANCE + " FROM " + ACCOUNT_DETAILS + " WHERE " + ACCOUNT_NUMBER + "= '"+accountNo+"';" ;
        Cursor cursor =database.rawQuery(query,null);
        cursor.moveToNext();

        return ( new Account(cursor.getString(cursor.getColumnIndex(ACCOUNT_NUMBER)), cursor.getString(cursor.getColumnIndex(BANK_NAME)), cursor.getString(cursor.getColumnIndex(ACCOUNT_HOLDER_NAME)), Double.parseDouble(cursor.getString(cursor.getColumnIndex(BALANCE)))));

    }

    public void addAccount(String account_no, String account_holder_name, String bank_name, double balance){

        SQLiteDatabase database =this.getWritableDatabase();
        ContentValues contentValues =new ContentValues();

        contentValues.put(ACCOUNT_NUMBER, account_no);
        contentValues.put(BANK_NAME, bank_name);
        contentValues.put(ACCOUNT_HOLDER_NAME, account_holder_name);
        contentValues.put(BALANCE, balance);

        database.insert(ACCOUNT_DETAILS,null, contentValues);

    }

    public void removeAccount(String accountNo){

        SQLiteDatabase database =this.getWritableDatabase();
        database.delete( ACCOUNT_DETAILS,ACCOUNT_NUMBER + " = " + accountNo,null);

    }

    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) {

        SQLiteDatabase database = this.getReadableDatabase();

        if (expenseType == ExpenseType.EXPENSE){
            double total= this.getAccount(accountNo).getBalance() - amount;

            if (total > 0){

                String query = " UPDATE " + ACCOUNT_DETAILS + " SET " + BALANCE + " = '" + total + "' WHERE " + ACCOUNT_NUMBER + " = '" + accountNo + "';" ;
                Cursor cursor = database.rawQuery(query,null);
                cursor.moveToFirst();
                cursor.close();

            }
            else{
                Toast.makeText(context, "Account balance insufficient", Toast.LENGTH_SHORT).show();

            }

        }
        else{
            double total = this.getAccount(accountNo).getBalance() + amount;
            String strTotal=new Double(total).toString();

            String query = " UPDATE " + ACCOUNT_DETAILS + " SET " + BALANCE + " = " + strTotal + " WHERE " + ACCOUNT_NUMBER + " = '" + accountNo + "';";
            Cursor cursor = database.rawQuery(query,null);
            cursor.moveToFirst();
            cursor.close();
        }


    }

    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, Double amount){

        SQLiteDatabase database =this.getWritableDatabase();
        ContentValues contentValues =new ContentValues();

        contentValues.put(DATE, date.toString());
        contentValues.put(ACCOUNT_NO, accountNo);
        contentValues.put(EXPENSE_TYPE, String.valueOf(expenseType));
        contentValues.put(AMOUNT, amount);

        database.insert(TRANSACTION_DETAILS,null, contentValues);
    }

    public List<Transaction> getAllTransactionLogs() {

        SQLiteDatabase database = this.getReadableDatabase();
        String query = " SELECT " + DATE + ", " + ACCOUNT_NUMBER + ", " + EXPENSE_TYPE + ", " + AMOUNT + " FROM " + TRANSACTION_DETAILS + " ;" ;
        Cursor cursor = database.rawQuery(query,null);

        List<Transaction> transactions = new ArrayList<Transaction>();

        if (cursor.moveToFirst()) {
            do {
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                Date date = null;

                try {
                    date = dateFormat.parse(this.parseTodayDate(cursor.getString(0)));

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                transactions.add(new Transaction(date,cursor.getString(1), ExpenseType.valueOf(cursor.getString(2)), cursor.getDouble(3)));
            } while (cursor.moveToNext());
        }
        return transactions;

    }

    public static String parseTodayDate(String time) {

        String inputFormat = "EEE MMM d HH:mm:ss zzz yyyy";
        String outputFormat = "dd-MM-yyyy";

        SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat);

        String string = null;
        Date date = null;

        try {
            date = inputDateFormat.parse(time);
            string = outputDateFormat.format(date);

            Log.i("mini", "Converted Date is Today :- " + string);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return string;
    }
}