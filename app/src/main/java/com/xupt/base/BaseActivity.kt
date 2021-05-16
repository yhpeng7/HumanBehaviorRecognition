package com.xupt.base

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.xupt.utils.KeyBoardUtil
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity : AppCompatActivity() {

    /**
     * 检查登录
     */
    protected var isLogin : Boolean = false

    /**
     * 布局文件id
     */
    protected abstract fun attachLayoutRes() : Int

    /**
     * 是否使用EventBus
     */
    open fun useEventBus() : Boolean = false

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 初始化View
     */
    abstract fun initView()

    /**
     * 开始请求
     */
    abstract fun start()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(attachLayoutRes())
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
        initData()
        initView()
        start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }

    /**
     * 点击空白处 隐藏键盘
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_UP) {
            val v = currentFocus
            if (KeyBoardUtil.isHideKeyboard(v,ev)) {
                KeyBoardUtil.hideKeyBoard(this,v)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}