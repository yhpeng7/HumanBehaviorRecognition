package com.xupt.base

import android.os.Bundle
import com.cxz.swipelibrary.SwipeBackActivityBase
import com.cxz.swipelibrary.SwipeBackActivityHelper
import com.cxz.swipelibrary.SwipeBackLayout
import com.cxz.swipelibrary.Utils

abstract class BaseMvpSwipeBackActivity<V : IView, P : IPresenter<V>> : BaseMvpActivity<V, P>(), SwipeBackActivityBase {

    private lateinit var mHelper: SwipeBackActivityHelper

    /**
     * SwipeBack Enable
     */
    open fun swipeBackEnable() : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHelper = SwipeBackActivityHelper(this)
        mHelper.onActivityCreate()
        initSwipeBack()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mHelper.onPostCreate()
    }

    private fun initSwipeBack() {
        setSwipeBackEnable(swipeBackEnable())
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT)
    }

    override fun getSwipeBackLayout(): SwipeBackLayout {
        return mHelper.swipeBackLayout
    }

    override fun scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this)
        swipeBackLayout.scrollToFinishActivity()
    }

    override fun setSwipeBackEnable(enable: Boolean) {
        swipeBackLayout.setEnableGesture(enable)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}