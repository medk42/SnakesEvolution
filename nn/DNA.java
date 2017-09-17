package nn;

/**
 * Class which handles the DNA of some creature. It takes care of creating 
 * random DNA, mixing with another DNA and mutating.
 * 
 * @author Jakub Medek
 */
public class DNA {
    public byte[] genes;

    /**
     * Creates a DNA with random genes.
     * @param genesCount Number of genes to use.
     */
    public DNA(int genesCount) {
        genes = new byte[genesCount];
        for (int i = 0; i < genes.length; i++) genes[i] = (byte) (Math.random()*255-128);
    }

    /**
     * Creates a DNA with predefined genes.
     * @param genes Genes to use.
     */
    public DNA(byte[] genes) {
        this.genes = genes;
    }

    /**
     * Making a genes crossover (mixing it with another DNA) based on 
     * random pivot.
     * @param partner Another DNA to mix with.
     * @return Returns a new DNA which is a result of the crossover.
     */
    public DNA crossover(DNA partner) {
        boolean[] childGensBinary = new boolean[genes.length*8];
        boolean[] thisGenesBinary = bytesToBinaryArray(this.genes);
        boolean[] partersGenesBinary = bytesToBinaryArray(partner.genes);

        boolean parentIsThis = Math.random()<0.5;
        for (int i = 0; i < childGensBinary.length; i++) {
            if (parentIsThis) {
                childGensBinary[i] = thisGenesBinary[i];
            } else {
                childGensBinary[i] = partersGenesBinary[i];
            }
            if (Math.random()<0.01) parentIsThis = !parentIsThis;
        }

        byte[] childGens = binaryToByteArray(childGensBinary);

        return new DNA(childGens);
    }

    /**
     * Converts an array of bytes to 8 times larger array of boolean.
     * @param bytesToConvert Array of bytes to convert.
     * @return Boolean array, which is binary representation of byte array 
     * on input.
     */
    private boolean[] bytesToBinaryArray(byte[] bytesToConvert) {
        boolean[] binaryArray = new boolean[bytesToConvert.length*8];

        for (int i = 0; i < bytesToConvert.length; i++) {
            byte byteToConvert = bytesToConvert[i];
            binaryArray[7+i*8] = ((byteToConvert & 0x01) != 0);
            binaryArray[6+i*8] = ((byteToConvert & 0x02) != 0);
            binaryArray[5+i*8] = ((byteToConvert & 0x04) != 0);
            binaryArray[4+i*8] = ((byteToConvert & 0x08) != 0);
            binaryArray[3+i*8] = ((byteToConvert & 0x10) != 0);
            binaryArray[2+i*8] = ((byteToConvert & 0x20) != 0);
            binaryArray[1+i*8] = ((byteToConvert & 0x40) != 0);
            binaryArray[0+i*8] = ((byteToConvert & 0x80) != 0);
        }


        return binaryArray;
    }

    /**
     * Converts an array of boolean to 8 times smaller array of bytes.
     * @param binaryToConvert Array of boolean values to convert.
     * @return Byte array, which is byte representation of boolean array 
     * on input.
     */
    private byte[] binaryToByteArray(boolean[] binaryToConvert) {
        byte[] byteArray = new byte[binaryToConvert.length/8];

        for (int i = 0; i < byteArray.length; i++) {
            byte value = 0;
            for (int j = 0; j < 8; j++) {
                value*=2;
                value+=binaryToConvert[i*8+j]?1:0;
            }
            byteArray[i] = value;
        }

        return byteArray;
    }

    /**
     * Mutating genes of this DNA. Random mutation based on mutation rate.
     * @param mutationRate Mutation rate for random mutation.
     */
    public void mutate(float mutationRate) {
        boolean[] genesBinary = bytesToBinaryArray(genes);
        for (int i = 0; i < genesBinary.length; i++) {
            if (Math.random() < mutationRate) {
                genesBinary[i] = !genesBinary[i];
            }
        }
        genes = binaryToByteArray(genesBinary);
    }
}
