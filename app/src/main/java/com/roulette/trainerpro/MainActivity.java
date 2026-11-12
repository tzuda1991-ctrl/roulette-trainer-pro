package com.roulette.trainerpro;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.MotionEvent;
import android.graphics.*;
import java.util.*;

public class MainActivity extends Activity {
  @Override public void onCreate(Bundle b) { super.onCreate(b); getWindow().getDecorView().setSystemUiVisibility(5894|1024|512); setContentView(new Table()); }
  class Table extends View {
    final Paint p=new Paint(3); final Random rng=new Random();
    final int gold=Color.rgb(218,175,74), green=Color.rgb(10,76,54), red=Color.rgb(180,35,38);
    final int[] order={0,32,15,19,4,21,2,25,17,34,6,27,13,36,11,30,8,23,10,5,24,16,33,1,20,14,31,9,22,18,29,7,28,12,35,3,26};
    final Set<Integer> reds=new HashSet<>(Arrays.asList(1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36));
    int balance=1000,stake=10,selection=-2,last=-1; boolean spinning=false; long start=0; float angle=0;
    Table(){super(MainActivity.this);setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
    void fill(Canvas c,int color,float l,float t,float r,float b,float radius){p.setColor(color);p.setStyle(Paint.Style.FILL);c.drawRoundRect(l,t,r,b,radius,radius,p);}
    void text(Canvas c,String s,float x,float y,float size,int color){p.setColor(color);p.setTextSize(size);p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));p.setTextAlign(Paint.Align.CENTER);p.setStyle(Paint.Style.FILL);c.drawText(s,x,y,p);}
    void circle(Canvas c,int color,float x,float y,float r){p.setColor(color);p.setStyle(Paint.Style.FILL);c.drawCircle(x,y,r,p);}
    @Override protected void onDraw(Canvas canvas){
      super.onDraw(canvas);float w=getWidth(),h=getHeight();canvas.save();canvas.scale(w/1000f,h/560f);
      fill(canvas,Color.rgb(40,24,17),0,0,1000,560,18);fill(canvas,gold,9,9,991,551,24);fill(canvas,green,17,17,983,543,19);
      text(canvas,"ROULETTE TRAINER PRO",500,47,27,gold);
      text(canvas,"VIRTUAL BALANCE  "+balance+"   |   BET  "+stake+"   |   LAST  "+(last<0?"--":last),500,80,20,Color.WHITE);
      float cx=252,cy=294,r=176;circle(canvas,Color.rgb(73,43,22),cx,cy,r+18);circle(canvas,gold,cx,cy,r+9);circle(canvas,Color.rgb(27,27,27),cx,cy,r);
      if(spinning){long elapsed=System.currentTimeMillis()-start;angle+=Math.max(.025f,.32f*(1-elapsed/3500f));if(elapsed>=3500){spinning=false;last=rng.nextInt(37);balance+=win(last)?stake:-stake;}}
      for(int i=0;i<37;i++){float a=(float)(2*Math.PI*i/37+angle);float b=(float)(2*Math.PI*(i+1)/37+angle);p.setColor(order[i]==0?Color.rgb(20,133,65):reds.contains(order[i])?red:Color.rgb(22,22,24));p.setStyle(Paint.Style.FILL);canvas.drawArc(cx-r,cy-r,cx+r,cy+r,(float)Math.toDegrees(a),(float)Math.toDegrees(b-a),true,p);float mid=(a+b)/2;text(canvas,""+order[i],cx+(float)Math.cos(mid)*144,cy+(float)Math.sin(mid)*144+5,12,Color.WHITE);}
      circle(canvas,gold,cx,cy,113);circle(canvas,Color.rgb(95,56,29),cx,cy,103);circle(canvas,gold,cx,cy,32);circle(canvas,Color.rgb(53,33,21),cx,cy,18);
      float ballAngle=spinning?(float)(-angle*1.7):-.8f;circle(canvas,Color.WHITE,cx+(float)Math.cos(ballAngle)*123,cy+(float)Math.sin(ballAngle)*123,8);
      float left=480,top=126,cw=44,ch=38;
      fill(canvas,Color.rgb(6,50,35),left-8,top-8,left+13*cw+8,top+3*ch+13,6);
      for(int n=1;n<=36;n++){int col=(n-1)/3,row=2-(n-1)%3;float x=left+col*cw,y=top+row*ch;fill(canvas,reds.contains(n)?red:Color.rgb(22,25,28),x+1,y+1,x+cw-1,y+ch-1,2);text(canvas,""+n,x+cw/2,y+25,16,Color.WHITE);if(selection==n){p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);p.setColor(gold);canvas.drawRect(x+2,y+2,x+cw-2,y+ch-2,p);p.setStyle(Paint.Style.FILL);}}
      fill(canvas,Color.rgb(16,120,58),480,260,656,315,8);text(canvas,"0",568,296,28,Color.WHITE);
      fill(canvas,red,670,260,806,315,8);text(canvas,"RED",738,295,24,Color.WHITE);
      fill(canvas,Color.rgb(20,23,27),816,260,956,315,8);text(canvas,"BLACK",886,295,24,Color.WHITE);
      fill(canvas,gold,500,363,680,423,10);text(canvas,"SPIN",590,403,29,Color.BLACK);
      fill(canvas,Color.rgb(18,92,69),702,363,822,423,10);text(canvas,"BET +10",762,400,19,Color.WHITE);
      fill(canvas,Color.rgb(95,49,36),835,363,960,423,10);text(canvas,"RESET",897,400,20,Color.WHITE);
      text(canvas,"Select a number or RED / BLACK. Practice only. No real money.",718,476,15,Color.WHITE);
      text(canvas,spinning?"SPINNING...":last<0?"PLACE YOUR BET":("RESULT: "+last+(win(last)?"  WIN":"  LOSS")),252,512,20,gold);
      canvas.restore();if(spinning)postInvalidateDelayed(16);
    }
    boolean win(int n){return selection==-2?reds.contains(n):selection==-3?n!=0&&!reds.contains(n):selection==n;}
    @Override public boolean onTouchEvent(MotionEvent e){if(e.getAction()!=MotionEvent.ACTION_UP)return true;float x=e.getX()*1000/getWidth(),y=e.getY()*560/getHeight();if(spinning)return true;
      if(y>=126&&y<240&&x>=480&&x<1008){int col=(int)((x-480)/44),row=(int)((y-126)/38);if(col<12&&row<3)selection=col*3+3-row;}
      else if(y>=260&&y<=315){if(x>=480&&x<656)selection=0;else if(x>=670&&x<806)selection=-2;else if(x>=816&&x<956)selection=-3;}
      else if(y>=363&&y<=423){if(x>=500&&x<680){if(balance>=stake){balance-=stake;spinning=true;start=System.currentTimeMillis();}}else if(x>=702&&x<822)stake=stake>=100?10:stake+10;else if(x>=835&&x<960){balance=1000;stake=10;last=-1;selection=-2;}}
      invalidate();return true;
    }
  }
}
