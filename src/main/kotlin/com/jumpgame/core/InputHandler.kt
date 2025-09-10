package com.jumpgame.core

import java.awt.event.KeyEvent
import java.awt.event.KeyListener

/**
 * Handles keyboard input for the game, implementing KeyListener interface.
 *
 * This class manages the state of pressed keys and provides convenient methods
 * to check for game-specific input states like movement and jumping.
 */
class InputHandler : KeyListener {
    
    /** Set containing the key codes of currently pressed keys */
    private val pressedKeys = mutableSetOf<Int>()
    
    /**
     * Returns true if any left movement key is currently pressed.
     * Supports both arrow key (VK_LEFT) and WASD (VK_A) input.
     */
    val isLeftPressed: Boolean
        get() = KeyEvent.VK_LEFT in pressedKeys || KeyEvent.VK_A in pressedKeys
    
    /**
     * Returns true if any right movement key is currently pressed.
     * Supports both arrow key (VK_RIGHT) and WASD (VK_D) input.
     */
    val isRightPressed: Boolean
        get() = KeyEvent.VK_RIGHT in pressedKeys || KeyEvent.VK_D in pressedKeys
    
    /**
     * Returns true if any jump key is currently pressed.
     * Supports space bar, up arrow key, and W key input.
     */
    val isJumpPressed: Boolean
        get() = KeyEvent.VK_SPACE in pressedKeys || KeyEvent.VK_UP in pressedKeys || KeyEvent.VK_W in pressedKeys
    
    /**
     * Creates and returns a GameInput object representing the current input state.
     *
     * @return GameInput containing the current state of movement and jump inputs
     */
    fun getCurrentInput(): GameInput {
        return GameInput(
            isLeftPressed = isLeftPressed,
            isRightPressed = isRightPressed,
            isJumpPressed = isJumpPressed
        )
    }
    
    /**
     * Called when a key is pressed. Adds the key code to the pressed keys set.
     *
     * @param e The KeyEvent containing information about the pressed key
     */
    override fun keyPressed(e: KeyEvent) {
        pressedKeys.add(e.keyCode)
    }
    
    /**
     * Called when a key is released. Removes the key code from the pressed keys set.
     *
     * @param e The KeyEvent containing information about the released key
     */
    override fun keyReleased(e: KeyEvent) {
        pressedKeys.remove(e.keyCode)
    }
    
    /**
     * Called when a key is typed. Not used for game input.
     *
     * @param e The KeyEvent containing information about the typed key
     */
    override fun keyTyped(e: KeyEvent) {
        // Not used for game input
    }
    
    /**
     * Checks if a specific key code is currently pressed.
     *
     * @param keyCode The key code to check
     * @return true if the key is currently pressed, false otherwise
     */
    fun isKeyPressed(keyCode: Int): Boolean {
        return keyCode in pressedKeys
    }
    
    /**
     * Resets the input handler by clearing all pressed key states.
     * Useful when restarting the game or handling focus changes.
     */
    fun reset() {
        pressedKeys.clear()
    }
    
    /**
     * Returns the number of keys currently pressed.
     *
     * @return The count of pressed keys
     */
    fun getPressedKeysCount(): Int = pressedKeys.size
    
    /**
     * Returns a copy of the set containing all currently pressed key codes.
     *
     * @return A new Set containing the pressed key codes
     */
    fun getPressedKeys(): Set<Int> = pressedKeys.toSet()
}