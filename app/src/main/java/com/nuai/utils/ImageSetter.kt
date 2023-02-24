package com.nuai.utils

import android.text.TextUtils
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.io.File

object ImageSetter {

    fun loadImage(
        mUrl: String?,
        placeHolder: Int,
        mImageView: ImageView
    ) {
        if (!TextUtils.isEmpty(mUrl)) {
            Picasso.get()
                .load(mUrl)
                .placeholder(placeHolder)
                .into(mImageView)
        } else {
            mImageView.setImageResource(placeHolder)
        }
    }

    fun loadImage(
        file: File?,
        placeHolder: Int,
        mImageView: ImageView
    ): Boolean {
        return if (file != null) {
            Picasso.get()
                .load(file)
                .placeholder(placeHolder)
                .into(mImageView)
            true
        } else {
            mImageView.setImageResource(placeHolder)
            false
        }
    }

    fun loadImage(image: Int, placeHolder: Int, mImageView: ImageView) {
        if (image > 0) {
            Picasso.get()
                .load(image)
                .placeholder(placeHolder)
                .into(mImageView)
        } else {
            mImageView.setImageResource(placeHolder)
        }
    }

    fun loadImageResize(
        mUrl: String?,
        placeHolder: Int,
        mImageView: ImageView,
        width: Int,
        height: Int
    ): Boolean {
        return if (!TextUtils.isEmpty(mUrl)) {
            Picasso.get()
                .load(mUrl)
                .placeholder(placeHolder)
                .resize(width, height)
                .into(mImageView)
//            mImageView.load(mUrl) {
//                transformations(CircleCropTransformation())
//                size(width, height)
//                placeholder(placeHolder)
//            }
            true
        } else {
            mImageView.setImageResource(placeHolder)
            false
        }
    }


//    fun loadRoundedImage(
//        mUrl: String?,
//        placeHolder: Int,
//        mImageView: ImageView
//    ) {
//        if (!TextUtils.isEmpty(mUrl)) {
//            Picasso.get()
//                .load(mUrl)
//                .transform(CircleTransform())
//                .placeholder(placeHolder)
//                .error(placeHolder)
//                .into(mImageView)
//        } else {
//            mImageView.setImageResource(placeHolder)
//        }
//    }

//    fun loadRoundedImageResize(
//        mUrl: String?,
//        placeHolder: Int,
//        width: Int,
//        height: Int,
//        mImageView: ImageView
//    ) {
//        if (!TextUtils.isEmpty(mUrl)) {
//            Picasso.get()
//                .load(mUrl)
//                .transform(CircleTransform())
//                .resize(width, height)
//                .centerCrop()
//                .placeholder(placeHolder)
//                .error(placeHolder)
//                .into(mImageView)
//        } else {
//            mImageView.setImageResource(placeHolder)
//        }
//    }

//    fun loadRoundedImage(mUrl: File?, placeHolder: Int, mImageView: ImageView) {
//        if (mUrl != null) {
//            Picasso.get().load(mUrl)
//                .placeholder(placeHolder)
//                .transform(CircleTransform())
//                .into(mImageView)
//        } else {
//            mImageView.setImageResource(placeHolder)
//        }
//    }

    fun loadImageFile(
        mUrl: File?,
        placeHolder: Int,
        mImageView: ImageView
    ): Boolean {
        return if (mUrl != null) {
//            mImageView.load(mUrl) {
//                placeholder(placeHolder)
//            }
            Picasso.get()
                .load(mUrl)
                .placeholder(placeHolder)
                .into(mImageView)
            true
        } else {
            mImageView.setImageResource(placeHolder)
            false
        }
    }

    fun loadImageFileResize(
        mUrl: File?,
        placeHolder: Int,
        mImageView: ImageView,
        width: Int,
        height: Int
    ): Boolean {
        return if (mUrl != null) {
            Picasso.get()
                .load(mUrl)
                .placeholder(placeHolder)
                .resize(width, height)
                .into(mImageView)
            /* Picasso.get()
                 .load(mUrl)
                 .placeholder(placeHolder)
                 .into(mImageView)*/
//            mImageView.load(mUrl) {
//                transformations(CircleCropTransformation())
//                size(width, height)
//                placeholder(placeHolder)
//            }
            true
        } else {
            mImageView.setImageResource(placeHolder)
            false
        }
    }

    fun loadImageFileFitXY(
        mUrl: File?,
        placeHolder: Int,
        mImageView: ImageView
    ): Boolean {
        return if (mUrl != null) {
            Picasso.get()
                .load(mUrl)
                .centerInside()
                .placeholder(placeHolder)
                .into(mImageView)
            true
        } else {
            mImageView.setImageResource(placeHolder)
            false
        }
    }

//    fun loadRoundedCornerImage(
//        mUrl: String?,
//        placeHolder: Int,
//        radius: Int,
//        margin: Int,
//        mImageView: ImageView
//
//    ) {
//        if (!TextUtils.isEmpty(mUrl)) {
//            val transformation: Transformation = RoundedCornersTransformation(radius, margin, RoundedCornersTransformation.CornerType.ALL)
//            Picasso.get()
//                .load(mUrl)
//                .transform(transformation)
//                .placeholder(placeHolder)
//                .error(placeHolder)
//                .into(mImageView)
//        } else {
//            mImageView.setImageResource(placeHolder)
//        }
//    }
}
