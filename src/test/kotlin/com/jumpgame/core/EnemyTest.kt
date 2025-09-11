package com.jumpgame.core

import com.jumpgame.util.Vector2D
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

/**
 * Unit tests for the Enemy class.
 *
 * Tests enemy movement, collision detection, defeat mechanics,
 * boundary collision, and stomp detection.
 */
class EnemyTest {

    private lateinit var enemy: Enemy
    private val gameWidth = 800

    @BeforeEach
    fun setUp() {
        enemy = Enemy(Vector2D(400, 368)) // Position on ground (400 - 32 = 368)
    }

    @Test
    fun `should initialize enemy with correct properties`() {
        assertEquals(400.0, enemy.position.x, 0.01)
        assertEquals(368.0, enemy.position.y, 0.01)
        assertTrue(enemy.isAlive)
        assertEquals(24, enemy.width)
        assertEquals(32, enemy.height)
        
        // Should start moving left
        assertTrue(enemy.velocity.x < 0)
        assertEquals(0.0, enemy.velocity.y, 0.01)
    }

    @Test
    fun `should move left initially`() {
        val initialX = enemy.position.x
        enemy.update(0.1, gameWidth)
        
        assertTrue(enemy.position.x < initialX)
        assertEquals(368.0, enemy.position.y, 0.01) // Should stay on ground
    }

    @Test
    fun `should reverse direction when hitting left boundary`() {
        // Move enemy to left edge
        enemy.position = Vector2D(0.0, 368.0)
        enemy.update(0.1, gameWidth)
        
        // Should now be moving right
        assertTrue(enemy.velocity.x > 0)
        assertEquals(0.0, enemy.position.x, 0.01) // Should be at boundary
    }

    @Test
    fun `should reverse direction when hitting right boundary`() {
        // Move enemy to right edge and make it move right
        enemy.position = Vector2D(gameWidth - 24.0, 368.0)
        // First update to set velocity to left, then manually set to right for test
        enemy.update(0.1, gameWidth)
        
        // Use reflection or create enemy moving right to test right boundary
        val rightMovingEnemy = Enemy(Vector2D(gameWidth - 10.0, 368.0))
        // Manually set velocity to right for testing
        rightMovingEnemy.velocity = Vector2D(50.0, 0.0)
        rightMovingEnemy.update(0.1, gameWidth)
        
        // Should now be at right boundary and moving left
        assertTrue(rightMovingEnemy.velocity.x < 0)
        assertEquals(gameWidth - 24.0, rightMovingEnemy.position.x, 0.01)
    }

    @Test
    fun `should stay on ground level`() {
        // Set enemy above ground
        enemy.position = Vector2D(400.0, 300.0)
        enemy.update(0.1, gameWidth)
        
        // Should be moved to ground level
        assertEquals(368.0, enemy.position.y, 0.01) // 400 - 32 = 368
    }

    @Test
    fun `should be defeated when defeat is called`() {
        assertTrue(enemy.isAlive)
        
        enemy.defeat()
        
        assertFalse(enemy.isAlive)
        assertEquals(Vector2D.ZERO, enemy.velocity)
    }

    @Test
    fun `should not update when defeated`() {
        enemy.defeat()
        val initialPosition = enemy.position
        
        enemy.update(0.1, gameWidth)
        
        assertEquals(initialPosition, enemy.position)
    }

    @Test
    fun `should detect collision with player bounds`() {
        val playerBounds = PlayerBounds(400, 368, 32, 48)
        
        assertTrue(enemy.collidesWith(playerBounds))
    }

    @Test
    fun `should not detect collision when player is far away`() {
        val playerBounds = PlayerBounds(100, 100, 32, 48)
        
        assertFalse(enemy.collidesWith(playerBounds))
    }

    @Test
    fun `should not detect collision when enemy is defeated`() {
        enemy.defeat()
        val playerBounds = PlayerBounds(400, 368, 32, 48)
        
        assertFalse(enemy.collidesWith(playerBounds))
    }

    @Test
    fun `should detect stomp when player is above and falling`() {
        val playerBounds = PlayerBounds(400, 330, 32, 48) // Player above enemy (bottom at 378)
        val fallingVelocity = Vector2D(0.0, 100.0) // Falling down
        
        assertTrue(enemy.isBeingStomped(playerBounds, fallingVelocity))
    }

    @Test
    fun `should not detect stomp when player is not falling`() {
        val playerBounds = PlayerBounds(400, 350, 32, 48) // Player above enemy
        val upwardVelocity = Vector2D(0.0, -100.0) // Moving up
        
        assertFalse(enemy.isBeingStomped(playerBounds, upwardVelocity))
    }

    @Test
    fun `should not detect stomp when player is beside enemy`() {
        val playerBounds = PlayerBounds(430, 368, 32, 48) // Player beside enemy
        val fallingVelocity = Vector2D(0.0, 100.0) // Falling down
        
        assertFalse(enemy.isBeingStomped(playerBounds, fallingVelocity))
    }

    @Test
    fun `should not detect stomp when enemy is defeated`() {
        enemy.defeat()
        val playerBounds = PlayerBounds(400, 350, 32, 48) // Player above enemy
        val fallingVelocity = Vector2D(0.0, 100.0) // Falling down
        
        assertFalse(enemy.isBeingStomped(playerBounds, fallingVelocity))
    }

    @Test
    fun `should get correct bounds`() {
        val bounds = enemy.getBounds()
        
        assertEquals(400, bounds.x)
        assertEquals(368, bounds.y)
        assertEquals(24, bounds.width)
        assertEquals(32, bounds.height)
    }

    @Test
    fun `should handle continuous movement correctly`() {
        val initialX = enemy.position.x
        
        // Update multiple times
        repeat(10) {
            enemy.update(0.016, gameWidth) // Simulate 60 FPS
        }
        
        // Should have moved left
        assertTrue(enemy.position.x < initialX)
        assertTrue(enemy.isAlive)
    }
}