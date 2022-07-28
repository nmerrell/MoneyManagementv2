package nathanmerrell.com.moneymanagement.fragments.mainFragments

import android.annotation.TargetApi
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import nathanmerrell.com.moneymanagement.ModelController
import nathanmerrell.com.moneymanagement.R
import nathanmerrell.com.moneymanagement.utils.DropDownListItem
import java.text.DecimalFormat
import java.text.NumberFormat

@TargetApi(Build.VERSION_CODES.O)
class BreakdownFragment : Fragment() {
    private lateinit var expenses: ArrayList<DropDownListItem>
    private lateinit var income: ArrayList<DropDownListItem>
    private lateinit var breakdownView: View
    private lateinit var expenseBreakdownTableLayout: TableLayout
    private lateinit var incomeView: TextView
    private lateinit var expenseView: TextView
    private lateinit var pieChartView: PieChart
    private lateinit var colors: ArrayList<Int>
    private val categories = ModelController.getCategories()
    private var gasAmount = 0.0
    private var insuranceAmount = 0.0
    private var foodAmount = 0.0
    private var housingAmount = 0.0
    private var carAmount = 0.0
    private var leisureAmount = 0.0
    private var shoppingAmount = 0.0
    private var fitnessAmount = 0.0
    var reset = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        breakdownView = inflater.inflate(R.layout.activity_expense_breakdown, container, false)

        incomeView = breakdownView.findViewById(R.id.breakdownIncome)
        expenseView = breakdownView.findViewById(R.id.breakdownExpense)
        pieChartView = breakdownView.findViewById(R.id.breakdownPieChart)
        expenseBreakdownTableLayout = breakdownView.findViewById(R.id.breakdownExpenseTable)

        gatherAmounts()
        colors = createColorArray()

        updateValues()
        
        return breakdownView
    }

    companion object {
        fun newInstance(): BreakdownFragment = BreakdownFragment()
    }

    private fun numberFormat(value: Double) : String {
        val numberFormat = NumberFormat.getCurrencyInstance()
        return numberFormat.format(value)
    }

    fun updateValues() {
        var incomeAmount = 0.0
        var expenseAmount = 0.0

        gatherAmounts()

        for (i in 0 until income.size){
            incomeAmount += income[i].value!!.toDouble()
        }
        for (i in 0 until expenses.size){
            expenseAmount += expenses[i].value!!.toDouble()
            when (expenses[i].category!!) {
                "Gas" -> {
                    gasAmount += expenses[i].value!!.toDouble()
                }
                "Insurance" -> {
                    insuranceAmount += expenses[i].value!!.toDouble()
                }
                "Car" -> {
                    carAmount += expenses[i].value!!.toDouble()
                }
                "Food" -> {
                    foodAmount += expenses[i].value!!.toDouble()
                }
                "Housing" -> {
                    housingAmount += expenses[i].value!!.toDouble()
                }
                "Fitness" -> {
                    fitnessAmount += expenses[i].value!!.toDouble()
                }
                "Shopping" -> {
                    shoppingAmount += expenses[i].value!!.toDouble()
                }
                "Leisure" -> {
                    leisureAmount += expenses[i].value!!.toDouble()
                }
            }
        }
        val inc = numberFormat(incomeAmount)
        val exp = numberFormat(expenseAmount)

        incomeView.text = Editable.Factory.getInstance().newEditable(inc)
        expenseView.text = Editable.Factory.getInstance().newEditable(exp)

        createPieChart()
        breakdownLegend()
    }

    private fun gatherAmounts(){
        expenses = if (!reset) ModelController.getAmounts("Expenses") else ArrayList()
        income = if (!reset) ModelController.getAmounts("Income") else ArrayList()

        gasAmount = 0.0
        insuranceAmount = 0.0
        housingAmount = 0.0
        carAmount = 0.0
        foodAmount = 0.0
        fitnessAmount = 0.0
        leisureAmount = 0.0
        shoppingAmount = 0.0
    }

    private fun createPieChart(): PieChart {
        val xEntries = ArrayList<PieEntry>()
        val expensesAmount = ArrayList<Float>()
        val expenseType = ArrayList<String>()

        for (i in 0 until categories.size){
            expenseType.add(categories[i])
        }

        expensesAmount.add(gasAmount.toFloat())
        expensesAmount.add(insuranceAmount.toFloat())
        expensesAmount.add(carAmount.toFloat())
        expensesAmount.add(foodAmount.toFloat())
        expensesAmount.add(housingAmount.toFloat())
        expensesAmount.add(fitnessAmount.toFloat())
        expensesAmount.add(shoppingAmount.toFloat())
        expensesAmount.add(leisureAmount.toFloat())

        val removedAmounts = ArrayList<Float>()
        val removedTypes = ArrayList<String>()

        for (i in 0 until expensesAmount.size){
            if (expensesAmount[i] == 0.0f){
                removedAmounts.add(expensesAmount[i])
                removedTypes.add(expenseType[i])
            }
        }
        for (i in 0 until removedAmounts.size){
            expensesAmount.remove(removedAmounts[i])
        }
        for (i in 0 until removedTypes.size){
            expenseType.remove(removedTypes[i])
        }

        val totalAmount = (0 until expensesAmount.size).sumByDouble { expensesAmount[it].toDouble() }

        val percentAmounts = ArrayList<Float>()

        (0 until expensesAmount.size).mapTo(percentAmounts) { ((expensesAmount[it] / totalAmount) * 100).toFloat() }

        val df = DecimalFormat("#.##")

        for (i in 0 until expenseType.size){
            expenseType[i] = "${expenseType[i]} - ${df.format(percentAmounts[i])}%"
        }

        (0 until expensesAmount.size).mapTo(xEntries) { PieEntry(percentAmounts[it], expenseType[it]) }

        val pieDataSet = PieDataSet(xEntries, "")
        pieDataSet.valueTextSize = 12f

        pieDataSet.sliceSpace = 3f
        pieDataSet.selectionShift = 0f

        pieDataSet.colors = colors

        val pieData = PieData(pieDataSet)
        pieData.setValueFormatter(PercentFormatter())
        pieData.setValueTextSize(0f)
        pieChartView.data = pieData

        val description = Description()
        description.text = ""
        pieChartView.description = description

        pieChartView.legend.isEnabled = false

        pieChartView.isDrawHoleEnabled = false
        pieChartView.setEntryLabelTextSize(18f)
        pieChartView.setEntryLabelColor(Color.BLACK)

        pieChartView.minimumHeight = resources.displayMetrics.heightPixels - 1000
        pieChartView.invalidate()

        return pieChartView
    }

    private fun createColorArray() : ArrayList<Int>{

        val colors = ArrayList<Int>()
        colors.add(Color.MAGENTA)
        colors.add(Color.RED)
        colors.add(Color.BLUE)
        colors.add(Color.YELLOW)
        colors.add(Color.LTGRAY)
        colors.add(Color.GREEN)
        colors.add(Color.CYAN)

        return colors
    }

    private fun breakdownLegend() {
        expenseBreakdownTableLayout.removeAllViews()
        for (i in 0 until categories.size) {

            val tableRow = TableRow(context)
            tableRow.setPadding(0, 10, 0, 10)

            val categoryName = TextView(context)
            val categoryValue = TextView(context)

            categoryName.setTextColor(Color.BLACK)
            categoryName.setBackgroundColor(Color.TRANSPARENT)
            categoryName.textSize = 20f
            categoryName.typeface = Typeface.SANS_SERIF
            categoryName.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            categoryName.text = Editable.Factory.getInstance().newEditable(categories[i])

            categoryValue.setTextColor(Color.BLACK)
            categoryValue.setBackgroundColor(Color.TRANSPARENT)
            categoryValue.textSize = 20f
            categoryValue.typeface = Typeface.SANS_SERIF
            categoryValue.text = Editable.Factory.getInstance().newEditable(
                    when (i) {
                        0 -> numberFormat(gasAmount)
                        1 -> numberFormat(insuranceAmount)
                        2 -> numberFormat(carAmount)
                        3 -> numberFormat(foodAmount)
                        4 -> numberFormat(housingAmount)
                        5 -> numberFormat(fitnessAmount)
                        6 -> numberFormat(shoppingAmount)
                        7 -> numberFormat(leisureAmount)
                        else -> {numberFormat(0.0)}
                    }
            )

            tableRow.addView(categoryName)
            tableRow.addView(categoryValue)
            expenseBreakdownTableLayout.addView(tableRow)
        }
    }
}
