package ir.mohammadhf.birthdays.core

import android.view.View
import androidx.viewbinding.ViewBinding
import ir.mohammadhf.birthdays.core.bases.BaseFragment

interface ViewBindingHandler<T : ViewBinding> {
    fun initBinding(binding: T, fragment: BaseFragment<T>, onBind: (T.() -> Unit)?): View

    fun requireBinding(block: (T.() -> Unit)?): T
}