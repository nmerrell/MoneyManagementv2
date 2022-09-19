package nathanmerrell.com.moneymanagement.databaseManagement

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import nathanmerrell.com.moneymanagement.utils.DropDownListItem
import java.text.DateFormatSymbols
import java.util.*

class DataService {
    private lateinit var db: SQLiteDatabase
    fun service(dbName: String){
        db = SQLiteDatabase.openDatabase(dbName, null, SQLiteDatabase.OPEN_READWRITE)
    }

    fun setAmounts(source: String, amount: String, tag: String, type: String){
        var sql = "SELECT * FROM $tag"
        val c: Cursor = db.rawQuery(sql, null)
        c.close()

        sql = if (tag == "Income")
                "Insert into $tag (Source, Amount) Values (\"${source.lowercase(Locale.getDefault())}\", \"$amount\")"
            else
                "Insert into $tag (Source, Amount, Type) Values (\"${source.lowercase(Locale.getDefault())}\", \"$amount\", \"$type\")"

        db.execSQL(sql)

    }
    fun getAmounts(tag: String): ArrayList<DropDownListItem> {
        val sourceAmtArray = ArrayList<DropDownListItem>()
        try {
            val c: Cursor = db.rawQuery("Select * from $tag", null)
            for (i in 0 until c.count) {
                if (c.moveToNext()) {
                    if (tag == "Income") {
                        val dropDownListItem = DropDownListItem(c.getString(0), c.getString(1), "")
                        sourceAmtArray.add(dropDownListItem)
                    }
                    else {
                        val dropDownListItem = DropDownListItem(c.getString(0), c.getString(1), c.getString(2))
                        sourceAmtArray.add(dropDownListItem)
                    }
                } else {
                    val dropDownListItem = DropDownListItem("", "", "")
                    sourceAmtArray.add(dropDownListItem)
                }
            }
            c.close()
        }
        catch (e: Exception){
            println("${e.message} for getAmounts")
        }
        return sourceAmtArray
    }

    fun updateSource(source: String, amount: String, tag: String, type: String) {
        val sql = if (tag == "Income") "UPDATE $tag SET Amount=\"$amount\" WHERE Source=\"${source.lowercase(
            Locale.getDefault()
        )}\"" else "UPDATE $tag SET Amount=\"$amount\", Type=\"$type\" WHERE Source=\"${source.lowercase(
            Locale.getDefault()
        )}\""
        db.execSQL(sql)
    }

    fun deleteEntry(source: String, tag: String){
        db.execSQL("DELETE FROM $tag WHERE Source=\"${source.lowercase(Locale.getDefault())}\"")
    }

    fun deleteDBEntries(all: Boolean){
        db.execSQL("DELETE FROM Income")
        db.execSQL("DELETE FROM Expenses")
        if (all) {
            db.execSQL("DELETE FROM Archive_Income")
            db.execSQL("DELETE FROM Archive_Expense")
        }
    }

    fun getCategories(): ArrayList<String>{
        val categories = ArrayList<String>()

        val sql = "Select * from Category"
        val c: Cursor = db.rawQuery(sql, null)
        while (c.moveToNext()){
            categories.add(c.getString(c.getColumnIndexOrThrow("Type")))
        }
        c.close()

        return categories
    }

    fun archiveData(incomeAmounts: ArrayList<DropDownListItem>, expenseAmounts: ArrayList<DropDownListItem>, month: String, year: String) {
        //deleteDBEntries()
        var incomeDBMonth: String? = null
        var expenseDbMonth: String? = null
        var incomeDBYear: String? = null
        var expenseDBYear: String? = null
        val incomeDateSQL = "SELECT * FROM ARCHIVE_INCOME"
        val iC = db.rawQuery(incomeDateSQL, null)
        while (iC.moveToNext()) {
            val dbMonth = iC.getString(iC.getColumnIndexOrThrow("Month"))
            val dbYear = iC.getString(iC.getColumnIndexOrThrow("Year"))
            if (checkMonthYear(dbMonth, dbYear)){
                if (incomeDBMonth == null && incomeDBYear == null) {
                    incomeDBMonth = dbMonth
                    incomeDBYear = dbYear
                }
            }
        }
        iC.close()

        val expenseDateSQL = "SELECT * FROM ARCHIVE_EXPENSE"
        val eC = db.rawQuery(expenseDateSQL, null)
        while (eC.moveToNext()) {
            val dbYear = eC.getString(eC.getColumnIndexOrThrow("Year"))
            val dbMonth = eC.getString(eC.getColumnIndexOrThrow("Month"))
            if (checkMonthYear(dbMonth, dbYear)){
                if (expenseDbMonth == null && expenseDBYear == null) {
                    expenseDbMonth = dbMonth
                    expenseDBYear = dbYear
                }
            }
        }
        eC.close()

        for (source in incomeAmounts) {
            if (!recordExist(incomeDBMonth ?: month, incomeDBYear ?: year, source, 0)) {
                val sql = "INSERT INTO Archive_Income (SOURCE, INCOME, MONTH, YEAR) VALUES (\"${source.text}\", ${source.value}, \"${incomeDBMonth
                        ?: month}\", \"${incomeDBYear ?: year}\")"
                db.execSQL(sql)
            }
        }

        for (source in expenseAmounts) {
            if (!recordExist(expenseDbMonth ?: month, expenseDBYear ?: year, source, 1)) {
                val sql = "INSERT INTO Archive_Expense (SOURCE, EXPENSE, MONTH, YEAR, CATEGORY) VALUES (\"${source.text}\", ${source.value}, \"${expenseDbMonth
                        ?: month}\", \"${expenseDBYear ?: year}\", \"${source.category}\")"
                db.execSQL(sql)
            }
        }
    }

    private fun recordExist(month: String, year: String, item: DropDownListItem, table: Int): Boolean {
        val sql = "SELECT * FROM ARCHIVE_${if (table == 0) "Income" else "Expense"}"
        val c = db.rawQuery(sql, null)
        while (c.moveToNext()) {
            val dbMonth = c.getString(c.getColumnIndexOrThrow("Month"))
            val dbYear = c.getString(c.getColumnIndexOrThrow("Year"))
            val amount = c.getString(c.getColumnIndexOrThrow(if (table == 0) "Income" else "Expense"))
            val source = c.getString(c.getColumnIndexOrThrow("Source"))

            if (source == item.text && amount == item.value && month == dbMonth && year == dbYear) {
                return true
            }
        }
        return false
    }

    private fun checkMonthYear(month: String, year: String): Boolean {
        val localMonth = Date().toString().substring(4, 7)
        val localYear = Date().toString().substring(Date().toString().lastIndex - 3, Date().toString().lastIndex + 1)
        return (localMonth == month && localYear == year)
    }

    fun retrieveArchiveData(month: String, year: String): ArrayList<ArrayList<DropDownListItem>> {
        val list = ArrayList<ArrayList<DropDownListItem>>()
        val incomeList = ArrayList<DropDownListItem>()
        val expenseList = ArrayList<DropDownListItem>()

        var sql = "SELECT * FROM ARCHIVE_INCOME"
        val incomeCursor = db.rawQuery(sql, null)
        while (incomeCursor.moveToNext()) {
            val dbYear = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow("Year"))
            val dbMonth = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow("Month"))

            if (dbYear == year && dbMonth == month.substring(0, 3)) {
                val income = DropDownListItem(incomeCursor.getString(incomeCursor.getColumnIndexOrThrow("Source")), incomeCursor.getString(incomeCursor.getColumnIndexOrThrow("Income")), "")
                incomeList.add(income)
            }
        }
        incomeCursor.close()

        sql = "SELECT * FROM ARCHIVE_EXPENSE"
        val expenseCursor = db.rawQuery(sql, null)
        while (expenseCursor.moveToNext()) {
            val dbYear = expenseCursor.getString(incomeCursor.getColumnIndexOrThrow("Year"))
            val dbMonth = expenseCursor.getString(incomeCursor.getColumnIndexOrThrow("Month"))

            if (dbYear == year && dbMonth == month.substring(0, 3)) {
                val expense = DropDownListItem(expenseCursor.getString(expenseCursor.getColumnIndexOrThrow("Source")), expenseCursor.getString(expenseCursor.getColumnIndexOrThrow("Expense")), expenseCursor.getString(expenseCursor.getColumnIndexOrThrow("Category")))
                expenseList.add(expense)
            }
        }
        expenseCursor.close()

        list.add(incomeList)
        list.add(expenseList)
        return list
    }
}
