/*
 * Course: CSC-1120A
 * Lab 5 - Mean Image Median Revisited
 * Name: Keagan Weinstock
 * Last Updated: 02/12/2024
 */
package weinstockk;

/**
 * Functional interface for the apply method
 */
@FunctionalInterface
public interface Transform {
    /**
     * Applies a transformation, this is
     * usually for photo change buttons combining a group of pictures
     * @param integers takes in an array of ints
     * @return an int based on the transformation given
     */
    int apply(int[] integers);
}
