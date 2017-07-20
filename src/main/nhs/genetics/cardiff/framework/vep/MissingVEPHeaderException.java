package nhs.genetics.cardiff.framework.vep;

/**
 * Exception for missing VEP annotations
 *
 * @author  Matt Lyon
 * @since   2017-06-12
 */

public class MissingVEPHeaderException extends RuntimeException {
    public MissingVEPHeaderException(String message) {
        super(message);
    }
}