package com.android.settings.gestures;

/**
 * Created by sahaj on 11/4/15.
 */
import java.util.HashMap;
import android.content.Context;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.Serializable;


class Event implements Serializable {
    private HashMap<Integer,Event> eventMap;
    private int action;

    public Event(int action){
        eventMap = new HashMap<Integer,Event>();
        this.action = action;
    }

    public void storeEvent(int action, Event event){
        eventMap.put(action,event);
    }

    public Event retrieveEvent(int action){
        return eventMap.get(action);
    }

    public HashMap<Integer,Event> getMap(){
        return eventMap;
    }

    public int getAction(){
        return action;
    }
}
public class GestureRecorder{
    private Event rootEvent = null;
    private static final String FILE_NAME = "Gesture.obj";
    private Context context;

    HashMap<Integer,String> eventDesc = new HashMap<Integer,String>();


    public GestureRecorder(Context context){
        if(rootEvent == null){
            rootEvent = retrieveEventFromFile(context);
        }
        if(rootEvent == null){
            rootEvent = new Event(-1);
        }
        eventDesc.put(0,"Action Down");
        eventDesc.put(1,"Action Up");
        eventDesc.put(2,"Move");
        eventDesc.put(5,"Action Pointer Down");
        eventDesc.put(6,"Action Pointer Up");
        eventDesc.put(-1,"Gesture End");
    }

    public Event storeEvent(Event currentEvent,int action,boolean isFinalAction,Context context){
//        Log.d("Presentation", "GestureRecorder : storeEvent");
        Event newEvent = null;

        if(currentEvent == null){
            //Log.d("Presentation", "GestureRecorder : storeEvent : currentEvent is null ");

            if(rootEvent.retrieveEvent(action) == null) {
                if(action != 2) {
                    newEvent = new Event(action);
                    rootEvent.storeEvent(action,newEvent);
                    if(isFinalAction){
                        newEvent = new Event(-1);
                        currentEvent.storeEvent(-1, newEvent);
                    }
                }
            }else{
                return rootEvent.retrieveEvent(action);
            }

        }else{
            //Log.d("Presentation", "GestureRecorder : storeEvent : currentEvent is not null. Storing in "+action);
            if(currentEvent.retrieveEvent(action) == null) {
                if(action != 2) {
                    newEvent = new Event(action);
                    currentEvent.storeEvent(action, newEvent);
                    if(isFinalAction){
                        newEvent = new Event(-1);
                        currentEvent.storeEvent(-1, newEvent);
                    }
                    Log.d("Presentation", "Stored event: " + eventDesc.get(action));
                }else{
                    newEvent = currentEvent;
                }
            }else{
                return currentEvent.retrieveEvent(action);
            }
        }
        if(isFinalAction){
  //          Log.d("Presentation", "GestureRecorder : storeEvent final action");

            storeEventToFile(context);
        }

        return newEvent;
    }

    public Event retrieveEvent(Event currentEvent,int action){
        if(currentEvent == null){
            return rootEvent.retrieveEvent(action);
        }else{
            return currentEvent.retrieveEvent(action);
        }
    }

    public void storeEventToFile(Context context){
    //    Log.d("Presentation", "GestureRecorder : storeEventToFile");
        FileOutputStream outputStream;
        ObjectOutputStream oos;

        try {
            outputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(outputStream);
            oos.writeObject(rootEvent);
            oos.close();
            outputStream.close();
     //       Log.d("Presentation", "GestureRecorder : storeEventToFile finished");
        } catch (Exception e) {
            e.printStackTrace();
        }
     //   Log.d("Presentation", "GestureRecorder : storeEventToFile printing fle contents");
        try{
            FileInputStream inputStream = context.openFileInput(FILE_NAME);
            ObjectInputStream is = new ObjectInputStream(inputStream);
            Event event = retrieveEventFromFile(context);

            Log.d("Presentation", "------------printing---------------");

            printEvents(event);
            /*for(Integer key : event.getMap().keySet()){
                Log.d("Presentation", "GestureRecorder : " + key);
             //   printEvents(event.getMap().get(key));
            }*/
            is.close();
            inputStream.close();
      //      Log.d("Presentation", "GestureRecorder : retrieveEventFromFile finished");
        }catch(FileNotFoundException fe){
            fe.printStackTrace();
        }catch(IOException ie){
            ie.printStackTrace();
        }
    }

    private void printEvents(Event event){

     //   Log.d("Presentation", "printEvents entry ");

        if(event == null){
            return;
        }

        if(event.getMap() == null){
            return;
        }

        for(Integer key : event.getMap().keySet()){
            Log.d("Presentation", "GestureRecorder : Retrieved:  "+eventDesc.get(key));
            printEvents(event.getMap().get(key));
        }
    }

    public Event retrieveEventFromFile(Context context){
        //Log.d("Presentation", "GestureRecorder : retrieveEventFromFile");
        Event event = null;
        try{
            FileInputStream inputStream = context.openFileInput(FILE_NAME);
            ObjectInputStream is = new ObjectInputStream(inputStream);
            event = (Event) is.readObject();
            is.close();
            inputStream.close();
         //   Log.d("Presentation", "GestureRecorder : retrieveEventFromFile finished");
        }catch(FileNotFoundException fe){
            fe.printStackTrace();
        }catch(IOException ie){
            ie.printStackTrace();
        }catch(ClassNotFoundException ce){
            ce.printStackTrace();
        }
        return event;
    }
}
