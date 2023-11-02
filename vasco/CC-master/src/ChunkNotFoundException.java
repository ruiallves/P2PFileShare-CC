public class ChunkNotFoundException extends Exception{
    // Parameterless Constructor
    public ChunkNotFoundException() {}

    // Constructor that accepts a message
    public ChunkNotFoundException(String message)
    {
        super(message);
    }
}
