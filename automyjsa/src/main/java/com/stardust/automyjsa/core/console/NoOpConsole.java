package com.stardust.automyjsa.core.console;

import androidx.annotation.Nullable;

import com.stardust.automyjsa.runtime.api.Console;

/**
 * Created by Stardust on 2017/9/21.
 */

public class NoOpConsole implements Console {
    @Override
    public void verbose(@Nullable Object data, Object... options) {

    }

    @Override
    public void log(@Nullable Object data, Object... options) {

    }

    @Override
    public void print(int level, Object data, Object... options) {

    }

    @Override
    public void info(@Nullable Object data, Object... options) {

    }

    @Override
    public void warn(@Nullable Object data, Object... options) {

    }

    @Override
    public void error(@Nullable Object data, Object... options) {

    }

    @Override
    public void assertTrue(boolean value, @Nullable Object data, Object... options) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void show(boolean isAutoHide) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public boolean isAutoHide() {
        return false;
    }

    @Override
    public void setMaxLines(int maxLines) {

    }

    @Override
    public String println(int level, CharSequence charSequence) {

        return null;
    }

    @Override
    public void setTitle(CharSequence title,String color,int size) {

    }

    @Override
    public void setBackground(@Nullable String color) {

    }

    @Override
    public void setLogSize(int size) {


    }

    @Override
    public void setCanInput(boolean can) {

    }

}
