package com.key.magicbook.activity.login

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Intent
import android.view.View
import android.view.animation.LinearInterpolator
import com.allen.library.utils.ToastUtils
import com.key.keylibrary.utils.KeyboardHeightObserver
import com.key.keylibrary.utils.KeyboardHeightProvider
import com.key.keylibrary.utils.UiUtils
import com.key.keylibrary.widget.CustomEditTextView.MineFocusChangeListener
import com.key.magicbook.R
import com.key.magicbook.activity.index.IndexActivity
import com.key.magicbook.base.MineBaseActivity
import com.key.magicbook.bean.UserInfo
import com.key.magicbook.util.DialogUtil
import kotlinx.android.synthetic.main.activity_login.*
import org.apache.commons.codec.digest.Md5Crypt
import org.litepal.LitePal

/**
 * created by key  on 2020/3/13
 */
class LoginActivity : MineBaseActivity<LoginPresenter>(), KeyboardHeightObserver {
    private var keyboardHeightProvider: KeyboardHeightProvider? = null
    private var topTransitionY = 0f
    private var cardLoginY = 0f
    private var dialogShow = false
    private var changeBaseWidth = 0;
    private var lastAccount = ""
    private var lastName = ""
    override fun createPresenter(): LoginPresenter? {
        return LoginPresenter()
    }

    override fun initView() {
        keyboardHeightProvider = KeyboardHeightProvider(this)
        card_login.post {
            keyboardHeightProvider!!.start()
        }
        account.setInputTypeNum()
        password.setInputTypePass()
        changeBaseWidth = (UiUtils.getScreenWidth(this@LoginActivity) - UiUtils.dip2px(54f)) / 4
        login.layoutParams.width = changeBaseWidth * 3
        register.layoutParams.width = changeBaseWidth
        register_account.setInputTypeNum()
        register_password.setInputTypePass()
        register_confirm_password.setInputTypePass()
        register_name.setInputTypeChineseAndEnglish(true)
        initRegister()
        register.setOnClickListener {
            hintKeyBoard()
            if (register.layoutParams.width < 2 * changeBaseWidth) {
                exchange(false)
            } else {
                register()
            }
        }


        login.setOnClickListener {
            if (login.layoutParams.width < 2 * changeBaseWidth) {
                exchange(true)
            } else {
                var check: Boolean = false
                if (!check && account.editTextString.isEmpty()) {
                    ToastUtils.showToast("请输入账号")
                    check = true
                }

                if (!check && password.editTextString.isEmpty()) {
                    ToastUtils.showToast("请输入密码")
                    check = true
                }

                if (!check) {
                    val findAll = LitePal.findAll(UserInfo::class.java)
                    var right = false
                    for (value in findAll) {
                        if (Md5Crypt.apr1Crypt(
                                password.editTextString,
                                "key"
                            ) == value.password
                        ) {
                            val contentValues = ContentValues()
                            contentValues.put("isLogin", "true")
                            LitePal.updateAll(
                                UserInfo::class.java,
                                contentValues,
                                "account = ?",
                                account.editTextString
                            )
                            startActivity(Intent(this@LoginActivity, IndexActivity::class.java))
                            right = true
                            break
                        }
                    }

                    if (!right) {
                        ToastUtils.showToast("账号密码错误")
                    }
                }
            }
        }

        changeBg()
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        if (!dialogShow) {
            if (height > 0) {
                if (cardLoginY == 0f) {
                    cardLoginY = card_login.y
                }
                var fl = UiUtils.getScreenHeight(this) - (
                        cardLoginY
                                + UiUtils.measureView(card_login)[1] + height + UiUtils.dip2px(80f))
                val mAnimatorTranslateY: ObjectAnimator = ObjectAnimator.ofFloat(
                    card_login,
                    "translationY", topTransitionY, fl
                )
                mAnimatorTranslateY.duration = 200
                mAnimatorTranslateY.interpolator = LinearInterpolator()
                mAnimatorTranslateY.start()
                mAnimatorTranslateY.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        topTransitionY = fl
                    }

                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}


                })

            } else {
                val mAnimatorTranslateY: ObjectAnimator = ObjectAnimator.ofFloat(
                    card_login,
                    "translationY", topTransitionY, 0f
                )
                mAnimatorTranslateY.duration = 200
                mAnimatorTranslateY.interpolator = LinearInterpolator()
                mAnimatorTranslateY.start()
                mAnimatorTranslateY.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        topTransitionY = 0f
                    }

                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
            }

        }


    }

    override fun onPause() {
        super.onPause()
        hintKeyBoard()
        keyboardHeightProvider!!.setKeyboardHeightObserver(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        keyboardHeightProvider!!.close()
    }

    override fun onResume() {
        super.onResume()
        keyboardHeightProvider!!.setKeyboardHeightObserver(this@LoginActivity)

    }


    private fun changeWidth(target: View, targetWidth: Int) {
        val viewWrapper = ViewWrapper(target)
        ObjectAnimator.ofInt(viewWrapper, "width", targetWidth).setDuration(300).start()
    }

    private class ViewWrapper(private val mTarget: View) {
        var width: Int
            get() = mTarget.layoutParams.width
            set(width) {
                mTarget.layoutParams.width = width
                mTarget.requestLayout()
            }
    }

    private fun initRegister() {
        register_account.setMineFocusChangeListener {
            if(!it){
                if (lastAccount != register_account.editTextString) {
                    val empty: Boolean = register_account.editTextString.isEmpty()
                    if (!empty) {
                        if (isRepetitionAccount(register_account.editTextString)) {
                            hintKeyBoard()
                            DialogUtil.getWarm(
                                this@LoginActivity, "此账号已经注册过了"
                            ) { }.show()
                        }
                    }
                    lastAccount = register_account.editTextString
                }
            }
        }


        register_name.setMineFocusChangeListener {
            if(!it){
                if (lastName != register_name.editTextString) {
                    val empty: Boolean = register_name.editTextString.isEmpty()
                    if (!empty) {
                        if (isRepetitionAccount(register_name.editTextString)) {
                            hintKeyBoard()
                            DialogUtil.getWarm(
                                this@LoginActivity, "此昵称已被其他人使用"
                            ) { }.show()
                        }
                    }
                    lastName = register_name.editTextString
                }
            }

        }
    }


    private fun isRepetitionAccount(account: String): Boolean {
        val all = LitePal.findAll(UserInfo::class.java)
        for (userInfo in all) {
            val equals = userInfo.account == account
            if (equals) {
                return true
            }
        }
        return false
    }


    private fun isRepetitionName(name: String): Boolean {
        val all = LitePal.findAll(UserInfo::class.java)
        for (userInfo in all) {
            val equals = userInfo.userName == name
            if (equals) {
                return true
            }
        }
        return false
    }


    private fun register() {
        var check = false
        if (register_account.editTextString.isEmpty()) {
            ToastUtils.showToast("请输入正确的账号")
            check = true
        } else {
            if (isRepetitionAccount(register_account.editTextString)) {
                ToastUtils.showToast("此账号已经注册过了")
                check = true
            }
        }
        if (register_password.editTextString.isEmpty() && !check) {
            ToastUtils.showToast("请输入密码")
            check = true
        }

        if (register_confirm_password.editTextString.isEmpty() && !check) {
            ToastUtils.showToast("请输入确认密码")
            check = true
        }
        if (register_confirm_password.editTextString != register_password.getEditTextString() && !check) {
            ToastUtils.showToast("两次密码输入不一致")
            check = true
        }

        if (register_name.editTextString.isEmpty() && !check) {
            ToastUtils.showToast("请输入昵称")
            check = true
        } else {
            if (isRepetitionName(register_name.editTextString)) {
                ToastUtils.showToast("此昵称已被其他人使用")
                check = true
            }
        }
        if (!check) {
            val userInfo = UserInfo()
            userInfo.account = register_account.editTextString
            userInfo.password = Md5Crypt.apr1Crypt(register_password.editTextString, "key")
            userInfo.userName = register_name.editTextString
            userInfo.realPassword = register_password.editTextString
            userInfo.save()
            ToastUtils.showToast("注册成功")

            exchange(true)
            account.editTextString = userInfo.account
            password.editTextString = userInfo.realPassword
        }
    }


    private fun exchange(isLogin: Boolean) {
        var transitionX = 0f;
        transitionX = if (isLogin) {
            UiUtils.getScreenWidth(this).toFloat()
        } else {
            -UiUtils.getScreenWidth(this).toFloat()
        }
        val mAnimatorTranslateX: ObjectAnimator = ObjectAnimator.ofFloat(
            card_login,
            "translationX", 0f, transitionX
        )
        mAnimatorTranslateX.duration = 300
        mAnimatorTranslateX.interpolator = LinearInterpolator()
        if (isLogin) {
            changeWidth(register, changeBaseWidth)
            changeWidth(login, 3 * changeBaseWidth)
        } else {
            changeWidth(register, 3 * changeBaseWidth)
            changeWidth(login, changeBaseWidth)
        }

        mAnimatorTranslateX.start()
        mAnimatorTranslateX.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                if (isLogin) {
                    login_root.visibility = View.VISIBLE
                    register_root.visibility = View.GONE
                } else {
                    login_root.visibility = View.GONE
                    register_root.visibility = View.VISIBLE
                }

                cardLoginY = card_login.y
                val mAnimatorTranslateX: ObjectAnimator = ObjectAnimator.ofFloat(
                    card_login,
                    "translationX", transitionX, 0f
                )
                mAnimatorTranslateX.duration = 0
                mAnimatorTranslateX.interpolator = LinearInterpolator()
                mAnimatorTranslateX.start()
                val mAnimatorTranslateAlpha: ObjectAnimator = ObjectAnimator.ofFloat(
                    card_login,
                    "alpha", 0f, 1f
                )
                mAnimatorTranslateAlpha.duration = 500
                mAnimatorTranslateAlpha.start()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun changeBg() {
        val animator1 = ObjectAnimator.ofFloat(bg_1, "alpha", 1.0f, 0f);
        val animator2 = ObjectAnimator.ofFloat(bg_2, "alpha", 0f, 1.0f);
        val animatorScale1 = ObjectAnimator.ofFloat(bg_1, "scaleX", 1.0f, 1.3f);
        val animatorScale2 = ObjectAnimator.ofFloat(bg_1, "scaleY", 1.0f, 1.3f);
        val animatorSet1 = AnimatorSet()
        animatorSet1.duration = 5000;
        animatorSet1.play(animator1).with(animator2).with(animatorScale1).with(animatorScale2)
        animatorSet1.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                // 放大的View复位
                bg_1.scaleX = 1.0f;
                bg_1.scaleY = 1.0f;
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        val animator3 = ObjectAnimator.ofFloat(bg_2, "alpha", 1.0f, 0f);
        val animator4 = ObjectAnimator.ofFloat(bg_1, "alpha", 0f, 1.0f);
        val animatorScale3 = ObjectAnimator.ofFloat(bg_2, "scaleX", 1.0f, 1.3f);
        val animatorScale4 = ObjectAnimator.ofFloat(bg_2, "scaleY", 1.0f, 1.3f);
        val animatorSet2 = AnimatorSet();
        animatorSet2.duration = 5000;
        animatorSet2.play(animator3).with(animator4).with(animatorScale3).with(animatorScale4);

        animator2.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                //            // 放大的View复位
                bg_2.scaleX = 1.0f;
                bg_2.scaleY = 1.0f;
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(animatorSet1, animatorSet2)
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                animation!!.start();
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        animatorSet.start()
    }


}