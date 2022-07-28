package nathanmerrell.com.moneymanagement.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import nathanmerrell.com.moneymanagement.R

class AboutDialog : DialogFragment() {
    private lateinit var thisDialog: AlertDialog
    private lateinit var aboutView: View
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        aboutView = LayoutInflater.from(activity).inflate(R.layout.dialog_menu_about, null)

        val builder = AlertDialog.Builder(context)
                .setView(aboutView)

        thisDialog = builder.create()

        configureView()

        createText()

        return thisDialog
    }

    private fun configureView() {
        val positiveButton: Button = aboutView.findViewById(R.id.aboutPositiveButton)
        positiveButton.setOnClickListener {
            this.dismiss()
        }
    }

    private fun createText() {
        val aboutInformationText = "About Information goes in here"
        val aboutText: TextView = aboutView.findViewById(R.id.aboutInformation)
        aboutText.text = aboutInformationText
    }

    companion object {
        fun newInstance() : AboutDialog {
            return AboutDialog()
        }
    }
}