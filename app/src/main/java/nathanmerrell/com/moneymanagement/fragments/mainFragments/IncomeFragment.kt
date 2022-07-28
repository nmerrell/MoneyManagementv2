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
import nathanmerrell.com.moneymanagement.dialog.AddIncomeDialog
import nathanmerrell.com.moneymanagement.listeners.IncomeListener
import nathanmerrell.com.moneymanagement.utils.DropDownListItem
import java.text.NumberFormat

@TargetApi(Build.VERSION_CODES.O)
class IncomeFragment : Fragment() {
    private lateinit var incomeTable: TableLayout
    private lateinit var amounts: ArrayList<DropDownListItem>
    private lateinit var addIncomeButton: FloatingActionButton
    private lateinit var listener: IncomeListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.income_fragment, container, false)

        incomeTable = view.findViewById(R.id.incomeTable)

        addIncomeButton = view.findViewById(R.id.addIncomeButton)

        fillInView()

        addIncomeButton.setOnClickListener {
            addSourceItem("", "", true)
        }

        return view
    }
    companion object {

        fun newInstance(): IncomeFragment {
            return IncomeFragment()
        }
    }
    private fun fillInView() {
        amounts = ModelController.getAmounts(resources.getString(R.string.income))
        for (i in 0 until amounts.size){
            addRows(amounts[i].text!!, amounts[i].value!!)
        }
        if (amounts.isEmpty())
            addRows("","")

    }

    fun configureListener(listener: IncomeListener) {
        this.listener = listener
    }

    fun addRows(source: String, amount: String){
        if (incomeTable.childCount > 0) {
            checkTable()
        }
        val tableRow = TableRow(context)
        tableRow.setPadding(0, 10, 0, 10)

        val addSource = TextView(context)
        val addIncome = TextView(context)

        tableRow.id = incomeTable.childCount

        addSource.setTextColor(Color.BLACK)
        addSource.id = incomeTable.childCount
        addSource.setBackgroundColor(Color.TRANSPARENT)
        addSource.setHint(R.string.sourceHint)
        addSource.textSize = 20f
        addSource.typeface = Typeface.SANS_SERIF
        addSource.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

        addIncome.setTextColor(Color.BLACK)
        addIncome.setBackgroundColor(Color.TRANSPARENT)
        addIncome.setHint(R.string.incomeHint)
        addIncome.id = incomeTable.childCount
        addIncome.textSize = 20f
        addIncome.typeface = Typeface.SANS_SERIF
        addIncome.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

        if (source.isNotEmpty() && amount.isNotEmpty()) {
            val format = NumberFormat.getCurrencyInstance()
            val amtDouble = amount.toDouble()
            val amt = format.format(amtDouble)
            addIncome.text = Editable.Factory.getInstance().newEditable(amt)
            addSource.text = Editable.Factory.getInstance().newEditable(source.capitalize())
        }

        tableRow.addView(addSource)
        tableRow.addView(addIncome)

        tableRow.setOnClickListener {
            if (addSource.text.toString().isNotEmpty() || addIncome.text.toString().isNotEmpty()){
                addSourceItem(addSource.text.toString(), addIncome.text.toString(), false)
            }
        }

        incomeTable.addView(tableRow)
    }

    private fun addSourceItem(src: String, amt: String, isNew: Boolean){
        var amount = amt
        if (amount.isNotEmpty() && amount.first() == '$') {
            amount = amt.substring(1)
        }
        val dialog = AddIncomeDialog.newInstance(listener, src, amount, isNew)
        dialog.show(requireActivity().supportFragmentManager, "Income Source Add")
    }

    private fun checkTable() {
        var rowToDelete = -1
        for (i in 0 until incomeTable.childCount){
            val row = incomeTable.getChildAt(i) as TableRow
            for (j in 0 until row.childCount){
                val view = row.getChildAt(j) as TextView
                if (view.text.isEmpty()){
                    rowToDelete = i
                    break
                }
            }
        }
        if (rowToDelete != -1){
            incomeTable.removeViewAt(rowToDelete)
        }
    }

    fun updateSource(source: String, amount: String, remove: Boolean) {
        var rowToDelete = -1
        for (i in 0 until incomeTable.childCount){
            val row = incomeTable.getChildAt(i) as TableRow
            for (j in 0 until row.childCount){
                val view = row.getChildAt(j) as TextView
                if (view.text.toString() == source){
                    rowToDelete = i
                    break
                }
            }
        }
        if (rowToDelete != -1){
            incomeTable.removeViewAt(rowToDelete)
            if (incomeTable.childCount == 0) {
                addRows("", "")
            }
        }
        if (!remove) {
            addRows(source, amount)
        }
    }

    fun reset() {
        amounts = ArrayList()
        incomeTable.removeAllViews()
    }
}
