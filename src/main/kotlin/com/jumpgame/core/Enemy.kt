package com.jumpgame.core

import com.jumpgame.util.Vector2D

/**
 * Represents an enemy character that moves automatically and can be defeated by stomping.
 *
 * Enemies walk mechanically from side to side, reversing direction when they reach
 * the game boundaries. They can be defeated by the player jumping on top of them,
 * but will cause the player to die if touched from the side or bottom.
 *
 * @property initialPosition The starting position of the enemy in the game world
 */
class Enemy(initialPosition: Vector2D) {
    
    /** Current position of the enemy in the game world */
    var position: Vector2D = initialPosition
    
    /** Current velocity of the enemy */
    var velocity: Vector2D = Vector2D(-MOVE_SPEED, 0.0) // Start moving left
    
    /** Flag indicating whether the enemy is alive */
    var isAlive: Boolean = true
        private set
    
    /** Width of the enemy's collision box in pixels */
    val width: Int = 24
    
    /** Height of the enemy's collision box in pixels */
    val height: Int = 32
    
    companion object {
        /** Horizontal movement speed in pixels per second */
        private const val MOVE_SPEED = 50.0
        
        /** Y-coordinate of the ground level */
        private const val GROUND_LEVEL = 400.0
    }
    
    /**
     * Updates the enemy's position and handles boundary collision.
     * Should be called once per frame to maintain proper movement.
     *
     * @param deltaTime The time elapsed since the last update in seconds
     * @param gameWidth The width of the game world for boundary checking
     */
    fun update(deltaTime: Double, gameWidth: Int) {
        if (!isAlive) return
        
        // Update position
        position = position.add(velocity.multiply(deltaTime))
        
        // Check boundaries and reverse direction if needed
        val bounds = getBounds()
        when {
            bounds.x <= 0 -> {
                position = Vector2D(0.0, position.y)
                velocity = Vector2D(MOVE_SPEED, velocity.y) // Move right
            }
            bounds.x + bounds.width >= gameWidth -> {
                position = Vector2D(gameWidth - width.toDouble(), position.y)
                velocity = Vector2D(-MOVE_SPEED, velocity.y) // Move left
            }
        }
        
        // Keep enemy on ground
        if (position.y + height < GROUND_LEVEL) {
            position = Vector2D(position.x, GROUND_LEVEL - height)
        }
    }
    
    /**
     * Returns the collision bounds of the enemy for collision detection and rendering.
     *
     * @return EnemyBounds object containing the enemy's rectangular boundaries
     */
    fun getBounds(): EnemyBounds {
        return EnemyBounds(
            position.x.toInt(),
            position.y.toInt(),
            width,
            height
        )
    }
    
    /**
     * Marks the enemy as defeated and stops its movement.
     * Called when the player successfully stomps on the enemy.
     */
    fun defeat() {
        isAlive = false
        velocity = Vector2D.ZERO
    }
    
    /**
     * Checks if the given bounds overlap with this enemy's bounds.
     * Used for collision detection with the player.
     *
     * @param other The bounds to check collision against
     * @return true if the bounds overlap, false otherwise
     */
    fun collidesWith(other: PlayerBounds): Boolean {
        if (!isAlive) return false
        
        val enemyBounds = getBounds()
        return enemyBounds.x < other.x + other.width &&
               enemyBounds.x + enemyBounds.width > other.x &&
               enemyBounds.y < other.y + other.height &&
               enemyBounds.y + enemyBounds.height > other.y
    }
    
    /**
     * Checks if the player is stomping on this enemy (jumping on top).
     * A stomp is detected when the player is above the enemy and falling downward.
     *
     * @param playerBounds The player's collision bounds
     * @param playerVelocity The player's current velocity
     * @return true if the player is stomping on the enemy, false otherwise
     */
    fun isBeingStomped(playerBounds: PlayerBounds, playerVelocity: Vector2D): Boolean {
        if (!isAlive) return false
        
        val enemyBounds = getBounds()
        val playerBottom = playerBounds.y + playerBounds.height
        val enemyTop = enemyBounds.y
        
        // Check if there's horizontal overlap
        val horizontalOverlap = playerBounds.x < enemyBounds.x + enemyBounds.width &&
                               playerBounds.x + playerBounds.width > enemyBounds.x
        
        // Check if player is above enemy and falling down
        val playerAbove = playerBottom <= enemyTop + 15
        val falling = playerVelocity.y > 0
        
        return horizontalOverlap && playerAbove && falling
    }
}

/**
 * Represents the rectangular boundaries of an enemy for collision detection and rendering.
 *
 * @property x The x-coordinate of the top-left corner
 * @property y The y-coordinate of the top-left corner
 * @property width The width of the bounding rectangle
 * @property height The height of the bounding rectangle
 */
data class EnemyBounds(val x: Int, val y: Int, val width: Int, val height: Int)