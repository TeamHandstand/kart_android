package us.handstand.kartwheel.layout

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat

object BitmapUtils {

    //region - Public

    fun drawBitmap(bitmap: Bitmap, centerPivot: Point, canvas: Canvas) {
        val bitmapWidth = bitmap.width
        val bitmapHeight = bitmap.height

        val srcRect = Rect(0, 0, bitmapWidth, bitmapHeight)
        val destRect = Rect(
                centerPivot.x - bitmapWidth / 2,
                centerPivot.y - bitmapHeight / 2,
                centerPivot.x + bitmapWidth / 2,
                centerPivot.y + bitmapHeight / 2)
        canvas.drawBitmap(bitmap, srcRect, destRect, null)
    }

    fun getBitmap(context: Context, contextDrawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, contextDrawableId)!!
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun getBoundedDimensionBitmap(bitmap: Bitmap, dimen: Int): Bitmap {
        var scaledWidth: Int
        var scaledHeight: Int
        var widthToHeightRatio = (bitmap.width / bitmap.height)
        if (widthToHeightRatio >= 1) {
            scaledHeight = dimen
            scaledWidth = (dimen * widthToHeightRatio)
        } else { // width is smaller than height
            scaledWidth = dimen
            scaledHeight = (dimen / widthToHeightRatio)
        }

        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false)
    }

    fun getCircularCroppedBitmap(bitmap: Bitmap, borderWidth: Float, backgroundColor: Int, borderColor: Int): Bitmap {
        val backgroundBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(backgroundBitmap)

        val radius = (bitmap.width / 2).toFloat()

        val paint = Paint()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = backgroundColor
        paint.style = Paint.Style.FILL

        canvas.drawCircle(radius, radius, radius, paint)

        canvas.drawBitmap(bitmap, 0.0f, 0.0f, null)

        // Draw border
        paint.style = Paint.Style.STROKE
        paint.color = borderColor
        paint.strokeWidth = borderWidth

        canvas.drawCircle(radius, radius, radius - borderWidth / 2, paint)

        return backgroundBitmap
    }

    fun getGrayscaleBitmap(bitmap: Bitmap): Bitmap {
        val height = bitmap.height
        val width = bitmap.width

        val grayScaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(grayScaleBitmap)
        val paint = Paint()
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        val matrixFilter = ColorMatrixColorFilter(matrix)
        paint.colorFilter = matrixFilter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return grayScaleBitmap
    }

    fun drawTextBitmapToCanvas(canvas: Canvas, text: String, textSize: Float, textColor: Int, location: Point) {
        val paint = Paint()
        paint.textSize = textSize
        paint.isAntiAlias = true
        paint.color = textColor
        val textBounds = Rect()
        paint.getTextBounds(text, 0 , text.length, textBounds)
        canvas.drawText(
                text,
                (location.x.toFloat() - textBounds.width() / 2 - textBounds.left),
                (location.y.toFloat() + textBounds.height() / 2 - textBounds.bottom),
                paint)
    }

    //endregion
}
