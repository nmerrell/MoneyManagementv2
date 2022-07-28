package nathanmerrell.com.moneymanagement

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import nathanmerrell.com.moneymanagement.fragments.mainFragments.*
import nathanmerrell.com.moneymanagement.listeners.ExpenseListener
import nathanmerrell.com.moneymanagement.listeners.IncomeListener
import nathanmerrell.com.moneymanagement.listeners.OptionsMenuListener


class MainActivity : FragmentActivity() {
    private lateinit var incomeFragment: IncomeFragment
    private lateinit var overviewFragment: OverviewFragment
    private lateinit var expensesFragment: ExpensesFragment
    private lateinit var breakdownFragment: BreakdownFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_frag)

        overviewFragment = OverviewFragment.newInstance()
        incomeFragment = IncomeFragment.newInstance()
        expensesFragment = ExpensesFragment.newInstance()
        breakdownFragment = BreakdownFragment.newInstance()

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setText("Overview"))
        tabLayout.addTab(tabLayout.newTab().setText("Income"))
        tabLayout.addTab(tabLayout.newTab().setText("Expenses"))
        tabLayout.addTab(tabLayout.newTab().setText("Expense Breakdown"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val viewPager: ViewPager = findViewById(R.id.pager)
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        val adapter = FragmentAdapter(supportFragmentManager)
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        adapter.addItem(0, overviewFragment)
        adapter.addItem(1, incomeFragment)
        adapter.addItem(2, expensesFragment)
        adapter.addItem(3, breakdownFragment)


        viewPager.offscreenPageLimit = 3
        viewPager.adapter = adapter

        configureIncome()
        configureExpense()
        configureSettingButton()
    }

    override fun onBackPressed() {
        //do nothing
    }

    private fun configureIncome(){
        incomeFragment.configureListener(object: IncomeListener{
            override fun deleteSource(source: String, amount: String, tag: String) {
                ModelController.deleteEntry(source, tag)
                incomeFragment.updateSource(source, amount, true)
                updateAmounts()
            }

            override fun updateSource(source: String, amount: String, tag: String) {
                ModelController.updateSource(source, amount, tag, "")
                incomeFragment.updateSource(source, amount, false)
                updateAmounts()
            }

            override fun saveSource(source: String, amount: String, tag: String) {
                ModelController.setAmounts(source, amount, tag, "")
                incomeFragment.addRows(source, amount)
                updateAmounts()
            }
        })
    }

    private fun configureExpense() {
        expensesFragment.configureListener(object: ExpenseListener{
            override fun saveSource(source: String, amount: String, tag: String, type: String) {
                ModelController.setAmounts(source, amount, tag, type)
                expensesFragment.addRows(source, amount, type)
                updateAmounts()
            }

            override fun updateSource(source: String, amount: String, tag: String, type: String) {
                ModelController.updateSource(source, amount, tag, type)
                expensesFragment.updateSource(source, amount, type, false)
                updateAmounts()
            }

            override fun deleteSource(source: String, amount: String, tag: String, type: String) {
                ModelController.deleteEntry(source, tag)
                expensesFragment.updateSource(source, amount, type, true)
                updateAmounts()
            }

        })
    }

    private fun updateAmounts(){
        overviewFragment.gatherAmounts()
        breakdownFragment.updateValues()
    }

    private fun configureSettingButton() {
        val menuButton: TextView = this.findViewById(R.id.settingsMenuOption)
        menuButton.setOnClickListener {
            val menu = PopupMenu(this, menuButton, Gravity.END)
            menu.menuInflater.inflate(R.menu.options_menu, menu.menu)
            menu.setOnMenuItemClickListener(OptionsMenuListener(this, this))
            menu.show()
        }
    }

    fun resetEntries() {
        overviewFragment.reset = true
        breakdownFragment.reset = true
        updateAmounts()
        overviewFragment.reset = false
        breakdownFragment.reset = false
        incomeFragment.reset()
        expensesFragment.reset()
    }
}
