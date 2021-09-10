package ir.mohammadhf.birthdays.feature.setting.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skydoves.colorpickerview.listeners.ColorListener
import io.reactivex.subjects.BehaviorSubject
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.databinding.DialogCreateGroupBinding

class CreateGroupDialog : BottomSheetDialogFragment() {
    private var _binding: DialogCreateGroupBinding? = null
    private val binding
        get() = _binding!!

    private var selectedColor: Int? = null
    private var selectedName: String? = null

    val createdGroupBehaveSub = BehaviorSubject.create<Group>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCreateGroupBinding.inflate(inflater, container, false)
        isCancelable = false

        binding.run {
            if (areFieldsFilled()) {
                groupNameEt.setText(selectedName)
                groupColorPicker.setInitialColor(selectedColor!!)
                colorPrevCiv.setColorFilter(selectedColor!!)
                changeAddBtnEnable()
            }

            groupColorPicker.setColorListener(object : ColorListener {
                override fun onColorSelected(color: Int, fromUser: Boolean) {
                    changeAddBtnEnable()
                    binding.colorPrevCiv.setColorFilter(color)
                    selectedColor = color
                }
            })

            groupNameEt.addTextChangedListener {
                selectedName = binding.groupNameEt.text.toString()
                changeAddBtnEnable()
            }

            cancelTv.setOnClickListener { dismiss() }

            chooseBtn.setOnClickListener {
                createdGroupBehaveSub.onNext(
                    Group(0L, binding.groupNameEt.text.toString(), selectedColor!!)
                )
            }
        }

        return binding.root
    }

    fun setDefaults(color: Int, name: String) {
        selectedColor = color
        selectedName = name
    }

    private fun changeAddBtnEnable() {
        binding.chooseBtn.isEnabled = areFieldsFilled()
    }

    private fun areFieldsFilled(): Boolean =
        selectedColor != null && !selectedName.isNullOrEmpty()
}