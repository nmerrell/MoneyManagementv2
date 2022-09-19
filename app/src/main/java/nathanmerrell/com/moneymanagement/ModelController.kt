package nathanmerrell.com.moneymanagement

import android.content.Context
import nathanmerrell.com.moneymanagement.databaseManagement.DataService
import nathanmerrell.com.moneymanagement.databaseManagement.DatabaseModule
import nathanmerrell.com.moneymanagement.utils.DropDownListItem

class ModelController{
    companion object {
        private val dataService = DataService()
        private val dbModule = DatabaseModule()

        fun openDatabase(context: Context){
            val loadDB = true
            dbModule.DBModule(context, loadDB)
            dataService.service(dbModule.databaseFile().absolutePath)
        }

        fun setAmounts(source: String, amount: String, tag: String, type: String){
            dataService.setAmounts(source, amount, tag, type)
        }

        fun updateSource(source: String, amount: String, tag: String, type: String){
            dataService.updateSource(source, amount, tag, type)
        }

        fun getAmounts(tag: String): ArrayList<DropDownListItem> = dataService.getAmounts(tag)

        fun deleteEntry(source: String, tag: String){
            dataService.deleteEntry(source, tag)
        }

        fun deleteDBEntries(all: Boolean){
            dataService.deleteDBEntries(all)
        }

        fun getCategories(): ArrayList<String> = dataService.getCategories()

        fun archiveData(incomeAmounts: ArrayList<DropDownListItem>, expenseAmounts: ArrayList<DropDownListItem>, month: String, year: String) {
            dataService.archiveData(incomeAmounts, expenseAmounts, month, year)
        }

        fun requestArchiveData(month: String, year: String) : ArrayList<ArrayList<DropDownListItem>> = dataService.retrieveArchiveData(month, year)
    }
}
