package com.example.calendar.customView

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import kotlinx.android.parcel.Parcelize

class LifecycleOwnerNotFoundException(message: String? = null): Throwable(message)

interface MvvmViewState: Parcelable

interface MvvmViewModel<T: MvvmViewState> {
    var state: T?
}

interface MvvmView<V: MvvmViewState, T: MvvmViewModel<V>> {
    val viewModel: T
    fun onLifecycleOwnerAttached(lifecycleOwner: LifecycleOwner)
}

@Parcelize
class MvvmViewStateWrapper(
    val superState: Parcelable?,
    val state: MvvmViewState?
): Parcelable

abstract class MvvmConstraintLayout<V: MvvmViewState, T: MvvmViewModel<V>>
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),
    MvvmView<V, T> {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lifecycleOwner = context as? LifecycleOwner ?: throw LifecycleOwnerNotFoundException()
        onLifecycleOwnerAttached(lifecycleOwner)
    }

    override fun onSaveInstanceState() =
        MvvmViewStateWrapper(super.onSaveInstanceState(), viewModel.state)

    @Suppress("UNCHECKED_CAST")
    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is MvvmViewStateWrapper) {
            viewModel.state = state.state as V?
            super.onRestoreInstanceState(state.superState)
        }
    }
}