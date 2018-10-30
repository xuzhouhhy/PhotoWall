package com.xuzhouhhy.myapplication

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
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