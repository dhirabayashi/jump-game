package com.jumpgame.util

import kotlin.math.sqrt

/**
 * A 2D vector representation with double precision coordinates.
 *
 * This data class provides basic vector operations for 2D space calculations
 * including addition, subtraction, scalar multiplication, magnitude calculation, and normalization.
 *
 * @property x The x-coordinate component of the vector
 * @property y The y-coordinate component of the vector
 */
data class Vector2D(val x: Double, val y: Double) {
    
    /**
     * Secondary constructor that accepts integer coordinates and converts them to double.
     *
     * @param x The x-coordinate as an integer
     * @param y The y-coordinate as an integer
     */
    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())
    
    /**
     * Adds another vector to this vector and returns the result as a new vector.
     *
     * @param other The vector to add to this vector
     * @return A new Vector2D representing the sum of the two vectors
     */
    fun add(other: Vector2D): Vector2D = Vector2D(x + other.x, y + other.y)
    
    /**
     * Subtracts another vector from this vector and returns the result as a new vector.
     *
     * @param other The vector to subtract from this vector
     * @return A new Vector2D representing the difference of the two vectors
     */
    fun subtract(other: Vector2D): Vector2D = Vector2D(x - other.x, y - other.y)
    
    /**
     * Multiplies this vector by a scalar value and returns the result as a new vector.
     *
     * @param scalar The scalar value to multiply the vector by
     * @return A new Vector2D representing the scaled vector
     */
    fun multiply(scalar: Double): Vector2D = Vector2D(x * scalar, y * scalar)
    
    /**
     * Calculates the magnitude (length) of this vector.
     *
     * @return The magnitude of the vector as a double value
     */
    fun magnitude(): Double = sqrt(x * x + y * y)
    
    /**
     * Returns a normalized version of this vector (unit vector with magnitude 1).
     *
     * If the vector has zero magnitude, returns a zero vector instead of dividing by zero.
     *
     * @return A new Vector2D representing the normalized vector, or zero vector if magnitude is 0
     */
    fun normalize(): Vector2D {
        val mag = magnitude()
        return if (mag == 0.0) Vector2D(0.0, 0.0) else Vector2D(x / mag, y / mag)
    }
    
    companion object {
        /** A zero vector with coordinates (0.0, 0.0) */
        val ZERO = Vector2D(0.0, 0.0)
        
        /** An upward unit vector with coordinates (0.0, -1.0) */
        val UP = Vector2D(0.0, -1.0)
        
        /** A downward unit vector with coordinates (0.0, 1.0) */
        val DOWN = Vector2D(0.0, 1.0)
        
        /** A leftward unit vector with coordinates (-1.0, 0.0) */
        val LEFT = Vector2D(-1.0, 0.0)
        
        /** A rightward unit vector with coordinates (1.0, 0.0) */
        val RIGHT = Vector2D(1.0, 0.0)
    }
}