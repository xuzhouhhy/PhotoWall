package com.xuzhouhhy.myapplication

import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_photo_wall.*

/**
 * created by hanhongyun on 2018/10/25 15:01
 *
 */

class PhotoWallFragment : Fragment() {

    companion object {
        val TAG = PhotoWallFragment::class.java.simpleName
    }

    var imageList = mutableListOf<Image>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_wall, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclePhotoWall.adapter = object : PhotoWallAdapter() {
            override fun getImageList(): MutableList<Image> {
                return imageList
            }

            override fun getFragment(): Fragment {
                return this@PhotoWallFragment
            }
        }

        activity?.let { it ->
            val displayMetrics = it.resources.displayMetrics
            val width = displayMetrics.widthPixels - displayMetrics.density * 10 * 2
            val ivWidth = displayMetrics.density * 300
            val lp = WindowManager.LayoutParams(
//                    width.toInt(),
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    else
                        WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED or
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.RGBA_8888)
                    .apply {
                        gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                    }
            lp.windowAnimations = R.style.AlertImageAnimation
            val iv = ImageView(it).apply {
                this.scaleType = ImageView.ScaleType.CENTER_CROP
                val slideLength = ivWidth.toInt()
//                this.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT)
                this.layoutParams = ViewGroup.LayoutParams(slideLength, slideLength)
                Glide.with(it)
                        .load(R.drawable.xianruo)
                        .apply(RequestOptions().placeholder(android.R.color.darker_gray))
                        .into(this)
            }
//            tv.text = "testtesttesttesttesttesttesttesttesttesttest"
            recyclePhotoWall.postDelayed({
                it.window?.addContentView(iv, lp)
            }, 1000)
        }
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
                        imageList = it
                        recyclePhotoWall.layoutManager = GridLayoutManager(contextWrapper, 4)
                        recyclePhotoWall.adapter.notifyDataSetChanged()
                        Log.i(TAG, "subscribe success:${Thread.currentThread().name}")
                    }, {
                        Log.i(TAG, "subscribe fail:${Thread.currentThread().name}")
                    })
        }
    }
}

abstract class PhotoWallAdapter : RecyclerView.Adapter<PhotoWallAdapter.PhotoViewHolder>() {

    abstract fun getImageList(): MutableList<Image>
    abstract fun getFragment(): Fragment

    override fun getItemCount(): Int {
        return getImageList().size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoWallAdapter.PhotoViewHolder {
        return PhotoViewHolder(LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_photo_wall, parent, false))
    }

    override fun onBindViewHolder(holder: PhotoWallAdapter.PhotoViewHolder, position: Int) {
        holder.ivPhoto?.let {
            val image = getImageList()[position]
            val fragment = getFragment()
            Glide.with(fragment)
                    .load(image.path)
                    .into(it)
        }
    }

    class PhotoViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        var ivPhoto: ImageView? = null

        init {
            ivPhoto = itemView.findViewById(R.id.ivPhoto)
        }
    }
}