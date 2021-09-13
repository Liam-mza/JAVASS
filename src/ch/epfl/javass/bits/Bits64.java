package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

public final class Bits64 {

    private Bits64() {};


    /**
     * Return a long which have, in its binary representation, ones only at the index we choose
     * @param start(int): The index of the less significant bit we want to be a 1
     * @param size (int): The number of bit we want to be ones starting from the index start
     * @throws IllegalArgumentException: if the start index is negative or bigger than the size of a long (64)
     * @throws IllegalArgumentException: if the size is negative or bigger than the size of a long (64)
     * @throws IllegalArgumentException: if size+start is negative or bigger than the size of a long (64)
     * @return mask (long): The long which have, in its binary representation, ones only at the index we choose 
     */
    public static long mask(int start, int size) {

        Preconditions.checkArgument(start>=0 && start<=Long.SIZE );

        Preconditions.checkArgument(size>=0);

        Preconditions.checkArgument(start+size>=0 && start+size<=Long.SIZE );

        if (size==0) {
            return 0;
        }

        long mask = 0L;

        for (int i=0; i<size; ++i) {
            long temp=1L << (start+i);
            mask = mask | temp;     
        }

        return mask;
    }


    /**
     * Return a value where the "size" least significant bits are equal to the bits of the given long starting 
     * at the bit of the given index to the bit of index start+size (excluded)
     * @param bits (long): the long that is going to be used to extract bits
     * @param start (int): the index of the first bit we want to extract
     * @param size (int): the number of bits we want to extract
     * @throws IllegalArgumentException: if the start index is negative or bigger than the size of a long (64)
     * @throws IllegalArgumentException: if the size is negative or bigger than the size of a long (64)
     * @throws IllegalArgumentException: if size+start is negative or bigger than the size of a long (64)
     * @return extracted (long): the long where the "size" least significant bits are equal to the bits of the given long starting 
     * at the bit of the given index to the bit of index start+size
     */
    public static long extract(long bits, int start, int size) {

        Preconditions.checkArgument(start>=0 && start<=Long.SIZE );

        Preconditions.checkArgument(start+size>=0 && start+size<=Long.SIZE  );

        Preconditions.checkArgument(size>=0);

        if (start==64) {
            return 0L;
        }

        long mask= mask(start, size);
        long extracted= bits & mask;
        extracted = extracted>>>(start);
        return extracted;
    }

    /**
     * Take 2 long and pack them in one long by putting them (in their binary representation) side by side
     * @param v1 (long): the first long we want to pack, the one that is going to take the least significant bits
     * @param s1 (int): the number of bits we want the first long to be packed in
     * @param v2 (long): the second long we want to pack, the one that is going to take the bits following the ones taken by the first long
     * @param s2 (int): the number of bits we want the second long to be packed in
     * @throws IllegalArgumentException: if the sum of the given sizes is bigger than the size of a long (64)
     * @throws IllegalArgumentException: if one of the given sizes  is smaller than 1 or bigger than the size of a long (64)
     * @throws IllegalArgumentException: if one of the number to be packed needs more bits to be represented than the wanted size
     * @return (long): the long produced by the packing of the two long
     */
    public static long pack(long v1, int s1, long v2, int s2) {
        checkPackValue(v1, s1);
        checkPackValue(v2, s2);

        Preconditions.checkArgument((s1+s2)<=Long.SIZE);

        long temp= v2<<s1;
        long packedInt = v1 | temp;
        return packedInt;
    }

    /**
     * A method that check if the value and the size wanted are valid
     * @param v (long): the value to pack
     * @param s (int): the number of bits we want the value to be packed in
     * @throws IllegalArgumentException: if the given sizes  is smaller than 1 or is equals or bigger than or the size of a long (64)
     * @throws IllegalArgumentException: if the number to be packed needs more bits to be represented than the wanted size
     */
    private static void checkPackValue(long v, int s){

        Preconditions.checkArgument(s>=1 && s<Long.SIZE);

        double maxValueWithSBits = Math.pow(2, s);

        Preconditions.checkArgument(v<maxValueWithSBits);
    }

}
