package com.demo.mvp.findlocation

import com.demo.mvp.BasePresenter
import com.demo.mvp.BaseViewer


/**
 * MVP contract for [MainActivity]
 */
interface MainContract {
    interface Viewer : BaseViewer<MainContract.Presenter>
    interface Presenter : BasePresenter
}