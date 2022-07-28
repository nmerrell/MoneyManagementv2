package nathanmerrell.com.moneymanagement.utils

class DropDownListItem(txt: String, value: String, category: String) {
    var text: String? = null
    var value: String? = null
    var category: String? = null

    init {
        text = txt
        this.value = value
        this.category = category
    }

    override fun toString(): String {
        return text!!
    }
}
