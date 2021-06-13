package ir.mohammadhf.birthdays.feature.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.subjects.BehaviorSubject
import ir.mohammadhf.birthdays.R
import ir.mohammadhf.birthdays.databinding.DialogChooseBirthdayBinding
import ir.mohammadhf.birthdays.utils.DateDataGenerator
import javax.inject.Inject

@AndroidEntryPoint
class ChooseBirthdayDialog : BottomSheetDialogFragment() {
    private var _binding: DialogChooseBirthdayBinding? = null
    private val binding
        get() = _binding!!

    val selectedDateBehaveSub = BehaviorSubject.create<Array<Int?>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogChooseBirthdayBinding.inflate(
            inflater, container, false
        )

        binding.run {
            monthNp.let {
                it.minValue = 1
                it.maxValue = 12
                it.displayedValues = resources.getStringArray(R.array.months)
            }

            dayNp.let {
                it.minValue = 1
                it.maxValue = 31
            }

            yearNp.let {
                it.minValue = DateDataGenerator.minYearValue
                it.maxValue = DateDataGenerator.maxYearValue
            }

            showYearSb.setOnCheckedChangeListener { _, isChecked ->
                yearNp.visibility = if (isChecked) VISIBLE else GONE
            }

            cancelTv.setOnClickListener {
                dismiss()
            }

            chooseBtn.setOnClickListener {
                selectedDateBehaveSub.onNext(
                    arrayOf(
                        if (showYearSb.isChecked) yearNp.value else null,
                        monthNp.value,
                        dayNp.value
                    )
                )
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}