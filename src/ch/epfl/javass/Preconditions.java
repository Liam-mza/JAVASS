package ch.epfl.javass;

/**
 * Class which verifies certain conditions, and have to be verified before
 * starting any procedure
 */
public final class Preconditions {

    /**
     * Private constructor to make impossible the creation of an object of type
     * Preconditions
     *
     */
    private Preconditions() {};

    /**
     * Throws an IllegalArgumentException if its parameter b is false, and does nothing
     * otherwise
     * @param b (boolean): the condition to check
     * @throws IllegalArgumentException: if the given boolean is false 
     */
    public static void checkArgument(boolean b) {
        if (!b) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Throws an IndexOutOfBoundsException if the given index is negative or
     * superior or equal to the given size
     * @param index (int): the index to check
     * @param size (int): the size the index need to be in
     * @throws IndexOutOfBoundsException: if the given index is negative or bigger than (or equals to) the given size
     * @return (int): the index itself if it is valid
     */
    public static int checkIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(" "+index);
        } else {
            return index;
        }
    }

}
