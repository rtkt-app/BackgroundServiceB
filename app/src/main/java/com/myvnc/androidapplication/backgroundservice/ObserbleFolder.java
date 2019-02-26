package com.myvnc.androidapplication.backgroundservice;

import android.content.Context;
import android.os.FileObserver;
import android.util.Log;
import android.widget.Toast;

public class ObserbleFolder extends FileObserver {

    public ObserbleFolder(String path) {
        super(path);
    }

    @Override
    public void startWatching() {
        super.startWatching();
        Log.d("Observer Event", "Start watching");
    }

    // 監視を終了したいときは、このメソッドを呼ぶ
    @Override
    public void stopWatching() {
        super.stopWatching();
        Log.d("Observer Event", "Stop watching");
    }

    // ディレクトリ内で、変化が起きたら呼ばれるイベント
    @Override
    public void onEvent(int event, String path) {
        switch (event) {
            case FileObserver.ACCESS:
                Log.d("Observer Event", "ACCESS");
                break;
            case FileObserver.ALL_EVENTS:
                Log.d("Observer Event", "ALL_EVENTS");
                break;
            case FileObserver.ATTRIB:
                Log.d("Observer Event", "ATTRIB");
                break;
            case FileObserver.CLOSE_NOWRITE:
                Log.d("Observer Event", "NOWRITE");
                break;
            case FileObserver.CLOSE_WRITE:
                Log.d("Observer Event", "WRITE");
                break;
            case FileObserver.CREATE:
                Log.d("Observer Event", "CREATE");
                break;
            case FileObserver.DELETE:
                Log.d("Observer Event", "DELETE");
                break;
            case FileObserver.DELETE_SELF:
                Log.d("Observer Event", "DELETE_SELF");
                break;
            case FileObserver.MODIFY:
                Log.d("Observer Event", "MODIFY");
                break;
            case FileObserver.MOVED_FROM:
                Log.d("Observer Event", "MOVED_FROM");
                break;
            case FileObserver.MOVED_TO:
                Log.d("Observer Event", "MOVED_TO");
                break;
            case FileObserver.MOVE_SELF:
                Log.d("Observer Event", "MOVE_SELF");
                break;
            case FileObserver.OPEN:
                Log.d("Observer Event", "OPEN");
                break;
        }
    }
}