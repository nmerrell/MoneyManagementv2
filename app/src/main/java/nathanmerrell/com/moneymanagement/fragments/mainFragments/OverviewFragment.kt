package nathanmerrell.com.moneymanagement.fragments.mainFragments

import android.annotation.TargetApi
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import nathanmerrell.com.moneymanagement.MainActivity
import nathanmerrell.com.moneymanagement.ModelController
import nathanmerrell.com.moneymanagement.R
import nathanmerrell.com.moneymanagement.utils.DropDownListItem
import java.text.NumberFormat
import java.util.*

@TargetApi(Build.VERSION_CODES.O)
class OverviewFragment : Fragment() {
    private lateinit var expenses: ArrayList<DropDownListItem>
    private lateinit var income: ArrayList<DropDownListItem>
    private lateinit var availableAmt: TextView
    private lateinit var expenseAmt: TextView
    private lateinit var totalAmt: TextView
    private var totals = 0.0
    private lateinit var overviewView: View
    var reset = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        overviewView = inflater.inflate(R.layout.activity_main_screen, container, false)
        configureTable()
        gatherAmounts()
        configureButtons()
        return overviewView
    }

    companion object {
        fun newInstance(): OverviewFragment = OverviewFragment()
    }

    fun gatherAmounts(){
        income = if (!reset) ModelController.getAmounts("Income") else ArrayList()
        expenses = if (!reset) ModelController.getAmounts("Expenses") else ArrayList()

        var incomeAmount = 0.0
        var expenseAmount = 0.0

        for (i in 0 until income.size){
            incomeAmount += income[i].value!!.toDouble()
        }
        for (i in 0 until expenses.size){
            expenseAmount += expenses[i].value!!.toDouble()
        }

        val format = NumberFormat.getCurrencyInstance()
        val incomesAmount = format.format(incomeAmount)
        val expensesAmount = format.format(expenseAmount)

        availableAmt.text = incomesAmount.toString()
        expenseAmt.text = expensesAmount.toString()
        val difference = incomeAmount - expenseAmount
        val percentDifference = 1.0 - expenseAmount / incomeAmount
        totals = difference
        totalAmt.text = (format.format(difference)).toString()

        if (totals > 0 && percentDifference >= .5) {
            totalAmt.setTextColor(resources.getColor(R.color.darkGreen, null))
        }
        else if (totals > 0 && percentDifference < .5){
            totalAmt.setTextColor(resources.getColor(R.color.caution, null))
        }
        else {
            totalAmt.setTextColor(Color.RED)
        }
    }

    private fun configureTable() {
        availableAmt = overviewView.findViewById(R.id.overviewIncomeAmount)
        expenseAmt = overviewView.findViewById(R.id.overviewExpenseAmount)
        totalAmt = overviewView.findViewById(R.id.overviewTotalAmount)
    }

    private fun configureButtons() {
        val archiveMonth: Button = overviewView.findViewById(R.id.archiveMonthButton)
        val onClickListener = archiveMonth.setOnClickListener {
            val localMonth = Date().toString().substring(4, 7)
            val localYear = Date().toString()
                .substring(Date().toString().lastIndex - 3, Date().toString().lastIndex + 1)
            ModelController.deleteDBEntries(false)
            ModelController.archiveData(income, expenses, localMonth, localYear)
            (activity as MainActivity).resetEntries()
        }

        val newMonth: Button = overviewView.findViewById(R.id.resetMonthButton)
        newMonth.setOnClickListener {
            ModelController.deleteDBEntries(true)
            (activity as MainActivity).resetEntries()
        }
    }

}