package com.dy.app.core;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.dy.app.activity.GameActivity;

import java.util.Vector;

public class TaskManager{
    private GameActivity activity;
    private boolean isSet = false;
    private boolean isRunning = false;
    private Vector<Runnable> tasks;
    private final String waitMessage = "Please wait...";
    public Vector<String> updateMsg;

    public static final String TAG = "TaskManager";

    public enum TaskType{
        INIT,
        START,
        UPDATE,
        END,
        SHOW_DIALOG,
        DISMISS_DIALOG,
    }

    public void addTask(Runnable task, String message) {
        tasks.add(task);
        updateMsg.add(message);
        if(!isRunning){
            runTask(tasks.get(0));
        }
    }
    private void runTask(Runnable task){
        new TaskWorker(task).execute();
    }


    public static TaskManager getInstance() {
        return instance = (instance == null) ? new TaskManager() : instance;
    }

    private TaskManager() {
        tasks = new Vector<Runnable>();
        updateMsg = new Vector<String>();
    }

    private static TaskManager instance = null;

    public void setActivity(GameActivity activity) {
        if(isSet) return;
        isSet = true;

        this.activity = activity;
    }

    private void openDialog() {
        activity.onMsgToMain(TAG, -1, TaskType.SHOW_DIALOG, null);
    }

    public void updateMessage(String message) {
        activity.onMsgToMain(TAG, -1, TaskType.UPDATE, message);
    }

    private void closeDialog() {
        activity.onMsgToMain(TAG, -1,TaskType.DISMISS_DIALOG, null);
    }

    private class TaskWorker extends AsyncTask<Void, String, Void>{
        Runnable task;

        public TaskWorker(Runnable task) {
            this.task = task;
        }

        @Override
        protected void onPreExecute() {
            if(!isRunning)
                openDialog();
            updateMessage(waitMessage + updateMsg.get(0));
            isRunning = true;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            task.run();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            tasks.remove(task);
            updateMsg.remove(0);
            if(tasks.size() > 0){
                runTask(tasks.get(0));
            } else {
                isRunning = false;
                closeDialog();
            }
        }
    }
}
