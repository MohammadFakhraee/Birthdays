package ir.mohammadhf.birthdays.core.bases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import io.reactivex.disposables.CompositeDisposable
import ir.mohammadhf.birthdays.MainActivity
import ir.mohammadhf.birthdays.core.ViewBindingHandler
import ir.mohammadhf.birthdays.core.ViewBindingHandlerImpl

abstract class BaseFragment<T : ViewBinding> : Fragment(),
    ViewBindingHandler<T> by ViewBindingHandlerImpl() {
    val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return initBinding(
            onCreateBinding(inflater, container), this
        ) {
            initial()
            subscribe()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity is MainActivity)
            (activity as MainActivity).changeBottomNavVisibility(isBottomNavShown())
    }

    abstract fun onCreateBinding(inflater: LayoutInflater, container: ViewGroup?): T

    abstract fun initial()

    abstract fun subscribe()

    abstract fun isBottomNavShown(): Boolean

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }
}