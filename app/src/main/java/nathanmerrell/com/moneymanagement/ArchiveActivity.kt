package nathanmerrell.com.moneymanagement

import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import nathanmerrell.com.moneymanagement.fragments.archiveFragment.ArchiveFragment
import nathanmerrell.com.moneymanagement.fragments.mainFragments.FragmentAdapter

class ArchiveActivity : FragmentActivity() {
    private lateinit var archiveFragment: ArchiveFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive_main)

        archiveFragment = ArchiveFragment.newInstance()

        val viewPager: ViewPager = findViewById(R.id.pager)
        val adapter = FragmentAdapter(supportFragmentManager)

        adapter.addItem(0, archiveFragment)

        viewPager.offscreenPageLimit = 1
        viewPager.adapter = adapter

        findViewById<Button>(R.id.archiveReturn).setOnClickListener {
            this.onBackPressed()
        }
    }
}