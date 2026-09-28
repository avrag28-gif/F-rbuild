package com.vedit.app;

import android.app.*;
import android.os.*;
import android.graphics.*;
import android.view.*;
import android.content.*;
import android.widget.*;

public class MainActivity extends Activity {
  @Override public void onCreate(Bundle b) {
    super.onCreate(b);
    getWindow().setStatusBarColor(Color.rgb(8,11,18));
    getWindow().setNavigationBarColor(Color.rgb(8,11,18));
    setContentView(new EditorView(this));
  }
}

class EditorView extends View {
  Paint p=new Paint(3);
  Bitmap preview;
  float sx,sy;
  boolean playing=false;
  int active=0;
  final int bg=Color.rgb(8,11,18), panel=Color.rgb(14,19,31), fg=Color.rgb(236,239,247), muted=Color.rgb(145,151,170), purple=Color.rgb(93,72,255);
  final String[] labels={"Media","Audio","Text","Stickers","Effects","Transitions","Filters","Adjust"};

  EditorView(Context c) {
    super(c);
    preview=BitmapFactory.decodeResource(getResources(),com.vedit.app.R.drawable.vedit_preview);
  }

  float X(float v){return v*sx;} float Y(float v){return v*sy;}
  void box(Canvas c,float l,float t,float r,float b,float rad,int col){
    p.setColor(col);p.setStyle(Paint.Style.FILL);
    c.drawRoundRect(X(l),Y(t),X(r),Y(b),X(rad),X(rad),p);
  }
  void text(Canvas c,String s,float x,float y,float size,int col,boolean bold){
    p.setColor(col);p.setTextSize(X(size));p.setTypeface(Typeface.create("sans",bold?Typeface.BOLD:Typeface.NORMAL));
    c.drawText(s,X(x),Y(y),p);
  }

  @Override protected void onDraw(Canvas c){
    sx=getWidth()/432f; sy=getHeight()/960f; c.drawColor(bg);

    text(c,"V",16,34,25,Color.rgb(123,93,255),true);
    text(c,"VEdit",42,34,20,fg,true);
    text(c,"Project_01",178,34,15,fg,false);
    text(c,"Auto Save",309,32,12,Color.rgb(104,239,195),false);
    text(c,"↶",365,34,23,fg,false);
    box(c,391,12,423,47,10,purple); text(c,"⇧",401,35,18,Color.WHITE,false);

    box(c,0,58,76,960,0,panel);
    for(int i=0;i<8;i++){
      float y=72+i*62;
      if(i==active) box(c,7,y-22,69,y+20,9,purple);
      text(c,labels[i].substring(0,1),20,y+5,19,i==active?Color.WHITE:muted,true);
      text(c,labels[i],37,y+4,9,i==active?Color.WHITE:muted,false);
    }

    box(c,76,58,205,518,0,Color.rgb(11,15,24));
    text(c,"Media",91,89,18,fg,true);
    box(c,90,100,191,137,18,Color.rgb(26,32,48));
    text(c,"Search files...",102,124,12,muted,false);
    String[] cats={"All","Video","Photo","Audio"};
    for(int i=0;i<4;i++) text(c,cats[i],92+i*27,159,10,i==0?Color.WHITE:muted,false);

    for(int i=0;i<6;i++){
      int col=i%2,row=i/2; float x=91+col*58,y=179+row*78;
      box(c,x,y,x+51,y+58,7,Color.rgb(31,38,54));
      if(preview!=null) c.drawBitmap(preview,null,new RectF(X(x),Y(y),X(x+51),Y(y+43)),p);
      text(c,i<5?"clip.mp4":"music.mp3",x,y+70,8,fg,false);
    }

    box(c,205,58,432,518,0,Color.rgb(10,14,23));
    text(c,"Project_01",221,88,15,fg,true);
    box(c,222,103,416,392,4,Color.BLACK);
    if(preview!=null)c.drawBitmap(preview,null,new RectF(X(222),Y(103),X(416),Y(392)),p);
    text(c,"00:06:12 / 00:32:00",222,416,11,muted,false);
    p.setColor(purple);c.drawRect(X(222),Y(425),X(355),Y(428),p);
    text(c,"|◀",248,458,17,fg,false);
    text(c,playing?"❚❚":"▶",313,460,24,Color.WHITE,true);
    text(c,"▶|",357,458,17,fg,false);

    box(c,0,518,432,706,0,bg);
    text(c,"Inspector",15,548,16,fg,true);
    String[] tabs={"Video","Audio","Color","Speed"};
    for(int i=0;i<4;i++) text(c,tabs[i],18+i*62,577,11,i==0?Color.WHITE:muted,false);
    box(c,10,600,422,694,12,panel);
    text(c,"Transform",23,625,13,fg,true);
    text(c,"Scale",23,651,10,muted,false);
    box(c,72,640,300,649,5,Color.rgb(45,51,70));
    box(c,175,640,242,649,5,purple);
    text(c,"100%",383,653,9,fg,false);
    text(c,"Position     X 0      Y 0",23,678,10,fg,false);
    text(c,"Rotation     0°",23,697,10,fg,false);

    box(c,0,706,432,960,0,Color.rgb(10,14,23));
    text(c,"↶",17,735,21,fg,false); text(c,"↷",49,735,21,muted,false);
    text(c,"✂",112,735,18,fg,false); text(c,"⌕",308,735,18,fg,false);
    text(c,"00:00",82,779,8,muted,false); text(c,"00:05",137,779,8,muted,false);
    text(c,"00:10",192,779,8,muted,false); text(c,"00:15",247,779,8,muted,false);
    String[] tracks={"Video 1","Audio 1","Text 1","Stickers 1"};
    for(int i=0;i<4;i++){
      float y=795+i*38;text(c,tracks[i],8,y+22,10,fg,false);
      box(c,80,y,420,y+30,6,Color.rgb(24,30,46));
      if(i==0){box(c,83,y+2,200,y+28,4,purple);box(c,201,y+2,300,y+28,4,Color.rgb(54,45,125));box(c,301,y+2,417,y+28,4,purple);}
      if(i==1)box(c,83,y+3,417,y+27,4,Color.rgb(83,55,200));
      if(i==2){box(c,170,y+3,290,y+27,4,Color.rgb(75,55,160));text(c,"T  Better Every Day",180,y+22,10,fg,false);}
    }
    p.setColor(purple);c.drawRect(X(205),Y(785),X(207),Y(946),p);
  }

  @Override public boolean onTouchEvent(MotionEvent e){
    if(e.getAction()!=MotionEvent.ACTION_UP)return true;
    float x=e.getX()/sx,y=e.getY()/sy;
    if(y<58 && x>385) Toast.makeText(getContext(),"Export siap dihubungkan ke encoder video.",Toast.LENGTH_SHORT).show();
    else if(x<76 && y>60 && y<570){active=Math.max(0,Math.min(7,(int)((y-50)/62)));invalidate();}
    else if(x>280 && y>425 && y<485){playing=!playing;invalidate();}
    return true;
  }
}
