package com.xupt.base

import org.greenrobot.eventbus.EventBus

abstract class BasePresenter<V> : IPresenter<V> {

    protected var mView: V? = null

    private val isViewAttach: Boolean
        get() = mView != null


    open fun useEventBus() : Boolean = false

    override fun attachView(mView: V) {
        this.mView = mView
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    override fun detachView() {
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
            mView = null
        }
    }
}