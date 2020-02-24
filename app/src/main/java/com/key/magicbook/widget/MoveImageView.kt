package com.key.magicbook.widget
import android.content.Context
import android.graphics.PointF
import android.widget.ImageView
class MoveImageView(context: Context) : ImageView(context) {

    fun setMPointF(pointF: PointF) {
        x = pointF.x
        y = pointF.y
    }
}