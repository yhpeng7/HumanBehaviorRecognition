package com.xupt.base

@Suppress("UNCHECKED_CAST")
abstract class BaseMvpActivity<V, P : IPresenter<V>> : BaseActivity(), IView {

    protected var mPresenter : P? = null

    protected abstract fun createPresenter() : P

    override fun initView() {
        mPresenter = createPresenter()
        mPresenter?.attachView(this as V)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
        this.mPresenter = null
    }
}