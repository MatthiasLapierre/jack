package com.matthiaslapierre.jumper.core.impl

import com.matthiaslapierre.core.Constants
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.JumperConstants
import com.matthiaslapierre.jumper.core.JumperGameMap
import com.matthiaslapierre.jumper.core.JumperGameStates
import com.matthiaslapierre.jumper.core.impl.sprites.collectibles.CandySprite
import com.matthiaslapierre.jumper.core.impl.sprites.collectibles.PowerUpSprite
import com.matthiaslapierre.jumper.core.impl.sprites.obstacles.BatSprite
import com.matthiaslapierre.jumper.core.impl.sprites.obstacles.SpikeSprite
import com.matthiaslapierre.jumper.core.impl.sprites.platforms.JumpingPlatformSprite
import com.matthiaslapierre.utils.Utils
import kotlin.math.abs
import kotlin.math.floor

internal class JumperGameMapImpl(
    override val resourceManager: ResourceManager,
    override val gameStates: JumperGameStates
): JumperGameMap {

    companion object {
        private const val PATTERN_INITIAL_JUMPING_PLATFORM = 1
        private const val PATTERN_JUMPING_PLATFORM = 2
        private const val PATTERN_CANDIES_LINE = 3
        private const val PATTERN_CANDIES_GAP = 4
        private const val PATTERN_BAT = 5
        private const val PATTERN_SPIKE = 6
    }

    private var lastGeneratedSprite: Sprite? = null
    private var countObstaclesGenerated: Int = 0
    private var countPowerUpsGenerated: Int = 0
    private var elevation: Float = 0f

    private var screenWidth: Float = Constants.UNDEFINED
    private var screenHeight: Float = Constants.UNDEFINED

    override fun generate(): List<Sprite> {
        synchronized(this) {
            val generatedSprites = mutableListOf<Sprite>()

            if (screenHeight == Constants.UNDEFINED) {
                return generatedSprites
            }

            var nextSpriteY = screenHeight - (screenWidth * JumperConstants.FIRST_SPRITE_Y)
            var nextSpriteX = getNextSpriteX(null)
            var previousPattern: Int? = null
            if (lastGeneratedSprite != null) {
                previousPattern = getPattern(lastGeneratedSprite!!)
                nextSpriteX = getNextSpriteX(lastGeneratedSprite!!.x)
                nextSpriteY = lastGeneratedSprite!!.y
            }
            var nextPattern = getNextPattern(previousPattern)
            nextSpriteY -= getSpriteMarginTop(previousPattern, nextPattern)
            while (nextSpriteY > -(screenHeight * JumperConstants.GENERATOR_HIGHEST_Y)) {
                val sprites = buildSprites(nextSpriteX, nextSpriteY, nextPattern)
                generatedSprites.addAll(sprites)
                when (nextPattern) {
                    PATTERN_BAT, PATTERN_SPIKE -> countObstaclesGenerated++
                    PATTERN_CANDIES_LINE, PATTERN_CANDIES_GAP ->
                        countPowerUpsGenerated += sprites.filterIsInstance<PowerUpSprite>().size
                }
                val previousGeneratedSprite = if (lastGeneratedSprite != null) {
                    lastGeneratedSprite
                } else {
                    sprites.first()
                }
                lastGeneratedSprite = sprites.last()
                elevation += abs(lastGeneratedSprite!!.y - previousGeneratedSprite!!.y)
                nextSpriteY = lastGeneratedSprite!!.y
                nextSpriteX = getNextSpriteX(lastGeneratedSprite!!.x)
                previousPattern = nextPattern
                nextPattern = getNextPattern(previousPattern)
                nextSpriteY -= getSpriteMarginTop(previousPattern, nextPattern)
            }
            return generatedSprites
        }
    }

    override fun setScreenSize(screenWidth: Float, screenHeight: Float) {
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight
    }

    private fun getNextPattern(previousPattern: Int?): Int {
        return if (previousPattern == null) {
            PATTERN_INITIAL_JUMPING_PLATFORM
        } else if(previousPattern == PATTERN_INITIAL_JUMPING_PLATFORM) {
            val patterns = arrayOf(
                PATTERN_CANDIES_GAP,
                PATTERN_CANDIES_LINE
            )
            patterns[Utils.getRandomInt(0, patterns.size)]
        } else if(obstacleGenerationAllowed()) {
            if (elevation > screenWidth * JumperConstants.SPIKE_FIRST_INTERVAL) {
                if(Utils.getRandomInt(0, 100) > JumperConstants.DRAW_CHANCE_SPIKE) {
                    PATTERN_SPIKE
                } else {
                    PATTERN_BAT
                }
            } else {
                PATTERN_BAT
            }
        } else {
            val patterns = arrayOf(
                PATTERN_CANDIES_GAP,
                PATTERN_CANDIES_LINE,
                PATTERN_JUMPING_PLATFORM
            )
            patterns[Utils.getRandomInt(0, patterns.size)]
        }
    }

    private fun obstacleGenerationAllowed(): Boolean {
        return floor(elevation / Utils.getRandomFloat(
            screenWidth * JumperConstants.OBSTACLE_INTERVAL_MIN,
            screenWidth * JumperConstants.OBSTACLE_INTERVAL_MAX
        )) > countObstaclesGenerated
    }

    private fun powerUpGenerationAllowed(): Boolean {
        return floor(elevation / Utils.getRandomFloat(
            screenWidth * JumperConstants.POWER_UP_INTERVAL_MIN,
            screenWidth * JumperConstants.POWER_UP_INTERVAL_MAX
        )) > countPowerUpsGenerated
    }

    private fun getNextSpriteX(x: Float?): Float {
        return if (x != null) {
            val swing = screenWidth * JumperConstants.SPRITE_SWING_X
            val minX = 0f.coerceAtLeast(x - swing)
            val maxX = screenWidth.coerceAtMost(x + swing)
            Utils.getRandomFloat(minX, maxX)
        } else {
            Utils.getRandomFloat(0f, screenWidth)
        }
    }

    private fun buildSprites(x: Float, y: Float, pattern: Int): List<Sprite> =
        when(pattern) {
            PATTERN_INITIAL_JUMPING_PLATFORM -> buildJumpingPlatformSprites(
                x,
                y,
                JumperConstants.MAX_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS
            )
            PATTERN_JUMPING_PLATFORM -> buildJumpingPlatformSprites(
                x,
                y,
                Utils.getRandomInt(
                    JumperConstants.MIN_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS,
                    JumperConstants.MAX_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS
                )
            )
            PATTERN_CANDIES_LINE -> buildCandiesLineSprites(x, y)
            PATTERN_CANDIES_GAP -> buildCandiesGapSprites(y)
            PATTERN_SPIKE -> buildSpikeSprite(y)
            PATTERN_BAT -> buildBatSprites(x, y)
            else -> buildJumpingPlatformSprites(
                x,
                y,
                Utils.getRandomInt(
                    JumperConstants.MIN_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS,
                    JumperConstants.MAX_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS
                )
            )
        }

    private fun buildJumpingPlatformSprites(
        x: Float,
        y: Float,
        capacity: Int
    ): List<Sprite> {
        var nextX = x
        var nextY = y
        return (1..capacity).map {
            val jumpingPlatformSprite = buildJumpingPlatformSprite(nextX, nextY)
            nextX = getNextSpriteX(nextX)
            nextY -= Utils.getRandomFloat(
                screenWidth * JumperConstants.JUMPING_PLATFORM_MIN_MARGIN_TOP,
                screenWidth * JumperConstants.JUMPING_PLATFORM_MAX_MARGIN_TOP
            )
            jumpingPlatformSprite
        }
    }

    private fun buildCandiesLineSprites(
        x: Float,
        y: Float
    ): List<Sprite> {
        val capacityX = Utils.getRandomInt(1,
            JumperConstants.MAX_CANDIES_X_CAPACITY
        )
        val capacityY = Utils.getRandomInt(1,
            JumperConstants.MAX_CANDIES_Y_CAPACITY
        )
        val outset = (screenWidth * JumperConstants.CANDY_OUTSET)
        val spaceX = (screenWidth * JumperConstants.CANDY_SPACE_X)
        val spaceY = (screenWidth * JumperConstants.CANDY_SPACE_Y)
        val maxX = screenWidth - ((capacityX - 1) * spaceX) - outset
        val startX = when {
            x > maxX -> {
                maxX
            }
            x < outset -> {
                outset
            }
            else -> {
                x
            }
        }
        var spriteX = startX
        var spriteY = y
        val sprites = mutableListOf<Sprite>()
        var powerUpGenerated = false
        for (indexY in 0 until capacityY) {
            for (indexX in 0 until capacityX) {
                if (powerUpGenerationAllowed() && !powerUpGenerated) {
                    sprites.add(buildPowerUpSprite(spriteX, spriteY))
                    powerUpGenerated = true
                } else {
                    sprites.add(CandySprite(resourceManager, gameStates, spriteX, spriteY))
                }
                spriteX += spaceX
            }
            spriteX = startX
            spriteY -= spaceY
        }
        return sprites
    }

    private fun buildCandiesGapSprites(
        y: Float
    ): List<Sprite> {
        val sprites = mutableListOf<Sprite>()
        val capacityY = Utils.getRandomInt(1,
            JumperConstants.MAX_CANDIES_Y_CAPACITY
        )
        val spaceY = (screenWidth * JumperConstants.CANDY_SPACE_Y)
        var spriteY = y
        for (indexY in (1..capacityY)) {
            for (indexX in (1..2)) {
                val sprite = CandySprite(
                    resourceManager,
                    gameStates,
                    screenWidth * .33f * indexX,
                    spriteY
                )
                sprites.add(sprite)
            }
            spriteY -= spaceY
        }

        if (powerUpGenerationAllowed()) {
            val powerUpX = screenWidth*.5f
            val powerUpY = Utils.getRandomFloat(y, spriteY + spaceY)
            sprites.add(buildPowerUpSprite(powerUpX, powerUpY))
        }

        return sprites
    }

    private fun buildBatSprites(x: Float, y: Float): List<Sprite> {
        val outset = (screenWidth * JumperConstants.BAT_OUTSET)
        val minX = outset
        val maxX = screenWidth - outset
        val spriteX = when {
            x < minX -> {
                minX
            }
            x > maxX -> {
                maxX
            }
            else -> {
                x
            }
        }
        val batSprite = BatSprite(
            resourceManager,
            gameStates,
            spriteX,
            y,
            minX,
            maxX
        )
        return listOf(batSprite)
    }

    private fun buildSpikeSprite(y: Float): List<Sprite> {
        return listOf(SpikeSprite(resourceManager, gameStates, y))
    }

    private fun buildJumpingPlatformSprite(x: Float, y: Float): Sprite {
        val outset = screenWidth * JumperConstants.JUMPING_PLATFORM_OUTSET
        val maxX = screenWidth - outset
        val spriteX = when {
            x > maxX -> {
                maxX
            }
            x < outset -> {
                outset
            }
            else -> {
                x
            }
        }
        return JumpingPlatformSprite(resourceManager, gameStates, spriteX, y)
    }

    private fun buildPowerUpSprite(x: Float, y: Float): Sprite {
        return PowerUpSprite(resourceManager, gameStates, x, y, getRandomPowerUp())
    }

    private fun getRandomPowerUp(): Int {
        val randomInt = Utils.getRandomInt(1, 100)
        return when {
            randomInt <= JumperConstants.DRAW_CHANCE_POWER_UP_COPTER -> JumperGameStates.POWER_UP_COPTER
            randomInt <= JumperConstants.DRAW_CHANCE_POWER_UP_COPTER + JumperConstants.DRAW_CHANCE_POWER_UP_MAGNET -> JumperGameStates.POWER_UP_MAGNET
            randomInt <= JumperConstants.DRAW_CHANCE_POWER_UP_COPTER + JumperConstants.DRAW_CHANCE_POWER_UP_MAGNET + JumperConstants.DRAW_CHANCE_POWER_UP_ROCKET -> JumperGameStates.POWER_UP_ROCKET
            randomInt <= JumperConstants.DRAW_CHANCE_POWER_UP_COPTER + JumperConstants.DRAW_CHANCE_POWER_UP_MAGNET + JumperConstants.DRAW_CHANCE_POWER_UP_ROCKET + JumperConstants.DRAW_CHANCE_POWER_UP_SHIELD -> JumperGameStates.POWER_UP_ARMORED
            else -> JumperGameStates.POWER_UP_ARMORED
        }
    }

    private fun getSpriteMarginTop(previousPattern: Int?, nextPattern: Int?): Float =
        if (previousPattern == null) {
            0f
        } else if (nextPattern == PATTERN_BAT || nextPattern == PATTERN_SPIKE) {
            when (previousPattern) {
                PATTERN_CANDIES_LINE, PATTERN_CANDIES_GAP -> screenWidth * JumperConstants.CANDIES_MARGIN_TOP_BEFORE_OBSTACLE
                else -> screenWidth * JumperConstants.JUMPING_PLATFORM_MARGIN_TOP_BEFORE_OBSTACLE
            }
        } else {
            when (previousPattern) {
                PATTERN_CANDIES_LINE, PATTERN_CANDIES_GAP -> Utils.getRandomFloat(
                    screenWidth * JumperConstants.CANDIES_MIN_MARGIN_TOP,
                    screenWidth * JumperConstants.CANDIES_MAX_MARGIN_TOP
                )
                PATTERN_BAT -> Utils.getRandomFloat(
                    screenWidth * JumperConstants.BAT_MIN_MARGIN_TOP,
                    screenWidth * JumperConstants.BAT_MAX_MARGIN_TOP
                )
                PATTERN_SPIKE -> Utils.getRandomFloat(
                    screenWidth * JumperConstants.SPIKE_MIN_MARGIN_TOP,
                    screenWidth * JumperConstants.SPIKE_MAX_MARGIN_TOP
                )
                else -> Utils.getRandomFloat(
                    screenWidth * JumperConstants.JUMPING_PLATFORM_MIN_MARGIN_TOP,
                    screenWidth * JumperConstants.JUMPING_PLATFORM_MAX_MARGIN_TOP
                )
            }
        }

    private fun getPattern(sprite: Sprite): Int =
        when (sprite) {
            is CandySprite, is PowerUpSprite -> PATTERN_CANDIES_LINE
            is BatSprite -> PATTERN_BAT
            is SpikeSprite -> PATTERN_SPIKE
            else -> PATTERN_JUMPING_PLATFORM
        }

}