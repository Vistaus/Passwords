package com.cray.software.passwords.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.view.animation.Transformation

import com.cray.software.passwords.R
import com.cray.software.passwords.interfaces.Module

/**
 * Copyright 2016 Nazar Suhovich
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

object ViewUtils {

    fun getFabState(context: Context, @ColorRes colorNormal: Int, @ColorRes colorPressed: Int): ColorStateList {
        val states = arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf(android.R.attr.state_focused), intArrayOf())
        val colorP = getColor(context, colorPressed)
        val colorN = getColor(context, colorNormal)
        val colors = intArrayOf(colorP, colorN, colorN)
        return ColorStateList(states, colors)
    }

    fun getFabState(@ColorInt colorNormal: Int, @ColorInt colorPressed: Int): ColorStateList {
        val states = arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf(android.R.attr.state_focused), intArrayOf())
        val colors = intArrayOf(colorPressed, colorNormal, colorNormal)
        return ColorStateList(states, colors)
    }

    fun getDrawable(context: Context, @DrawableRes resource: Int): Drawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            context.resources.getDrawable(resource, null)
        } else {
            context.resources.getDrawable(resource)
        }
    }

    @ColorInt
    fun getColor(context: Context, @ColorRes resource: Int): Int {
        try {
            return if (Module.isMarshmallow) {
                context.resources.getColor(resource, null)
            } else {
                context.resources.getColor(resource)
            }
        } catch (e: Resources.NotFoundException) {
            return 0
        }

    }

    fun fadeInAnimation(view: View) {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.startOffset = 400
        fadeIn.duration = 400
        view.animation = fadeIn
        view.visibility = View.VISIBLE
    }

    fun fadeOutAnimation(view: View) {
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator() //and this
        fadeOut.duration = 400
        view.animation = fadeOut
        view.visibility = View.GONE
    }

    fun show(view: View) {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.startOffset = 400
        fadeIn.duration = 400
        view.animation = fadeIn
        view.visibility = View.VISIBLE
    }

    fun hide(view: View) {
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator() //and this
        fadeOut.duration = 400
        view.animation = fadeOut
        view.visibility = View.INVISIBLE
    }

    fun showOver(view: View) {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = OvershootInterpolator()
        fadeIn.duration = 300
        view.animation = fadeIn
        view.visibility = View.VISIBLE
    }

    fun hideOver(view: View) {
        val fadeIn = AlphaAnimation(1f, 0f)
        fadeIn.interpolator = OvershootInterpolator()
        fadeIn.duration = 300
        view.animation = fadeIn
        view.visibility = View.GONE
    }

    fun show(v: View, callback: AnimationCallback?) {
        if (v.visibility == View.VISIBLE) return
        val scaleUp = AnimationUtils.loadAnimation(v.context, R.anim.scale_zoom)
        scaleUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                callback?.onAnimationFinish(1)
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        v.startAnimation(scaleUp)
        v.visibility = View.VISIBLE
    }

    fun hide(v: View, callback: AnimationCallback?) {
        if (v.visibility == View.GONE) return
        val scaleDown = AnimationUtils.loadAnimation(v.context, R.anim.scale_zoom_out)
        scaleDown.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                callback?.onAnimationFinish(0)
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        v.startAnimation(scaleDown)
        v.visibility = View.GONE
    }

    fun showReveal(v: View) {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = AccelerateDecelerateInterpolator()
        fadeIn.duration = 300
        v.animation = fadeIn
        v.visibility = View.VISIBLE
    }

    fun hideReveal(v: View) {
        val fadeIn = AlphaAnimation(1f, 0f)
        fadeIn.interpolator = AccelerateDecelerateInterpolator()
        fadeIn.duration = 300
        v.animation = fadeIn
        v.visibility = View.GONE
    }

    fun expand(v: View) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val targetHeight = v.measuredHeight
        v.layoutParams.height = 0
        v.visibility = View.VISIBLE
        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                v.layoutParams.height = if (interpolatedTime == 1f)
                    ViewGroup.LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        // 1dp/ms
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        val initialHeight = v.measuredHeight
        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        // 1dp/ms
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    interface AnimationCallback {
        fun onAnimationFinish(code: Int)
    }
}