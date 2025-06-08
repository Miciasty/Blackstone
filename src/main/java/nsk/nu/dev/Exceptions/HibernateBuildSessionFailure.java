package nsk.nu.dev.Exceptions;

public class HibernateBuildSessionFailure extends RuntimeException {

    public HibernateBuildSessionFailure(String message) {
        super(message);
    }

    public HibernateBuildSessionFailure(String message, Exception e) {
        super(message);
    }
}
