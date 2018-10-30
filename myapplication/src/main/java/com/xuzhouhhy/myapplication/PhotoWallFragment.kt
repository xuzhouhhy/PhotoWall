package com.xuzhouhhy.myapplication

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * created by hanhongyun on 2018/10/25 15:01
 *
 */

class PhotoWallFragment : Fragment() {

    companion object {
        val TAG = PhotoWallFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_photo_wall, container, false)
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val images = activity?.let { contextWrapper ->
            val subscribe1 = Single.just(contextWrapper)
                    .map<MutableList<Image>> {
                        // 110张，46ms，on Smartisan OC105
                        val images = ImageDataSource(it).loadLocalImages()
                        images
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({

                        Log.i(TAG, "subscribe success:${Thread.currentThread().name}")
                    }, {
                        Log.i(TAG, "subscribe fail:${Thread.currentThread().name}")
                    })

        }
    }
}