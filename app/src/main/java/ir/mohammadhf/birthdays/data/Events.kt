package ir.mohammadhf.birthdays.data

data class BirthdaySelectEvent(
    val id: Long
)

data class GroupListSelectEvent(
    val groups: ArrayList<Long>
)

data class FrameSelectEvent(
    val id: Long
)