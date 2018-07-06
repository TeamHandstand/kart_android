package us.handstand.kartwheel.layout

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import us.handstand.kartwheel.R

class AvatarUtil(context: Context) {
    private val context: Context = context

    //region - Public

    // TODO: Update with UserRaceInfo
    // For testing purposes, we shall hardcode values
    fun createAvatarMarkerBitmap(profileBitmap: Bitmap,
                                 userState: String,
                                 isTargeted: Boolean,
                                 ranking: Int,
                                 armored: Boolean): Bitmap {
        val bitmapDimen = ViewUtil.dpToPx(context, 200)
        val backgroundBitmap = Bitmap.createBitmap(bitmapDimen, bitmapDimen, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(backgroundBitmap)

        val canvasWidth = backgroundBitmap.width
        val canvasHeight = backgroundBitmap.height

        val center = Point(canvasWidth / 2, canvasHeight / 2)
        val borderWidth = ViewUtil.dpToPx(context, 2)

        // Draw the profile bitmap
        val boundedProfileBitmap = BitmapUtils.getBoundedDimensionBitmap(profileBitmap, ViewUtil.dpToPx(context, 50))

        // TODO: Currently, we will just assign the border color based off placement, but there is a bit more
        // complex state-based logic to set the correct color
        var color = if (ranking == 1) Color.YELLOW else ContextCompat.getColor(context, R.color.light_grey)

        BitmapUtils.drawBitmap(BitmapUtils.getCircularCroppedBitmap(
                boundedProfileBitmap,
                borderWidth.toFloat(),
                Color.WHITE,
                color),
                center,
                canvas)

        // If we are being targeted, draw the targeted bitmap
        if (isTargeted) {
            val targetingBitmap = BitmapUtils.getBitmap(context, R.drawable.ic_targeting_crosshairs)
            BitmapUtils.drawBitmap(targetingBitmap, center, canvas)
        }

        val profileHeight = profileBitmap.height
        val profileWidth = profileBitmap.width

        val profileCenterTop = Point(center.x, center.y - profileHeight / 2)
        val profileBottomLeft = Point(center.x - profileWidth / 2, center.y + profileHeight / 2)
        val profileBottomRight = Point(center.x + profileWidth / 2, center.y + profileHeight / 2)

        // Overlay status affliction
        // TODO: Map userState to enums
        when (userState) {
            "disconnected" -> BitmapUtils.drawTextBitmapToCanvas(
                    canvas,
                    "☠",
                    ViewUtil.spToPx(context, 35).toFloat(),
                    Color.DKGRAY,
                    center)
            "injured" -> {
                val injuredBitmap = BitmapUtils.getBitmap(context, R.drawable.ic_injured_bandage)
                var injuredBitmapHeight = injuredBitmap.height
                val injuredBitmapCenter = Point(profileCenterTop.x,
                        (profileCenterTop.y + injuredBitmapHeight / 2 - borderWidth))
                BitmapUtils.drawBitmap(injuredBitmap, injuredBitmapCenter, canvas)
            }
        }

        // If we are in first place, let's add the crown
        if (ranking == 1) {
            val crownBitmap = BitmapUtils.getBitmap(context, R.drawable.first_place_crown)
            val crownCenter = Point(profileCenterTop.x, (profileCenterTop.y - crownBitmap.height / 4))
            BitmapUtils.drawBitmap(crownBitmap, crownCenter, canvas)
        }

        // Create item (if it exists)
        if (armored) {
            val shieldBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.avatar_shield)
            BitmapUtils.drawBitmap(shieldBitmap, profileBottomLeft, canvas)
        }

        // Create the buddy (always exists)
        // TODO: For testing purposes, we will just draw the current user's buddy url indiscriminately
//            Storage.userBuddyUrl
        val buddyBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.avatar_shield)
        BitmapUtils.drawBitmap(buddyBitmap, profileBottomRight, canvas)

        return backgroundBitmap
    }

    //endregion

}