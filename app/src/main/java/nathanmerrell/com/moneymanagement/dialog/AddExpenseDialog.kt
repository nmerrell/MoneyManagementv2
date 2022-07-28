package nathanmerrell.com.moneymanagement.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import nathanmerrell.com.moneymanagement.R
import nathanmerrell.com.moneymanagement.listeners.ExpenseListener
import nathanmerrell.com.moneymanagement.utils.DropDownListItem

class AddExpenseDialog : DialogFragment(){
    private lateinit var listener: ExpenseListener
    private var source: String? = null
    private var amount: String? = null
    private var category: String? = null
    private lateinit var categories: ArrayList<String>
    private var isNew: Boolean = false
    private lateinit var thisActivity: FragmentActivity
    private lateinit var expenseDialog: AlertDialog
    private lateinit var addExpenseView: View
    private lateinit var sourceName: EditText
    private lateinit var sourceAmount: EditText
    private lateinit var sourceCategory: Spinner
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        addExpenseView = LayoutInflater.from(context).inflate(R.layout.dialog_add_expense, null)

        sourceName = addExpenseView.findViewById(R.id.expenseName)
        sourceAmount = addExpenseView.findViewById(R.id.expenseAmount)
        sourceCategory = addExpenseView.findViewById(R.id.expenseCategory)

        thisActivity = requireActivity()

        val spinnerAdapter = context?.let { ArrayAdapter(it, R.layout.spinner_item, categories) }
        spinnerAdapter?.setDropDownViewResource(R.layout.spinner_dropdown_list)
        sourceCategory.adapter = spinnerAdapter

        configureButtons()

        if (source!!.isNotEmpty() && amount!!.isNotEmpty() && category!!.isNotEmpty()) {
            sourceName.text = Editable.Factory.getInstance().newEditable(source!!)
            sourceName.isEnabled = false
            sourceAmount.text = Editable.Factory.getInstance().newEditable(amount!!)
            for (i in 0 until categories.size) {
                if (categories[i].toLowerCase() == category!!.toLowerCase()){
                    sourceCategory.setSelection(i)
                    break
                }
            }
        }

        expenseDialog = AlertDialog.Builder(context)
                .setView(addExpenseView)
                .setCancelable(false)
                .show()

        return expenseDialog
    }

    private fun configureButtons() {
        val positive: Button = addExpenseView.findViewById(R.id.expensePositiveButton)
        positive.setOnClickListener {
            if (sourceName.text.toString().isNotEmpty() && sourceAmount.text.toString().isNotEmpty() && sourceCategory.selectedItem.toString().isNotEmpty()) {
                if (isNew) {
                    listener.saveSource(sourceName.text.toString(), sourceAmount.text.toString(), "Expenses", sourceCategory.selectedItem.toString())
                }
                else {
                    listener.updateSource(sourceName.text.toString(), sourceAmount.text.toString(), "Expenses", sourceCategory.selectedItem.toString())
                }
            }
            else {
                val dialog = ErrorDialog.newInstance("All fields are required", "Missing Fields")
                dialog.returnDialog(expenseDialog)
                dialog.show((if (activity != null)requireActivity() else thisActivity).supportFragmentManager, "Expense Add Missing Fields")
            }
        }

        val neutral: Button = addExpenseView.findViewById(R.id.expenseNeutralButton)
        neutral.setOnClickListener {
            this.dismiss()
        }

        val negative: Button = addExpenseView.findViewById(R.id.expenseNegativeButton)
        negative.visibility = if (!isNew) View.VISIBLE else View.INVISIBLE
        negative.setOnClickListener {
            listener.deleteSource(sourceName.text.toString(), sourceAmount.text.toString(), "Expenses", sourceCategory.selectedItem.toString())
        }
    }

    companion object {
        fun newInstance(listener: ExpenseListener, item: DropDownListItem, categories: ArrayList<String>, isNew: Boolean): AddExpenseDialog{
            val fragment = AddExpenseDialog()
            fragment.listener = listener
            fragment.source = item.text
            fragment.amount = item.value
            fragment.category = item.category
            fragment.categories = categories
            fragment.isNew = isNew
            return fragment
        }
    }
}