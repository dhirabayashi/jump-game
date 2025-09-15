package com.jumpgame.ui

import com.jumpgame.core.GameWorld
import com.jumpgame.core.InputHandler
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JPanel
import javax.swing.Timer

/**
 * The main game panel that handles rendering and game loop management.
 *
 * This class extends JPanel and implements ActionListener to provide the visual
 * representation of the game and handle the game update cycle through a Timer.
 * It manages the GameWorld, InputHandler, and rendering of all game elements.
 */
class GamePanel : JPanel(), ActionListener {
    
    /** The game world instance that manages game logic and entities */
    private val gameWorld = GameWorld()
    
    /** The input handler for processing keyboard input */
    private val inputHandler = InputHandler()
    
    /** Timer for the game loop, running at approximately 60 FPS */
    private val gameTimer = Timer(16, this) // ~60 FPS
    
    /** Flag indicating whether the game is currently running */
    private var isGameRunning = false
    
    companion object {
        /** Color used to render the player character */
        private val PLAYER_COLOR = Color.BLUE
        
        /** Color used to render the ground */
        private val GROUND_COLOR = Color.GREEN
        
        /** Color used for the background */
        private val BACKGROUND_COLOR = Color.CYAN
        
        /** Color used for UI text */
        private val TEXT_COLOR = Color.BLACK
        
        /** Color used to render enemies */
        private val ENEMY_COLOR = Color.RED
        
        /** Color used for game over text */
        private val GAME_OVER_COLOR = Color.RED
    }
    
    /**
     * Initializes the game panel with proper size, background, and input handling.
     * Sets up the panel to be focusable and adds the input handler as a key listener.
     * Automatically starts the game upon initialization.
     */
    init {
        preferredSize = Dimension(gameWorld.gameWidth, gameWorld.gameHeight)
        background = BACKGROUND_COLOR
        isFocusable = true
        addKeyListener(inputHandler)
        
        startGame()
    }
    
    /**
     * Starts the game by beginning the game loop timer.
     * If the game is already running, this method has no effect.
     * Also requests focus to ensure keyboard input is captured.
     */
    fun startGame() {
        if (!isGameRunning) {
            isGameRunning = true
            gameTimer.start()
            requestFocus()
        }
    }
    
    /**
     * Stops the game by halting the game loop timer.
     * If the game is not running, this method has no effect.
     */
    fun stopGame() {
        if (isGameRunning) {
            isGameRunning = false
            gameTimer.stop()
        }
    }
    
    /**
     * Resets the game to its initial state.
     * Resets both the game world and input handler, and starts the game if it's not running.
     */
    fun resetGame() {
        gameWorld.reset()
        inputHandler.reset()
        if (!isGameRunning) {
            startGame()
        }
    }
    
    /**
     * Called by the game timer on each tick.
     * Updates the game state and triggers a repaint if the game is running.
     *
     * @param e The ActionEvent from the timer (unused)
     */
    override fun actionPerformed(e: ActionEvent) {
        if (isGameRunning) {
            update()
            repaint()
        }
    }
    
    /**
     * Updates the game state by processing input and updating the game world.
     * Called once per frame during the game loop.
     */
    private fun update() {
        val currentInput = inputHandler.getCurrentInput()
        gameWorld.handleInput(currentInput)
        gameWorld.update()
    }
    
    /**
     * Renders the game by painting all visual elements.
     * Called automatically by Swing when the panel needs to be repainted.
     *
     * @param g The Graphics context to paint on
     */
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        
        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        
        drawBackground(g2d)
        drawGround(g2d)
        drawPlayer(g2d)
        drawEnemies(g2d)
        drawUI(g2d)
        
        if (gameWorld.isGameOver) {
            drawGameOver(g2d)
        }
    }
    
    /**
     * Draws the background of the game.
     *
     * @param g2d The Graphics2D context to draw on
     */
    private fun drawBackground(g2d: Graphics2D) {
        g2d.color = BACKGROUND_COLOR
        g2d.fillRect(0, 0, width, height)
    }
    
    /**
     * Draws the ground and floating platforms.
     *
     * @param g2d The Graphics2D context to draw on
     */
    private fun drawGround(g2d: Graphics2D) {
        g2d.color = GROUND_COLOR

        // Draw each platform using its defined thickness
        gameWorld.platforms.forEach { platform ->
            val platformWidth = platform.endX - platform.startX
            val platformHeight = platform.thickness

            g2d.fillRect(
                platform.startX,
                platform.y,
                platformWidth,
                platformHeight
            )

            // Add a border for better visibility
            g2d.color = Color.DARK_GRAY
            g2d.drawRect(
                platform.startX,
                platform.y,
                platformWidth,
                platformHeight
            )
            g2d.color = GROUND_COLOR
        }

    }
    
    /**
     * Draws the player character with a simple rectangular body and eyes.
     *
     * @param g2d The Graphics2D context to draw on
     */
    private fun drawPlayer(g2d: Graphics2D) {
        val player = gameWorld.player
        val bounds = player.getBounds()
        
        g2d.color = PLAYER_COLOR
        g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height)
        
        // Draw eyes
        g2d.color = Color.WHITE
        g2d.fillOval(bounds.x + 5, bounds.y + 8, 8, 8)
        g2d.fillOval(bounds.x + 19, bounds.y + 8, 8, 8)
        
        g2d.color = Color.BLACK
        g2d.fillOval(bounds.x + 7, bounds.y + 10, 4, 4)
        g2d.fillOval(bounds.x + 21, bounds.y + 10, 4, 4)
    }
    
    /**
     * Draws the user interface elements including player status and controls.
     *
     * @param g2d The Graphics2D context to draw on
     */
    private fun drawUI(g2d: Graphics2D) {
        g2d.color = TEXT_COLOR
        g2d.font = Font("Arial", Font.BOLD, 16)
        
        val player = gameWorld.player
        val position = player.position
        
        g2d.drawString("Position: (${position.x.toInt()}, ${position.y.toInt()})", 10, 30)
        g2d.drawString("On Ground: ${player.isOnGround}", 10, 50)
        g2d.drawString("Jumping: ${player.isJumping}", 10, 70)
        g2d.drawString("Alive: ${player.isAlive}", 10, 90)
        g2d.drawString("Lives: ${gameWorld.remainingLives}", 10, 110)
        g2d.drawString("Enemies: ${gameWorld.getEnemies().size}", 10, 130)
        
        // Draw lives in a prominent position
        g2d.color = Color.RED
        g2d.font = Font("Arial", Font.BOLD, 20)
        g2d.drawString("Lives: ${gameWorld.remainingLives}", width - 120, 30)
        
        g2d.color = TEXT_COLOR
        g2d.font = Font("Arial", Font.PLAIN, 12)
        g2d.drawString("Controls: Arrow Keys/WASD to move, Space/Up/W to jump", 10, height - 20)
    }
    
    /**
     * Returns the game world instance for external access.
     *
     * @return The GameWorld instance managed by this panel
     */
    fun getGameWorld(): GameWorld = gameWorld
    
    /**
     * Returns the input handler instance for external access.
     *
     * @return The InputHandler instance managed by this panel
     */
    fun getInputHandler(): InputHandler = inputHandler
    
    /**
     * Returns whether the game is currently running.
     *
     * @return true if the game is running, false otherwise
     */
    fun isRunning(): Boolean = isGameRunning
    
    /**
     * Draws all enemies in the game world.
     *
     * @param g2d The Graphics2D context to draw on
     */
    private fun drawEnemies(g2d: Graphics2D) {
        val enemies = gameWorld.getEnemies()
        g2d.color = ENEMY_COLOR
        
        enemies.forEach { enemy ->
            if (enemy.isAlive) {
                val bounds = enemy.getBounds()
                g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height)
                
                // Draw simple face
                g2d.color = Color.WHITE
                g2d.fillOval(bounds.x + 4, bounds.y + 6, 4, 4)
                g2d.fillOval(bounds.x + 14, bounds.y + 6, 4, 4)
                
                g2d.color = Color.BLACK
                g2d.fillOval(bounds.x + 5, bounds.y + 7, 2, 2)
                g2d.fillOval(bounds.x + 15, bounds.y + 7, 2, 2)
                
                // Reset to enemy color for next enemy
                g2d.color = ENEMY_COLOR
            }
        }
    }
    
    /**
     * Draws the game over screen with restart instructions.
     *
     * @param g2d The Graphics2D context to draw on
     */
    private fun drawGameOver(g2d: Graphics2D) {
        // Semi-transparent overlay
        g2d.color = Color(0, 0, 0, 128)
        g2d.fillRect(0, 0, width, height)
        
        // Game over text
        g2d.color = GAME_OVER_COLOR
        g2d.font = Font("Arial", Font.BOLD, 48)
        val gameOverText = "GAME OVER"
        val fm = g2d.fontMetrics
        val textX = (width - fm.stringWidth(gameOverText)) / 2
        val textY = height / 2 - 50
        g2d.drawString(gameOverText, textX, textY)
        
        // Restart instructions
        g2d.color = Color.WHITE
        g2d.font = Font("Arial", Font.PLAIN, 20)
        val restartText = "Press F2 or use Game menu to restart"
        val restartFm = g2d.fontMetrics
        val restartX = (width - restartFm.stringWidth(restartText)) / 2
        val restartY = textY + 60
        g2d.drawString(restartText, restartX, restartY)
    }
}