package com.anwesh.uiprojects.linecirclerotatorview

/**
 * Created by anweshmishra on 31/05/18.
 */

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.MotionEvent
import android.graphics.*

val STATE_NODES : Int = 3

class LineCircleRotatorView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.draw(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class StateNode (var i : Int, var dir : Float = 0f, var scale : Float = 0f, var prevScale : Float = 0f) {

        var next : StateNode? = null

        private var prev : StateNode? = null

        init {
            this.addNeighbor()
        }

        fun addNeighbor() {
            if (i < STATE_NODES - 1) {
                next = StateNode(i + 1)
                next?.prev = this
            }
        }

        fun update(stopcb : (Float) -> Unit) {
            scale += 0.1f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(prevScale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }

        fun getNext(dir : Int, cb : () -> Unit) : StateNode {
            var curr : StateNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class Animator (var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class LinkedCircleRotator(var i : Int) {

        var dir : Int = 1

        val root : StateNode = StateNode(0)

        var curr : StateNode = root


        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            var h1 : Float = (h/2)* root.scale
            var h2 : Float = (h/2) * (root?.next?.next?.scale?:0f)
            var deg : Float = 180f * (root?.next?.scale?:0f)
            paint.strokeWidth = Math.min(w, h) / 50
            paint.strokeCap = Paint.Cap.ROUND
            canvas.save()
            canvas.translate(w/2, h1)
            canvas.rotate(deg)
            canvas.drawLine(-w/10, (h - 2 * h1) + h2 + w/10, w/10, (h - 2 * h1) + h2 + w/10, paint)
            canvas.drawCircle(0f, -h2 , w/10, paint)
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    this.dir *= -1
                }
                stopcb(it)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }

    data class Renderer(var view : View, var animated: Boolean = false) {

        private val animator : Animator = Animator(view)

        private val lcr : LinkedCircleRotator = LinkedCircleRotator(0)

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            paint.color = Color.parseColor("#2ecc71")
            lcr.draw(canvas, paint)
            animator.animate {
                lcr.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            lcr.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) {
            val view : LineCircleRotatorView = LineCircleRotatorView(activity)
            activity.setContentView(view)
        }
    }
}