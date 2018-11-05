package com.xuzhouhhy.myapplication

import android.content.ContextWrapper
import android.provider.MediaStore
import android.util.Log

/**
 * created by hanhongyun on 2018/10/29 15:39
 *
 */
data class Image constructor(val name: String, val path: String) {

    constructor(name: String, path: String, type: String, sizeInbyte: Long, width: Int,
                height: Int, dateAdded: Long) : this(name, path) {
        this.type = type
        this.sizeInbyte = sizeInbyte
        this.width = width
        this.height = height
        this.dateAdded = dateAdded
    }

    /**
     * 图片的类型     image/jpeg
     */
    var type: String? = null

    var sizeInbyte: Long? = null

    var width: Int? = null

    var height: Int? = null

    /**
     * 图片被添加的时间，long型  1450518608
     */
    var dateAdded: Long? = null

}

class ImageDataSource constructor(val contextWrapper: ContextWrapper) {

    companion object {
        val TAG = ImageDataSource::class.java.simpleName
    }


    private val IMAGE_PROJECTION by lazy {
        arrayOf(//查询图片需要的数据列
                MediaStore.Images.Media.DISPLAY_NAME, //图片的显示名称  aaa.jpg
                MediaStore.Images.Media.DATA, //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
                MediaStore.Images.Media.SIZE, //图片的大小，long型  132492
                MediaStore.Images.Media.WIDTH, //图片的宽度，int型  1920
                MediaStore.Images.Media.HEIGHT, //图片的高度，int型  1080
                MediaStore.Images.Media.MIME_TYPE, //图片的类型     image/jpeg
                MediaStore.Images.Media.DATE_ADDED)    //图片被添加的时间，long型  1450518608
    }


    /**
     * run only on io thread
     */
    fun loadLocalImages(): MutableList<Image> {
        //DESC means you see latest image first ,otherwise ASC
        return contextWrapper.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_PROJECTION,
                null,
                null,
                IMAGE_PROJECTION[6] + " DESC")?.let { cursor ->
            val images = mutableListOf<Image>()
            cursor.moveToFirst()
            Log.i(TAG, cursor.count.toString())
            while (cursor.moveToNext()) {
                val imageName = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[0]))
                val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[1]))
                val imageSize = cursor.getLong(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[2]))
                val imageWidth = cursor.getInt(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[3]))
                val imageHeight = cursor.getInt(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[4]))
                val imageMimeType = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[5]))
                val imageAddTime = cursor.getLong(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[6]))
                val image = Image(imageName, imagePath, imageMimeType, imageSize, imageWidth, imageHeight, imageAddTime)
                Log.i(TAG, image.toString())
                images.add(image)
            }
            cursor.close()
            images
        } ?: mutableListOf()
    }

}