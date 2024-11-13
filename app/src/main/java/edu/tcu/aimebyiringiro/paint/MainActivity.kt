package edu.tcu.aimebyiringiro.paint

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap

/**
 * This error is caused by the ImageView in the Palette Layout
 * The view is not properly inflated, so when the program tries to set the OnClickListener
 * it crashes because the view is null.
 * To fix this, the Palette Layout should be inflated before the ImageView is referenced.
 */

class MainActivity : AppCompatActivity() {
    private lateinit var drawingView: DrawingView
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.image_iv)
        imageView = findViewById(R.id.background_iv)

        setUpPallets(drawingView)
        setUpWidthSelector(drawingView)
        setUpUndoButton(drawingView)
        setUpBackgroundPicker(imageView) // Pass imageView directly instead of findViewById again
        setUpSaveButton()
    }

    private fun setUpPallets(drawingView: DrawingView) {
        val paletteLayout = findViewById<LinearLayout>(R.id.palette_layout)
        val colorPaletteLayout = paletteLayout.getChildAt(0) as LinearLayout

        for (i in 0 until colorPaletteLayout.childCount) {
            val view = colorPaletteLayout.getChildAt(i)
            if (view is ImageView) {
                view.setOnClickListener {
                    val color = (view.background as ColorDrawable).color
                    drawingView.setPathColor(color)
                }
            } else {
                Log.e("MainActivity", "Unexpected view type in palette: ${view.javaClass.simpleName}")
            }
        }
    }

    private fun setUpWidthSelector(drawingView: DrawingView) {
        findViewById<ImageView>(R.id.brush_iv).setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.path_width_selector)
            dialog.findViewById<ImageView>(R.id.width_small).setOnClickListener {
                drawingView.setPathWidth(5f)
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.width_medium).setOnClickListener {
                drawingView.setPathWidth(10f)
                dialog.dismiss()
            }
            dialog.findViewById<ImageView>(R.id.width_large).setOnClickListener {
                drawingView.setPathWidth(15f)
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun setUpUndoButton(drawingView: DrawingView) {
        findViewById<ImageView>(R.id.undo_iv).setOnClickListener {
            drawingView.undo()
        }
    }

    private fun setUpBackgroundPicker(backgroundIv: ImageView) {
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                backgroundIv.setImageURI(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
        backgroundIv.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setUpSaveButton() {
        findViewById<ImageView>(R.id.save_iv).setOnClickListener {
            val bitmap = findViewById<FrameLayout>(R.id.drawing_fl).drawToBitmap()
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "drawing_${System.currentTimeMillis()}.png")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            uri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }
        }
    }
}