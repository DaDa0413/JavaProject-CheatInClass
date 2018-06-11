
/** 
* @see 
* @author Al_assad yulinying_1994@outlook.com 
* @date 2016年11月17日 下午6:27:59 
* @version V1.0   
* Description: 简易音频播放器（只支持AU，RA，WAV） 
*              在不使用JMF的情况下快速实现音频播放 
*  
*/  
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;  
  
public class MusicPlayer {  
    private String musicPath; //音频文件  
    private volatile boolean run = true;  //记录音频是否播放  
    private Thread mainThread;   //播放音频的任务线程  
      
    private AudioInputStream audioStream;  
    private AudioFormat audioFormat;  
    private SourceDataLine sourceDataLine;  
      
    public MusicPlayer(String musicPath) {  
        this.musicPath = musicPath;  
        prefetch();  
    }  
      
    //数据准备  
    private void prefetch(){  
        try{  
        //获取音频输入流  
        audioStream = AudioSystem.getAudioInputStream(new File(musicPath));  
        //获取音频的编码对象  
        audioFormat = audioStream.getFormat();  
        //包装音频信息  
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,  
                audioFormat,AudioSystem.NOT_SPECIFIED);  
        //使用包装音频信息后的Info类创建源数据行，充当混频器的源  
        sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);  
          
        sourceDataLine.open(audioFormat);  
        sourceDataLine.start();  
          
        }catch(UnsupportedAudioFileException ex){  
            ex.printStackTrace();  
        }catch(LineUnavailableException ex){  
            ex.printStackTrace();  
        }catch(IOException ex){  
            ex.printStackTrace();  
        }  
          
    }  
    //析构函数:关闭音频读取流和数据行  
    protected void finalize() throws Throwable{  
        super.finalize();  
        sourceDataLine.drain();  
        sourceDataLine.close();  
        audioStream.close();  
    }  
      
    //播放音频:通过loop参数设置是否循环播放  
    private void playMusic(boolean loop)throws InterruptedException {  
        try{  
                if(loop){  
                    while(true){  
                        playMusic();  
                    }  
                }else{  
                    playMusic();  
                    //清空数据行并关闭  
                    sourceDataLine.drain();  
                    sourceDataLine.close();  
                    audioStream.close();  
                }  
              
        }catch(IOException ex){  
            ex.printStackTrace();  
        }  
          
          
    }  
    private void playMusic(){  
        try{  
            synchronized(this){  
                run = true;  
            }  
            //通过数据行读取音频数据流，发送到混音器;  
            //数据流传输过程：AudioInputStream -> SourceDataLine;  
            audioStream = AudioSystem.getAudioInputStream(new File(musicPath));  
            int count;  
            byte tempBuff[] = new byte[1024];  
              
                while((count = audioStream.read(tempBuff,0,tempBuff.length)) != -1){  
                    synchronized(this){  
                    while(!run)  
                        wait();  
                    }  
                    sourceDataLine.write(tempBuff,0,count);  
                              
            }  
  
        }catch(UnsupportedAudioFileException ex){  
            ex.printStackTrace();  
        }catch(IOException ex){  
            ex.printStackTrace();  
        }catch(InterruptedException ex){  
            ex.printStackTrace();  
        }  
          
    }  
      
      
    //暂停播放音频  
    private void stopMusic(){  
        synchronized(this){  
            run = false;  
            notifyAll();  
        }  
    }  
    //继续播放音乐  
    private void continueMusic(){  
        synchronized(this){  
             run = true;  
             notifyAll();  
        }  
    }  
      
      
    //外部调用控制方法:生成音频主线程；  
    public void start(boolean loop){  
        mainThread = new Thread(new Runnable(){  
            public void run(){  
                try {  
                    playMusic(loop);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
            }  
        });  
        mainThread.start();  
    }  
      
    //外部调用控制方法：暂停音频线程  
    public void stop(){  
        new Thread(new Runnable(){  
            public void run(){  
                stopMusic();  
                  
            }  
        }).start();  
    }  
    //外部调用控制方法：继续音频线程  
    public void continues(){  
        new Thread(new Runnable(){  
            public void run(){  
                continueMusic();  
            }  
        }).start();  
    }  
  
//Test  
    public static void main(String[] args) throws InterruptedException{  
  
        MusicPlayer player = new MusicPlayer("C:\\Users/RRRRRROG/Desktop/001.wav");   //创建音乐播放器  
          
        player.start(true);                                        //以开始以循环的形式播放，player(false)为不循环播放  
          
        TimeUnit.SECONDS.sleep(5);  
          
        player.stop();                        //暂停播放音频  
          
        TimeUnit.SECONDS.sleep(4);  
          
        player.continues();                //继续开始播放音频  
          
    }  
  
 }  