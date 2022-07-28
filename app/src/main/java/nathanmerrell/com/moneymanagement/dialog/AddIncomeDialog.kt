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
import nathanmerrell.com.moneymanagement.listeners.IncomeListener

class AddIncomeDialog : DialogFragment(){
    private lateinit var listener: IncomeListener
    private var source: String? = null
    private var amount: String? = null
    private var isNew: Boolean = false
    private lateinit var incomeDialog: AlertDialog
    private lateinit var thisActivity: FragmentActivity
    private lateinit var addItemView: View
    private lateinit var sourceName: EditText
    private lateinit var sourceAmount: EditText
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        addItemView = LayoutInflater.from(context).inflate(R.layout.dialog_add_item, null)

        sourceName = addItemView.findViewById(R.id.sourceName)
        sourceAmount = addItemView.findViewById(R.id.sourceAmount)

        configureButtons()

        thisActivity = requireActivity()

        if (source!!.isNotEmpty() && amount!!.isNotEmpty()) {
            sourceName.text = Editable.Factory.getInstance().newEditable(source!!)
            sourceName.isEnabled = false
            sourceAmount.text = Editable.Factory.getInstance().newEditable(amount!!)
        }

        val moneyCategory = ArrayList<String>()
        moneyCategory.add("")

        incomeDialog = AlertDialog.Builder(context)
                .setView(addItemView)
                .setCancelable(false)
                .show()

        return incomeDialog
    }

    private fun configureButtons() {
        val positive: Button = addItemView.findViewById(R.id.incomePositiveButton)
        positive.setOnClickListener {
            if (sourceName.text.toString().isNotEmpty() && sourceAmount.text.toString().isNotEmpty()) {
                if (isNew) {
                    listener.saveSource(sourceName.text.toString(), sourceAmount.text.toString(), "Income")
                }
                else {
                    listener.updateSource(sourceName.text.toString(), sourceAmount.text.toString(), "Income")
                }
            }
            else {
                val dialog = ErrorDialog.newInstance("All fields are required", "Missing Fields")
                dialog.returnDialog(incomeDialog)
                dialog.show((if (activity != null)requireActivity() else thisActivity).supportFragmentManager, "Expense Add Missing Fields")
            }
        }

        val neutral: Button = addItemView.findViewById(R.id.incomeNeutralButton)
        neutral.setOnClickListener {
            this.dismiss()
        }

        val negative: Button = addItemView.findViewById(R.id.incomeNegativeButton)
        negative.visibility = if (!isNew) View.VISIBLE else View.INVISIBLE
        negative.setOnClickListener {
            listener.deleteSource(sourceName.text.toString(), sourceAmount.text.toString(), "Income")
        }
    }

    companion object {
        fun newInstance(listener: IncomeListener, source: String, amount: String, isNew: Boolean): AddIncomeDialog{
            val fragment = AddIncomeDialog()
            fragment.listener = listener
            fragment.source = source
            fragment.amount = amount
            fragment.isNew = isNew
            return fragment
        }
    }
}