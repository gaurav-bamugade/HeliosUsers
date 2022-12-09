package com.example.usersidedemoproject.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usersidedemoproject.DBHelper;
import com.example.usersidedemoproject.DBStructure;
import com.example.usersidedemoproject.Model.EventsModelClass;
import com.example.usersidedemoproject.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventRecyclerView extends RecyclerView.Adapter<EventRecyclerView.ViewHolder> {

Context context;
ArrayList<EventsModelClass> arrayList;
DBHelper dbHelper;
    public EventRecyclerView(Context context, ArrayList<EventsModelClass> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_layout,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        EventsModelClass es=arrayList.get(position);
        holder.Event.setText(es.getEVENT());
        holder.DateTxt.setText(es.getDATE());
        holder.Time.setText(es.getTIME());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCalendarEvent(es.getEVENT(),es.getDATE(),es.getTIME());
                arrayList.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });

        if(isAlarmed(es.getDATE(),es.getEVENT(),es.getTIME()))
        {
          //  holder.setAlram.setImageResource(R.drawable.ic_notification_on);

        }
        else
        {
           // holder.setAlram.setImageResource(R.drawable.ic_notification_on);

        }
        Calendar datecalendar=Calendar.getInstance();
        datecalendar.setTime(ConvertStringToDate(es.getDATE()));
        int alarmYear=datecalendar.get(Calendar.YEAR);
        int alarmMonth=datecalendar.get(Calendar.MONTH);
        int alarmDay=datecalendar.get(Calendar.DAY_OF_MONTH);
        Calendar timeCalendar=Calendar.getInstance();
        timeCalendar.setTime(ConvertStringToTime(es.getTIME()));
        int alarmHour=timeCalendar.get(Calendar.HOUR_OF_DAY);
        int alramMinute=timeCalendar.get(Calendar.MINUTE);

      /*  holder.setAlram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAlarmed(es.getDATE(),es.getEVENT(),es.getTIME() ))
                {
                    //holder.setAlram.setImageResource(R.drawable.ic_notification_off);
                   // cancelAlarm(getRequestCode(es.getEVENT(),es.getEVENT(),es.getTIME()));
                    updateEvent(es.getDATE(),es.getEVENT(),es.getTIME(),"off");
                    notifyDataSetChanged();
                }
                else
                {
                 //   holder.setAlram.setImageResource(R.drawable.ic_notification_on);
                    Calendar alarmcalendar=Calendar.getInstance();
                    alarmcalendar.set(alarmYear,alarmMonth,alarmDay,alarmHour,alramMinute);
                    setAlarm(alarmcalendar,es.getEVENT(),es.getTIME(),getRequestCode(es.getDATE(),
                            es.getEVENT(),es.getTIME()));
                    notifyDataSetChanged();
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView DateTxt,Event,Time;
        Button delete;
        ImageButton setAlram;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            DateTxt=itemView.findViewById(R.id.eventdate);
            Event=itemView.findViewById(R.id.eventname);
            Time=itemView.findViewById(R.id.eventtime);
            delete=itemView.findViewById(R.id.delete);
            //setAlram=itemView.findViewById(R.id.alarmedBtn);
        }
    }
    private Date ConvertStringToDate(String eventDate)
    {
        SimpleDateFormat format;
        format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date=null;
        try{
            date = format.parse(eventDate);

        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }
    private Date ConvertStringToTime(String eventDate)
    {
        SimpleDateFormat format;
        format = new SimpleDateFormat("kk:mm", Locale.ENGLISH);
        Date date=null;
        try{
            date = format.parse(eventDate);

        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }
    private void deleteCalendarEvent(String event, String date, String time){
        dbHelper=new DBHelper(context);
        SQLiteDatabase database1=dbHelper.getWritableDatabase();
        dbHelper.deleteEvent(event,date,time,database1);
        dbHelper.close();
    }

    private boolean isAlarmed(String date,String event,String time)
    {
        boolean alarmed=false;
        dbHelper=new DBHelper(context);
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        Cursor cursor=dbHelper.ReadIDEvents(date,event,time,database);
        while (cursor.moveToNext())
        {
            @SuppressLint("Range") String notify=cursor.getString(cursor.getColumnIndex(DBStructure.Notify));
            if(notify.equals("on"))
            {
                alarmed=true;

            }
            else
            {
                alarmed=false;
            }
        }
        cursor.close();
        dbHelper.close();
        return alarmed;
    }
  /*  private void setAlarm(Calendar calendar, String event, String time, int RequestCode)
    {
        Intent i =new Intent(context.getApplicationContext(),AlarmReceiver.class);
        i.putExtra("event", event);
        i.putExtra("time",time);
        i.putExtra("id",RequestCode);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent=PendingIntent.getBroadcast(context,RequestCode,i,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager=(AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }*/
 /*   private void cancelAlarm(int RequestCode)
    {
        Intent i =new Intent(context.getApplicationContext(),AlarmReceiver.class);

        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,RequestCode,i,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager=(AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }*/
    @SuppressLint("Range")
    private int getRequestCode(String date, String event, String time)
    {
        int code=0;
        dbHelper=new DBHelper(context);
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        Cursor cursor=dbHelper.ReadIDEvents(date,event,time,database);
        while (cursor.moveToNext())
        {
            code=cursor.getInt(cursor.getColumnIndex(DBStructure.ID));
        }
        cursor.close();
        dbHelper.close();
        return code;
    }
    private void updateEvent(String date,String event,String time,String notify)
    {
        dbHelper=new DBHelper(context);
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        dbHelper.updateEvent(date,event,time,notify,database);
        dbHelper.close();
    }
}
