package com.dy.app.core.thread;

import com.dy.app.gameplay.player.Player;

public class Timer extends Thread{
    private long duration;
    private boolean isRunning = false;
    private long delay = 100;
    private OnTimerTickListener listener;
    private boolean isPaused = false;
    final Object lock = new Object();

    public interface OnTimerTickListener{
        void onTimeUpdate(long timeRemaining);
        void onTimeOut();
    }

    public Timer(long duration, OnTimerTickListener listener){
        this.duration = duration;
        this.listener = listener;
    }

    @Override
    public void run() {
        isRunning = true;
        //wait for the first time
        isPaused = true;

        while(isRunning){
            while(!isPaused){
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                duration -= delay;
                listener.onTimeUpdate(duration);

                if(duration <= 0){
                    listener.onTimeOut();
                    pauseTimer();
                }
            }

            synchronized (lock){
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public void stopTimer(){
        isRunning = false;
        isPaused = true;

        synchronized (lock){
            lock.notify();
        }
    }

    public void pauseTimer(){
        isPaused = true;
    }

    public void resumeTimer(){
        isPaused = false;
        synchronized (lock){
            lock.notify();
        }
    }
}
