package nathanmerrell.com.moneymanagement.fragments.archiveFragment

import android.annotation.TargetApi
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import nathanmerrell.com.moneymanagement.ModelController
import nathanmerrell.com.moneymanagement.R
import nathanmerrell.com.moneymanagement.utils.DropDownListItem
import java.text.DateFormatSymbols
import java.text.NumberFormat
import java.util.*
import android.widget.AdapterView
import androidx.fragment.app.Fragment


@TargetApi(Build.VERSION_CODES.O)
class ArchiveFragment : Fragment() {
    private lateinit var archiveView: View
    private lateinit var archiveIncome: ArrayList<DropDownListItem>
    private lateinit var archiveExpense: ArrayList<DropDownListItem>
    private var selectedMonth: String? = null
    private var selectedYear: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        archiveView = inflater.inflate(R.layout.archive_fragment, container, false)
        archiveExpense = ArrayList()
        archiveIncome = ArrayList()

        archiveMonthYear()
        return archiveView
    }
    companion object {
        fun newInstance(): ArchiveFragment = ArchiveFragment()
    }

    private fun requestArchiveData() {
        if (selectedMonth != null && selectedYear != null) {
            val list = ModelController.requestArchiveData(selectedMonth!!, selectedYear!!)
            archiveIncome = list[0]
            archiveExpense = list[1]

            createTable()
        }
    }

    private fun archiveMonthYear() {
        val monthSpinner: Spinner = archiveView.findViewById(R.id.archiveMonth)
        monthSpinner.adapter = spinnerAdapter(monthList())
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedMonth = DateFormatSymbols().months[position]
                if (selectedYear != null) {
                    requestArchiveData()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        val yearSpinner: Spinner = archiveView.findViewById(R.id.archiveYear)
        yearSpinner.adapter = spinnerAdapter(yearList())
        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedYear = yearSpinner.selectedItem.toString()
                if (selectedMonth != null) {
                    requestArchiveData()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun spinnerAdapter(list: ArrayList<String>): ArrayAdapter<String>? {
        val adapter = context?.let { ArrayAdapter(it, R.layout.spinner_item, list) }
        if (adapter != null) {
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_list)
        }
        return adapter
    }

    private fun monthList(): ArrayList<String> {
        val months = ArrayList<String>()
        val monthList = DateFormatSymbols().months
        for (month in monthList) {
            months.add(month)
        }
        return months
    }

    private fun yearList(): ArrayList<String> {
        val years = ArrayList<String>()
        var incrementYear = 2014
        for (i in 0 until 10) {
            years.add((++incrementYear).toString())
        }
        return years
    }

    private fun createTable() {
        val incomeTable: TableLayout = archiveView.findViewById(R.id.incomeTable)

        val expenseTable: TableLayout = archiveView.findViewById(R.id.expenseTable)

        if (archiveIncome.isEmpty()) {
            incomeTable.removeAllViews()
            incomeTable.addView(createRows("No Income archival data", null, null, true))
        }
        if (archiveExpense.isEmpty()) {
            expenseTable.removeAllViews()
            expenseTable.addView(createRows("No Expense archival data", null, null, true))
        }

        for (source in archiveIncome) {
            if (archiveIncome.indexOf(source) == 0) {
                incomeTable.removeAllViews()
            }
            incomeTable.addView(createRows(source.text!!, source.value!!, null, true))
        }

        for (source in archiveExpense) {
            if (archiveExpense.indexOf(source) == 0) {
                expenseTable.removeAllViews()
            }
            expenseTable.addView(createRows(source.text!!, source.value!!, source.category, false))
        }
    }

    private fun createRows(source: String, amount: String?, category: String?, income: Boolean): TableRow {
        val tableRow = TableRow(context)
        tableRow.setPadding(0, 10, 0, 10)

        val addSource = TextView(context)
        val addAmount = TextView(context)
        val addCategory = TextView(context)

        addSource.setTextColor(Color.BLACK)
        addSource.setBackgroundColor(Color.TRANSPARENT)
        addSource.textSize = 20f
        addSource.typeface = Typeface.SANS_SERIF
        addSource.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

        addAmount.setTextColor(Color.BLACK)
        addAmount.setBackgroundColor(Color.TRANSPARENT)
        addAmount.textSize = 20f
        addAmount.typeface = Typeface.SANS_SERIF
        addAmount.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

        addCategory.setTextColor(Color.BLACK)
        addCategory.setBackgroundColor(Color.TRANSPARENT)
        addCategory.textSize = 20f
        addCategory.typeface = Typeface.SANS_SERIF
        addCategory.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

        addSource.text = source

        if (amount != null) {
            val format = NumberFormat.getCurrencyInstance()
            val amtDouble = amount.toDouble()
            val amt = format.format(amtDouble)
            addAmount.text = amt
        }

        tableRow.addView(addSource)
        tableRow.addView(addAmount)

        if (!income) {
            addCategory.text = category ?: ""
            tableRow.addView(addCategory)
        }

        return tableRow
    }
}