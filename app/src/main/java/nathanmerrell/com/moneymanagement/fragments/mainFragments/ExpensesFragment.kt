package nathanmerrell.com.moneymanagement.fragments.mainFragments

import android.annotation.TargetApi
import android.graphics.Color
import android.graphics.Typeface
import android.os.*
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import nathanmerrell.com.moneymanagement.ModelController
import nathanmerrell.com.moneymanagement.R
import nathanmerrell.com.moneymanagement.dialog.AddExpenseDialog
import nathanmerrell.com.moneymanagement.listeners.ExpenseListener
import nathanmerrell.com.moneymanagement.utils.DropDownListItem
import java.text.NumberFormat

@TargetApi(Build.VERSION_CODES.O)
class ExpensesFragment : Fragment() {
    private lateinit var expenseTable: TableLayout
    private lateinit var amounts: ArrayList<DropDownListItem>
    private lateinit var categories: ArrayList<String>
    private lateinit var addExpenseButton: FloatingActionButton
    private lateinit var listener: ExpenseListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.expense_fragment, container, false)

        expenseTable = view.findViewById(R.id.expenseTable)
        addExpenseButton = view.findViewById(R.id.addExpenseButton)

        requestCategories()
        fillInView()

        addExpenseButton.setOnClickListener {
            addSourceItem("", "", "", true)
        }

        return view
    }
    companion object {

        fun newInstance(): ExpensesFragment {
            return ExpensesFragment()
        }
    }
    private fun fillInView() {
        amounts = ModelController.getAmounts(resources.getString(R.string.expense))
        for (i in 0 until amounts.size){
            addRows(amounts[i].text!!, amounts[i].value!!, amounts[i].category!!)
        }
        if (amounts.isEmpty())
            addRows("", "", "")

    }

    fun configureListener(listener: ExpenseListener) {
        this.listener = listener
    }

    fun addRows(source: String, amount: String, category: String){
        if (expenseTable.childCount > 0) {
            checkTable()
        }
        val tableRow = TableRow(context)
        tableRow.setPadding(0, 10, 0, 10)

        val addSource = TextView(context)
        val addExpense = TextView(context)
        val addCategory = TextView(context)

        tableRow.id = expenseTable.childCount

        addSource.setTextColor(Color.BLACK)
        addSource.id = expenseTable.childCount
        addSource.setBackgroundColor(Color.TRANSPARENT)
        addSource.setHint(R.string.sourceHint)
        addSource.textSize = 20f
        addSource.typeface = Typeface.SANS_SERIF
        addSource.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

        addExpense.setTextColor(Color.BLACK)
        addExpense.setBackgroundColor(Color.TRANSPARENT)
        addExpense.setHint(R.string.expenseHint)
        addExpense.id = expenseTable.childCount
        addExpense.textSize = 20f
        addExpense.typeface = Typeface.SANS_SERIF
        addExpense.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

        addCategory.setTextColor(Color.BLACK)
        addCategory.id = expenseTable.childCount
        addCategory.setBackgroundColor(Color.TRANSPARENT)
        addCategory.setHint(R.string.category)
        addCategory.textSize = 20f
        addCategory.typeface = Typeface.SANS_SERIF
        addCategory.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

        if (source.isNotEmpty() && amount.isNotEmpty()) {
            val format = NumberFormat.getCurrencyInstance()
            val amtDouble = amount.toDouble()
            val amt = format.format(amtDouble)
            addExpense.text = Editable.Factory.getInstance().newEditable(amt)
            addSource.text = Editable.Factory.getInstance().newEditable(source.capitalize())
            addCategory.text = Editable.Factory.getInstance().newEditable(category)
        }

        tableRow.addView(addSource)
        tableRow.addView(addExpense)
        tableRow.addView(addCategory)

        tableRow.setOnClickListener {
            if (addSource.text.toString().isNotEmpty() || addExpense.text.toString().isNotEmpty()){
                addSourceItem(addSource.text.toString(), addExpense.text.toString(), category, false)
            }
        }

        expenseTable.addView(tableRow)
    }

    private fun addSourceItem(src: String, amt: String, category: String, isNew: Boolean){
        var amount = amt
        if (amount.isNotEmpty() && amount.first() == '$') {
            amount = amt.substring(1)
        }
        val dropDownListItem = DropDownListItem(src, amount, category)
        val dialog = AddExpenseDialog.newInstance(listener, dropDownListItem, categories, isNew)
        dialog.show(requireActivity().supportFragmentManager, "Expense Source Add")
    }

    private fun checkTable() {
        var rowToDelete = -1
        for (i in 0 until expenseTable.childCount){
            val row = expenseTable.getChildAt(i) as TableRow
            for (j in 0 until row.childCount){
                val view = row.getChildAt(j) as TextView
                if (view.text.isEmpty()){
                    rowToDelete = i
                    break
                }
            }
        }
        if (rowToDelete != -1){
            expenseTable.removeViewAt(rowToDelete)
        }
    }

    fun updateSource(source: String, amount: String, category: String, remove: Boolean) {
        var rowToDelete = -1
        for (i in 0 until expenseTable.childCount){
            val row = expenseTable.getChildAt(i) as TableRow
            for (j in 0 until row.childCount){
                val view = row.getChildAt(j) as TextView
                if (view.text.toString() == source){
                    rowToDelete = i
                    break
                }
            }
        }
        if (rowToDelete != -1){
            expenseTable.removeViewAt(rowToDelete)
            if (expenseTable.childCount == 0) {
                addRows("", "" , "")
            }
        }
        if (!remove) {
            addRows(source, amount, category)
        }
    }

    private fun requestCategories() {
        categories = ModelController.getCategories()
        categories.add(0, "")
    }

    fun reset() {
        amounts = ArrayList()
        expenseTable.removeAllViews()
    }
}
