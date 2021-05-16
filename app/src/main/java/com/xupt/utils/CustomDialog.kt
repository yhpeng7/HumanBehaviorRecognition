package com.xupt.utils

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import com.xupt.R
import kotlinx.android.synthetic.main.loading_dialog.*

class CustomDialog protected constructor(context: Context, theme: Int, string: String) :
        Dialog(context, theme) {

    constructor(context: Context) : this(context, R.style.loading_dialog, "玩命加载中...")

    constructor(context: Context, string: String) : this(context, R.style.loading_dialog, string)

    init {
        setCanceledOnTouchOutside(false)//点击其他区域时   true  关闭弹窗  false  不关闭弹窗
        setOnCancelListener { dismiss() }
        setContentView(R.layout.loading_dialog)

        window!!.attributes.gravity = Gravity.CENTER//居中显示
        window!!.attributes.dimAmount = 0.7f//背景透明度  取值范围 0 ~ 1
    }

    override fun dismiss() {
        super.dismiss()
    }
}