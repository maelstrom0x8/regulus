package io.ceze.regulus.generator;

public class DuplicateRequestException extends RuntimeException {

    public DuplicateRequestException() {
        super();
    }

    public DuplicateRequestException(String msg) {
        super(msg);
    }
}
