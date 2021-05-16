package com.xupt.base

/**
 * in --- 消费类型为T的值
 */
interface IPresenter <V>{

    /**
     * 绑定View
     */
    fun attachView(mView : V)

    /**
     * 解绑View
     */
    fun detachView()
}