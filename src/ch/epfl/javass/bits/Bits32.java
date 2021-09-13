package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * 
 * The class Bits32 is going to be used to make different modifications on integer
 * 
 */
public final class Bits32 {

    /**
     * Private default constructor of the class Bits32
     */
    private Bits32() { };


    /**
     * Return a int which have, in its binary representation, ones only at the index we choose
     * @param start(int): The index of the less significant bit we want to be a 1
     * @param size (int): The number of bit we want to be ones starting from the index start
     * @throws IllegalArgumentException: if the start index is negative or bigger than the size of an integer (32)
     * @throws IllegalArgumentException: if the size is negative or bigger than the size of an integer (32)
     * @throws IllegalArgumentException: if size+start is negative or bigger than the size of an integer (32)
     * @return mask (int): The int which have, in its binary representation, ones only at the index we choose 
     */
    public static int mask(int start, int size) {

        Preconditions.checkArgument(start>=0 && start<=Integer.SIZE );

        Preconditions.checkArgument(size>=0);

        Preconditions.checkArgument(start+size>=0 && start+size<=Integer.SIZE );

        if (size==0) {
            return 0;
        }
        int mask = 0;

        for (int i=0; i<size; ++i) {
            int temp=1 << (start+i);
            mask = mask | temp;     
        }
        return mask;   
    }

    /**
     * Return a value where the "size" least significant bits are equal to the bits of the given int starting 
     * at the bit of the given index to the bit of index start+size (excluded)
     * @param bits (int): the integer that is going to be used to extract bits
     * @param start (int): the index of the first bit we want to extract
     * @param size (int): the number of bits we want to extract
     * @throws IllegalArgumentException: if the start index is negative or bigger than the size of an integer (32)
     * @throws IllegalArgumentException: if the size is negative or bigger than the size of an integer (32)
     * @throws IllegalArgumentException: if size+start is negative or bigger than the size of an integer (32)
     * @return extracted (int): the integer where the "size" least significant bits are equal to the bits of the given int starting 
     * at the bit of the given index to the bit of index start+size
     */
    public static int extract(int bits, int start, int size) {

        Preconditions.checkArgument(start>=0 && start<=Integer.SIZE );

        Preconditions.checkArgument(start+size>=0 && start+size<=Integer.SIZE );

        Preconditions.checkArgument(size>=0);

        if (start==32) {
            return 0;
        }
        int mask= mask(start, size);
        int extracted= bits & mask;
        extracted = extracted>>>(start);
        return extracted;
    }


    /**
     * Take 2 integer and pack them in one integer by putting them (in their binary representation) 
     * side by side
     * @param v1 (int): the first integer we want to pack, the one that is going to take the least significant bits
     * @param s1 (int): the number of bits we want the first integer to be packed in
     * @param v2 (int): the second integer we want to pack, the one that is going to take the bits following the ones taken by the first integer
     * @param s2 (int): the number of bits we want the second integer to be packed in
     * @throws IllegalArgumentException: if the sum of the given sizes is bigger than the size of an integer (32)
     * @throws IllegalArgumentException: if one of the given sizes  is smaller than 1 or bigger than the size of an integer (32)
     * @throws IllegalArgumentException: if one of the number to be packed needs more bits to be represented than the wanted size
     * @return (int): the integer produced by the packing of the two integer
     */
    public static int pack(int v1, int s1, int v2, int s2) {
        checkPackValue(v1, s1);
        checkPackValue(v2, s2);

        Preconditions.checkArgument((s1+s2)<=Integer.SIZE);

        int temp= v2<<s1;
        int packedInt = v1 | temp;
        return packedInt;
    }

    /**
     * Overloading of the method pack with 3 integer
     * 
     * @param v1 (int): the first integer we want to pack, the one that is going to take the least significant bits
     * @param s1 (int): the number of bits we want the first integer to be packed in
     * @param v2 (int): the second integer we want to pack, the one that is going to take the bits following the ones taken by the first integer
     * @param s2 (int): the number of bits we want the second integer to be packed in
     * @param v3 (int): the third integer we want to pack
     * @param s3 (int): the number of bits we want the third integer to be packed in
     * @throws IllegalArgumentException: if the sum of the given sizes is bigger than the size of an integer (32)
     * @throws IllegalArgumentException: if one of the given sizes  is smaller than 1 or bigger than the size of an integer (32)
     * @throws IllegalArgumentException: if one of the number to be packed needs more bits to be represented than the wanted size
     * @return (int): the integer produced by the packing of the three integer
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3) {
        checkPackValue(v1, s1);
        checkPackValue(v2, s2);
        checkPackValue(v3, s3);

        Preconditions.checkArgument((s1+s2+s3)<=Integer.SIZE);

        v2=v2<<s1;

        v3=v3<<(s1+s2);

        int packedInt = v1|v2|v3;

        return packedInt;
    }


    /**
     * Overloading of the method pack with 7 integer
     * 
     * @param v1 (int): the first integer we want to pack, the one that is going to take the least significant bits
     * @param s1 (int): the number of bits we want the first integer to be packed in
     * @param v2 (int): the second integer we want to pack, the one that is going to take the bits following the ones taken by the first integer
     * @param s2 (int): the number of bits we want the second integer to be packed in
     * @param v3 (int): the third integer we want to pack
     * @param s3 (int): the number of bits we want the third integer to be packed in
     * @param v4 (int): the fourth integer we want to pack
     * @param s4 (int): the number of bits we want the fourth integer to be packed in
     * @param v5 (int): the fith integer we want to pack
     * @param s5 (int): the number of bits we want the fith integer to be packed in
     * @param v6 (int): the sixth integer we want to pack
     * @param s6 (int): the number of bits we want the sixth integer to be packed in
     * @param v7 (int): the seventh integer we want to pack
     * @param s7 (int): the number of bits we want the seventh integer to be packed in
     * @throws IllegalArgumentException: if the sum of the given sizes is bigger than the size of an integer (32)
     * @throws IllegalArgumentException: if one of the given sizes  is smaller than 1 or bigger than the size of an integer (32)
     * @throws IllegalArgumentException: if one of the number to be packed needs more bits to be represented than the wanted size
     * @return (int): the integer produced by the packing of the seven integer
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3, int v4, int s4, int v5, int s5, int v6, int s6, int v7,int s7) {
        checkPackValue(v1, s1);
        checkPackValue(v2, s2);
        checkPackValue(v3, s3);
        checkPackValue(v4, s4);
        checkPackValue(v5, s5);
        checkPackValue(v6, s6);
        checkPackValue(v7, s7);

        Preconditions.checkArgument((s1+s2+s3+s4+s5+s6+s7)<=Integer.SIZE);

        v2=v2<<s1;

        v3=v3<<(s1+s2);

        v4 = v4<<(s1+s2+s3);

        v5= v5<< (s1+s2+s3+s4);

        v6= v6<< (s1+s2+s3+s4+s5);

        v7= v7<< (s1+s2+s3+s4+s5+s6);

        int packedInt = v1|v2|v3|v4|v5|v6|v7;

        return packedInt;
    }

    /**
     * A method that check if the value and the size wanted are valid
     * @param v (int): the value to pack
     * @param s (int): the number of bits we want the value to be packed in
     * @throws IllegalArgumentException: if the given sizes  is smaller than 1 or is equals or bigger than or the size of an integer (32)
     * @throws IllegalArgumentException: if the number to be packed needs more bits to be represented than the wanted size
     */
    private static void checkPackValue(int v, int s){

        Preconditions.checkArgument(s>=1 && s<Integer.SIZE);

        double maxValueWithSBits = Math.pow(2, s);

        Preconditions.checkArgument(v<maxValueWithSBits);
    }

}
