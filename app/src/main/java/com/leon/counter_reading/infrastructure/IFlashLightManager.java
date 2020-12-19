package com.leon.counter_reading.infrastructure;

/**
 * Created by Leon on 2/18/2018.
 */

public interface IFlashLightManager {
    boolean turnOn();

    boolean turnOff();

    boolean toggleFlash();
}
