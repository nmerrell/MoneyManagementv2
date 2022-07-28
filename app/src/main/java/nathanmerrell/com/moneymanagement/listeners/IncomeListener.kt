package nathanmerrell.com.moneymanagement.listeners

interface IncomeListener {
    fun saveSource(source: String, amount: String, tag: String)
    fun updateSource(source: String, amount: String, tag: String)
    fun deleteSource(source: String, amount: String, tag: String)
}