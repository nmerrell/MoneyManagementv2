package nathanmerrell.com.moneymanagement.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ErrorDialog : DialogFragment() {

    private var message: String? = null
    private var errorTitle = ""
    private var retDialog: AlertDialog? = null

    fun returnDialog(dlg: AlertDialog) {
        this.retDialog = dlg
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity).setTitle(errorTitle)
                .setMessage(message)
                .setPositiveButton("OK") { _, _ ->
                    if (retDialog != null)
                        retDialog!!.show()
                }

        val thisDlg = builder.show()

        thisDlg.window?.setLayout(900, 500)

        return thisDlg
    }

    companion object {

        fun newInstance(msg: String, errorTitle: String): ErrorDialog {
            val dlg = ErrorDialog()
            dlg.message = msg
            dlg.errorTitle = errorTitle
            return dlg
        }
    }
}
