package ir.mohammadhf.birthdays.data.model

abstract class BirthdayMulti {
    companion object TYPES {
        const val TYPE_TITLE = 1
        const val TYPE_PERSON = 2
    }
    abstract fun isEqualTo(otherMulti: BirthdayMulti): Boolean

    abstract fun getType(): Int
}

data class BirthdayPerson(
    val id: Long,
    val name: String,
    val daysLeft: Int,
    val image: String,
    val age: Int?
): BirthdayMulti() {
    override fun isEqualTo(otherMulti: BirthdayMulti): Boolean =
        this == (otherMulti as BirthdayPerson)

    override fun getType(): Int = TYPE_PERSON
}

data class BirthdayTitle(
    val text: String
): BirthdayMulti() {
    override fun isEqualTo(otherMulti: BirthdayMulti): Boolean =
        this == (otherMulti as BirthdayTitle)

    override fun getType(): Int = TYPE_TITLE
}

