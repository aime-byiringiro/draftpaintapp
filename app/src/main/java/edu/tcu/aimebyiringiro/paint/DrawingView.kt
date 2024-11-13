
package edu.tcu.aimebyiringiro.paint

import android.app.Dialog
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView (context: Context, attrs: AttributeSet): View(context, attrs) {
//
    /*
    1. we need Paint class to draw on canvas, think of it as a pen
    2. a) Path == Continuous line segments
     */

    private val paint = Paint()
    private val pathList = mutableListOf<CustomPath>()
    private var path = CustomPath(Color.BLACK, resources.displayMetrics.density)
    init {
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val x = event.x
        val y = event.y
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pathList.add(path)
                path.moveTo(x,y)

                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                path = CustomPath(path.color, path.width)
                return true
            }
            else -> {
                return false
            }
        }

    }


    fun setPathColor(color: Int) {
        path.color = color
    }

    // Define a method setPathColor(color: Int); it's just one line


    //Define a method setPathWidth(width: Int)



    //Define a method undo


//    1st: 5dp
//    2nd: 10dp
//    3rd: 15dp



    override fun onDraw(canvas: Canvas) {
        for (path in pathList) {
            paint.color = path.color
            paint.strokeWidth = path.width
            canvas.drawPath(path, paint)
        }

    }


    private data class CustomPath(var color: Int, val width: Float): Path()


}


