package nathanmerrell.com.moneymanagement.listeners

interface ExpenseListener {
    fun saveSource(source: String, amount: String, tag: String, type: String)
    fun updateSource(source: String, amount: String, tag: String, type: String)
    fun deleteSource(source: String, amount: String, tag: String, type: String)
}