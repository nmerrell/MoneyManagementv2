package nathanmerrell.com.moneymanagement.fragments.mainFragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.ArrayList

class FragmentAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
    private val fragmentList = ArrayList<Fragment>()
    override fun getCount(): Int {
        return fragmentList.size
    }
    override fun getItem(position:Int):Fragment {
        return fragmentList[position]
    }
    fun addItem(position:Int, fragment:Fragment) {
        fragmentList.add(position, fragment)
    }
}
