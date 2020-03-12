package com.key.magicbook.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.PointF
import android.graphics.Rect
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.key.keylibrary.utils.UiUtils
import com.key.magicbook.R
import com.key.magicbook.util.BezierTypeEvaluate
import com.key.magicbook.util.UiUtil
import kotlinx.android.synthetic.main.view_jump_on_text.view.*
import java.util.*

/**
 * created by key  on 2019/11/5
 */
class JumpOnTextView : FrameLayout {
    private var mRoot: FrameLayout? = null
    private var mMoveImageView :MoveImageView ?= null
    private var zeroPointF: PointF? = PointF(0f, 0f)
    private var mThread :Thread ?= null
    private var isFirst :Boolean = true
    private var isFirstSetPadding :Boolean = true
    private var stateHeight : Int = 0
    private var mSize :Int = 0
    private var mPadding :Int = 0
    private var mAnimationTime : Int =  800
    var onAnimationEndListener :OnAnimationEndListener ?= null
    private val textViews: List<TextView>
        get() {
            val childCount = jump_text_root!!.childCount
            val textViews = ArrayList<TextView>()
            for (i in 0 until childCount) {
                val childAt = jump_text_root!!.getChildAt(i)
                if (childAt is TextView) {
                    textViews.add(childAt)
                }
            }
            return textViews
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        deal(attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        deal(attrs)
        init()
    }

    private fun deal(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.JumpOnTextView)
        mPadding = a.getDimensionPixelSize(R.styleable.JumpOnTextView_jump_padding, 13)
    }

    private fun init() {
        val inflate = View.inflate(context, R.layout.view_jump_on_text, this)
        mRoot = inflate.findViewById(R.id.jump_view_root)
        mMoveImageView = MoveImageView(context)
        mMoveImageView!!.setBackgroundResource(R.drawable.circle_blue_gray)
        val layoutParams = LinearLayout.LayoutParams(50, 50)
        mMoveImageView!!.layoutParams = layoutParams
        val name = "MagicBook"
        add(name)
        mRoot!!.addView(mMoveImageView)
        mMoveImageView!!.visibility = View.INVISIBLE
    }


    private fun add(text: String) {
        val texts = ArrayList<String>()
        for (i in text.indices) {
            val substring = text.substring(i, i + 1)
            texts.add(substring)
        }
        jump_text_root.removeAllViews()
        mSize = ((UiUtils.getScreenWidth(context) - mPadding * 15) / texts.size)/3  * 4
        for (i in texts.indices) {
            val textView = TextView(context)
            textView.textSize = mSize.toFloat()
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textView.text = texts[i]
            textView.includeFontPadding = false
            textView.setTextColor(context!!.resources.getColor(R.color.blue))
            jump_text_root!!.addView(textView, layoutParams)
        }


        val i = UiUtils.measureView(jump_text_root)[1]
        val layoutParams = jump_text_root!!.layoutParams
        layoutParams.height = i*3
        jump_text_root!!.layoutParams = layoutParams
    }


    /***
     * 向下动画
     */
    fun viewDownAndUp(view: View) {
        val handler = Handler()
        Thread {
            handler.post { view.clearAnimation() }
            val textView = view as TextView
            val string = textView.text.toString()
            val time = mAnimationTime.toLong() / 2
            val rect = measureStringWidthOnScreen(TextView(context), string, mSize)
            val animationDown = TranslateAnimation(0f, 0f, 0f, (rect.height() / 3).toFloat())
            animationDown.duration = time
            handler.post { view.startAnimation(animationDown) }
            animationDown.setAnimationListener(object :Animation.AnimationListener{
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    val animationUp = TranslateAnimation(0f, 0f, (rect.height() / 3).toFloat(), 0f)
                    animationUp.duration = time
                    handler.post { view.startAnimation(animationUp) }
                }

                override fun onAnimationStart(animation: Animation?) {

                }
            })
        }.start()
    }

    private fun measureStringWidthOnScreen(content: TextView, info: String, textSize:Int): Rect {
        val rect = Rect()
        content.textSize = textSize.toFloat()
        content.paint.getTextBounds(info, 0, info.length, rect)
        return rect
    }



    fun mineLoop() {
        mThread =  Thread(Runnable {
            UiUtil.runOnUIThread(Runnable {
                mMoveImageView!!.visibility = View.VISIBLE
            })
            val calculate = calculate()
            for ((index, _) in calculate.withIndex()) {
                if (index == 0) {
                    val mControlPoint = PointF()
                    mControlPoint.x = calculate[0].x - zeroPointF!!.x
                    mControlPoint.y = (zeroPointF!!.y + calculate[0].y) / 4
                    UiUtil.runOnUIThread(Runnable {
                        startAnimation(mMoveImageView!!, zeroPointF!!, calculate[0], mControlPoint,0)
                    })
                    Thread.sleep(800)
                } else {
                    val mControlPoint = PointF()
                    mControlPoint.x =
                        (calculate[index].x - calculate[index - 1].x) / 2 + calculate[index - 1].x
                    mControlPoint.y = calculate[index].y / 2
                    UiUtil.runOnUIThread(Runnable {
                        startAnimation(
                            mMoveImageView!!,
                            calculate[index - 1],
                            calculate[index],
                            mControlPoint,
                            index
                        )
                    })
                    Thread.sleep(800)
                    if(index == calculate.size - 1){
                        UiUtil.runOnUIThread(Runnable {
                            mMoveImageView!!.visibility = View.GONE
                            val pointF = PointF()
                            pointF.x = 0f
                            pointF.y = stateHeight.toFloat()
                            mMoveImageView!!.setMPointF(pointF)
 //                         mineLoop()
                        })
                    }
                }
            }
        })
        mThread!!.start()
    }


    private fun calculate():ArrayList<PointF> {
        val pointFs = ArrayList<PointF>()
        if (isFirst) {
            val movePosition = IntArray(2)
            mMoveImageView!!.getLocationOnScreen(movePosition)
            stateHeight = movePosition[1]
            isFirst = false
        }

        var bottomMargin = 0
        for (value in textViews) {
            val position = IntArray(2)
            value.getLocationOnScreen(position)
            val globeRect = Rect()
            value.getLocalVisibleRect(globeRect)
            val rect = measureStringWidthOnScreen(TextView(context), value.text.toString(), mSize)
            val topPadding = (globeRect.height() - rect.height()) / 2
            if (bottomMargin == 0) {
                bottomMargin = topPadding
            } else if (topPadding < bottomMargin) {
                bottomMargin = topPadding
            }
        }

        for (value in textViews) {
            val position = IntArray(2)
            value.getLocationOnScreen(position)

            val globeRect = Rect()
            value.getLocalVisibleRect(globeRect)

            val rect = measureStringWidthOnScreen(TextView(context), value.text.toString(), mSize)

            val topPadding = globeRect.height() - rect.height() - bottomMargin
            if (isFirstSetPadding) {
                UiUtil.runOnUIThread(
                    Runnable { value.setPadding(0, 0, 0, 0) }
                )
            }

            val pointF = PointF()
            pointF.x = (position[0] + globeRect.width() / 2 - mMoveImageView!!.width / 2).toFloat()
            pointF.y = (position[1] - stateHeight + topPadding - mMoveImageView!!.height).toFloat()
            pointFs.add(pointF)
        }
        isFirstSetPadding = false
        return pointFs
    }



    private fun startAnimation(
        moveImageView: MoveImageView,
        mStartPoint: PointF,
        mEndPoint: PointF,
        mControlPoint: PointF,
        index : Int
    ) {
        val animator = ObjectAnimator.ofObject(
            moveImageView,
            "mPointF",
            BezierTypeEvaluate(mControlPoint),
            mStartPoint,
            mEndPoint
        )
        animator.duration = mAnimationTime.toLong()
        val listener = object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                UiUtil.runOnUIThread(Runnable {
                   viewDownAndUp(textViews[index])
                    if(index == textViews.size-1){
                       if(onAnimationEndListener != null){
                           onAnimationEndListener!!.animationEnd()
                       }
                    }
                })
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationStart(p0: Animator?) {

            }

        }
        animator.addListener(listener)
        animator.start()
    }


    interface OnAnimationEndListener{
        fun animationEnd()
    }

}
