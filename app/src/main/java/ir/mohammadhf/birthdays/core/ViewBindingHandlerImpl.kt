package ir.mohammadhf.birthdays.core

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import ir.mohammadhf.birthdays.core.bases.BaseFragment

class ViewBindingHandlerImpl<T : ViewBinding> : ViewBindingHandler<T>, LifecycleObserver {
    var binding: T? = null
    var lifecycle: Lifecycle? = null

    private lateinit var fragmentName: String

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroyView() {
        lifecycle?.removeObserver(this)
        lifecycle = null
        binding = null
    }

    override fun initBinding(binding: T, fragment: BaseFragment<T>, onBind: (T.() -> Unit)?): View {
        this.binding = binding
        lifecycle = fragment.viewLifecycleOwner.lifecycle
        lifecycle?.addObserver(this)
        fragmentName = fragment::class.simpleName ?: "N/A"
        onBind?.invoke(binding)
        return binding.root
    }

    override fun requireBinding(block: (T.() -> Unit)?): T =
        binding?.apply { block?.invoke(this) }
            ?: throw IllegalStateException(
                "Accessing binding outside of fragment lifecycle: $fragmentName"
            )
}