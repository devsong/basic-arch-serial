package io.github.devsong.serial.api.controller;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * date:  2023/4/8
 * author:guanzhisong
 */
public class NotMatcher extends BaseMatcher<Integer> {
    int code;

    public NotMatcher(int code) {
        this.code = code;
    }

    @Override
    public boolean matches(Object actual) {
        int code = (Integer) actual;
        return code != this.code;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("not " + this.code);
    }
}
