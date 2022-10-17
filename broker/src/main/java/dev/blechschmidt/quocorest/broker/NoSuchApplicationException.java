package dev.blechschmidt.quocorest.broker;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NoSuchApplicationException extends RuntimeException {
    static final long serialVersionUID = -6516152229878843038L;
}
