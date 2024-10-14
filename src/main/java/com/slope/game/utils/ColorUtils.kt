package com.slope.game.utils

object ColorUtils {
    fun isDangerousColor(color:FloatArray): Boolean{
        val  redThreshold = 0.8f
        val redValue = color[0]
        return redValue >= redThreshold
    }
    fun checkForDangerousColors(model: Model) {
        val colorArray = model.colorArray
        val colorsPerFace = 4

        for (i in colorArray.indices step colorsPerFace) {
            val faceColor = colorArray.copyOfRange(i, i + colorsPerFace)

            if (ColorUtils.isDangerousColor(faceColor)) {
                // Triggering an event when a dangerous color is detected
                triggerDangerousSurfaceEvent()
            }
        }
    }
    fun triggerDangerousSurfaceEvent() {
        println("Player hit a dangerous surface!")
        
        // Implement your game logic here
    }



}
