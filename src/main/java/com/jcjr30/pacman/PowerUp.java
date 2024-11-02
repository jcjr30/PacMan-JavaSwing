package com.jcjr30.pacman;

public class PowerUp {

        private boolean isActive;
        private long duration;

        public PowerUp(long duration) {
        this.duration = duration;
    }
        public void activate () {
        isActive = true;
        System.out.println("Power-up activated!");
        new Thread(() -> {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            deactivate();
        }).start();
    }
        public void deactivate () {
        isActive = false;
        System.out.println("Power-up deactivated!");
    }
        public boolean isActive () {
        return isActive;
    }
}