package com.example.calendar.helpers

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

// from https://github.com/Arello-Mobile/Moxy/blob/master/sample-github/src/main/java/com/arellomobile/mvp/sample/github/mvp/presenters/BasePresenter.java

open class BaseMvpSubscribe<View : MvpView> : MvpPresenter<View>() {
    private val compositeSubscription = CompositeDisposable();

    protected fun unsubscribeOnDestroy(disposable: Disposable) {
        compositeSubscription.add(disposable)
    }

    protected fun unsubscribeOnAll() {
        compositeSubscription.clear()
    }

    override fun onDestroy() {
        unsubscribeOnAll()
        super.onDestroy()
    }
}