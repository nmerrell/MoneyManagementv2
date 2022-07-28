package nathanmerrell.com.moneymanagement.listeners

import android.content.Context
import android.content.Intent
import android.view.*

import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import nathanmerrell.com.moneymanagement.ArchiveActivity
import nathanmerrell.com.moneymanagement.R
import nathanmerrell.com.moneymanagement.dialog.AboutDialog

open class OptionsMenuListener(private val context: Context, private val activity: FragmentActivity) : PopupMenu.OnMenuItemClickListener {
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.settings -> fileSettingsClick()
            R.id.archive -> fileArchiveClick()
            R.id.about -> fileAboutClick()
        }
        return true
    }

    private fun fileSettingsClick() {
        Toast.makeText(context, "Settings", Toast.LENGTH_LONG).show()
    }

    private fun fileAboutClick() {
        val aboutDialog = AboutDialog.newInstance()
        aboutDialog.show(activity.supportFragmentManager, "About Dialog")
    }

    private fun fileArchiveClick() {
        val intent = Intent(activity, ArchiveActivity::class.java)
        activity.startActivity(intent)
    }

}

