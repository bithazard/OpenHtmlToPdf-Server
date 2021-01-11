package bithazard.server.openhtmltopdfserver.document;

public class UnknownExtensionException extends RuntimeException {
    public UnknownExtensionException(String message) {
        super(message);
    }

    public UnknownExtensionException(String message, Throwable cause) {
        super(message, cause);
    }
}
