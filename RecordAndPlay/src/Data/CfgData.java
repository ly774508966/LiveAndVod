package Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CfgData {
	
	private static String serverIp = "";
	private static String serverPort = "";
	private static String bufferSize = "";
	private final static String CFGNAME_STRING = "devCfg";
	
    public static String getServerIp() {
		return serverIp;
	}

	public static String getServerPort() {
		return serverPort;
	}


	public static String getBufferSize() {
		return bufferSize;
	}


	public static void GetCfgInfo(Context context)
    {
    	//����ȡ�����ڴ������ݣ��򵥵���Ϣͨ����ֵ�������漴��
    	//FileInputStream fls = new FileInputStream(pathString);
    	//fls.read();
    	
    	SharedPreferences share = 
    			context.getSharedPreferences(CFGNAME_STRING, Context.MODE_PRIVATE);
    			//getSharedPreferences(pathString, Context.MODE_PRIVATE);
    	serverIp = share.getString("IP", "192.168.100.142");
    	serverPort = share.getString("PORT", "8080");
    	bufferSize = share.getString("BUFFER", "512");
    }
    
    public static void SetCfgInfo(Context context,String ip,String port,String buffer)
    {
    	SharedPreferences share = 
    			context.getSharedPreferences(CFGNAME_STRING, Context.MODE_PRIVATE);
    	//��Ҫ��editor�ύ����
    	SharedPreferences.Editor editor = share.edit();
    	editor.putString("IP", ip);
    	editor.putString("PORT", port);
    	editor.putString("BUFFER", buffer);
    	editor.commit();
    }
}
