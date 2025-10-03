package de.mangole.trolling.utils;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ChallengeEvent {
    boolean ignoreLobby() default true;
}
