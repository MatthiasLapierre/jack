package com.matthiaslapierre.jumper

internal object JumperConstants  {

    const val GRAVITY = 0.00078f
    const val ACCELEROMETER_SENSITIVITY = 120f
    const val CANDIES_ACCELERATION = 0.033f
    const val JUMP_ACCELERATION = 0.025f
    const val ROCKET_SPEED = 0.033f
    const val COPTER_SPEED = 0.02f
    const val MAX_FALL_SPEED = 0.02f
    const val CLOUD_SPEED_DECELERATION = 0.5f
    const val BACKGROUND_SPEED_DECELERATION = 0.3f

    const val FLOOR_HEIGHT = .27f

    const val PLAYER_WIDTH = .27f
    const val PLAYER_HIGHEST_Y = 0.5f
    const val PLAYER_INITIAL_POSITION = .18f
    const val PLAYER_INSET_X = .3f
    const val PLAYER_INSET_Y = .1f
    const val PLAYER_FEET_TOP = .2f
    const val PLAYER_FEET_BOTTOM = .1f

    const val SPRITE_LIFE_LOWEST_Y = 1.2f
    const val FREE_FALL_MAX = .70f

    const val CLOUD_INTERVAL = 0.6f
    const val FIRST_CLOUD_Y = 4.5f

    const val JUMPING_PLATFORM_WIDTH = .2f
    const val JUMPING_PLATFORM_BOUNCE_AREA_OUTSET = .2f
    const val JUMPING_PLATFORM_BOUNCE_AREA_HEIGHT = .5f

    const val CANDY_WIDTH = .15f

    const val BAT_WIDTH = .27f
    const val BAT_SPEED = .02f
    const val BAT_FRAME_RATE = 120
    const val BAT_BODY_INSET_X = .1f
    const val BAT_BODY_INSET_Y = .2f

    const val SPIKE_WIDTH = .15f
    const val SPIKE_WRAITH_DURATION = 3
    const val SPIKE_HIGHEST_Y = .2f

    const val CLOUD_OUTSET = .05f
    const val CLOUD_MIN_WIDTH = .3f
    const val CLOUD_MAX_WIDTH = .45f

    const val POWER_UP_WIDTH = .15f
    const val ROCKET_TOP = .07f

    const val ROCKET_TIMER = 5
    const val MAGNET_TIMER = 10
    const val COPTER_TIMER = 10

    const val MAGNET_RANGE_X = .5f

    // Generator
    const val GENERATOR_HIGHEST_Y = 2f
    const val SPRITE_SWING_X = .5f
    const val FIRST_SPRITE_Y = .45f
    // Outsets
    const val BAT_OUTSET = .14f
    const val CANDY_SPACE_X = .18f
    const val CANDY_SPACE_Y = .12f
    const val CANDY_OUTSET = .09f
    const val JUMPING_PLATFORM_OUTSET = .12f
    // -- Margin top
    const val CANDIES_MIN_MARGIN_TOP = 0.15f
    const val CANDIES_MAX_MARGIN_TOP = 0.3f
    const val CANDIES_MARGIN_TOP_BEFORE_OBSTACLE = 0.15f
    const val JUMPING_PLATFORM_MIN_MARGIN_TOP = 0.15f
    const val JUMPING_PLATFORM_MAX_MARGIN_TOP = 0.3f
    const val JUMPING_PLATFORM_MARGIN_TOP_BEFORE_OBSTACLE = 0.12f
    const val BAT_MIN_MARGIN_TOP = 0.15f
    const val BAT_MAX_MARGIN_TOP = 0.16f
    const val SPIKE_MIN_MARGIN_TOP = 0.14f
    const val SPIKE_MAX_MARGIN_TOP = 0.15f
    // Jumping platforms
    const val MIN_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS = 1
    const val MAX_NUMBER_SUCCESSIVE_JUMPING_PLATFORMS = 10
    // Candies
    const val MAX_CANDIES_X_CAPACITY = 3
    const val MAX_CANDIES_Y_CAPACITY = 10
    // Power-up
    const val POWER_UP_INTERVAL_MIN = 6f
    const val POWER_UP_INTERVAL_MAX = 10f
    const val DRAW_CHANCE_POWER_UP_COPTER = 25
    const val DRAW_CHANCE_POWER_UP_MAGNET = 25
    const val DRAW_CHANCE_POWER_UP_ROCKET = 25
    const val DRAW_CHANCE_POWER_UP_SHIELD = 25
    // Obstacles interval
    const val OBSTACLE_INTERVAL_MIN = 12f
    const val OBSTACLE_INTERVAL_MAX = 16f
    const val SPIKE_FIRST_INTERVAL = 40f
    const val DRAW_CHANCE_SPIKE = 40
}