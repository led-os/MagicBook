package com.key.magicbook.util

import android.animation.TypeEvaluator
import android.graphics.PointF

/**
 * created by key  on 2019/11/8
 */
class BezierTypeEvaluate : TypeEvaluator<PointF> {
    private var control: PointF? = null
    private var mPointList: ArrayList<PointF>? = null
    private var mPointNum: Int = 51
    constructor(control: PointF) {
        this.control = control
    }

    override fun evaluate(fraction: Float, startPointF: PointF?, endPointF: PointF?): PointF {
        return getBezierPoint(startPointF!!, endPointF!!, control!!, fraction)
    }


    private fun getBezierPoint(start: PointF, end: PointF, control: PointF, t: Float): PointF {
        val bezierPoint = PointF()
        bezierPoint.x =
            (1 - t) * (1 - t) * start.x + 2f * t * (1 - t) * control.x + t * t * end.x
        bezierPoint.y =
            (1 - t) * (1 - t) * start.y + 2f * t * (1 - t) * control.y + t * t * end.y
        return bezierPoint
    }

    //    n阶贝塞尔曲线
    private fun getBezierPointX(n: Int, position: Int, t: Float): Float {
        return if (n == 1) {
            (1 - t) * mPointList!![position].x + t * mPointList!![position + 1].x
        } else (1 - t) * getBezierPointX(n - 1, position, t) + t * getBezierPointX(
            n - 1,
            position + 1,
            t
        )
    }


    private fun getBezierPointY(n: Int, position: Int, t: Float): Float {
        return if (n == 1) {
            (1 - t) * mPointList!![position].y + t * mPointList!![position + 1].y
        } else (1 - t) * getBezierPointY(n - 1, position, t) + t * getBezierPointX(
            n - 1,
            position + 1,
            t
        )
    }

    private fun buildBezierPoints(): ArrayList<PointF> {
        val points = arrayListOf<PointF>()
        val order = mPointList!!.size - 1
        val delta = 1.0f / mPointNum
        var t = 0f
        while (t <= 1) {
            // Bezier点集
            points.add(PointF(getBezierPointX(order, 0, t), getBezierPointY(order, 0, t)))
            t += delta
        }
        return points
    }
}