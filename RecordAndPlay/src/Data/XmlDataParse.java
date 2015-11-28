package Data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import Model.LiveChannel;
import Model.LiveClass;
import Model.VodClass;
import Model.VodProgram;
import Model.VodVedio;

import android.util.Xml;

//����ֱ���͵㲥�ķ����ĵ�����,����directList

/*д�����ʱ������м�����������п��ܳ�����,������顣
     д�����ԣ������ǿ�ͷ�����������뵼���������ѭ���������Ǹ�type��ֵ������ѭ����
     ����xml�ĵ��ı��벻��ȷ�������룬�����⼸�����Ƽ򵥵�����Ӱ���˽����
     �Ժ�д�����Ҫͨ��һ���ٵ��ԣ�����������������Ӱ��Ч��*/
public class XmlDataParse{
	public static Object Parser(InputStream is)
	{
		//��ʼ��pull������,liveDirector,vodDirector
		XmlPullParser xpParser = Xml.newPullParser();
		
		ArrayList<LiveClass> liveTotalClasses = null;//ֱ���༯��
		LiveClass liveClass = null;//ֱ����
		LiveChannel liveChannel;//ֱ��Ƶ��
		
		ArrayList<VodClass>  vodTotalClasses = null;//�㲥�༯��
		VodClass vodClass = null;//�㲥��
		VodProgram vodProgram = null;//�㲥����
		VodVedio vodVedio;//�㲥��Ŀ
		
		Boolean isLive = false;//ֱ����־
		
		try {
			
			//�����������룬Ҫע�⣡
			xpParser.setInput(is, "UTF-8");
			
			//��ó�ʼ�ڵ���¼�����
			int type = xpParser.getEventType();
			//���ڵ㲻�ǽ����ڵ㣬һֱ����
			while(type != XmlPullParser.END_DOCUMENT)
			{
				//��ýڵ�����
				String nodeName = xpParser.getName();
				//�жϽڵ��¼�����
				switch (type) {
				case XmlPullParser.START_TAG:
					//��ýڵ�����
					if ("Asset".equals(nodeName)) {
						if ("LIVE".equals(xpParser.getAttributeValue(null, "Type"))) {
							liveTotalClasses = new ArrayList<>();
							isLive = true;
						}
						else if ("VOD".equals(xpParser.getAttributeValue(null, "Type"))) {
							vodTotalClasses = new ArrayList<>();
							isLive = false;
						}
					} 
					else if ("Class".equals(nodeName)) {
						if (isLive){
							liveClass = new LiveClass();
							liveClass.setID(Integer.parseInt( xpParser.getAttributeValue(null, "ID")));
							liveClass.setName(xpParser.getAttributeValue(null, "Name"));
						}
						else if (isLive == false) {
							vodClass = new VodClass();
							vodClass.setID(Integer.parseInt( xpParser.getAttributeValue(null, "ID")));
							vodClass.setName(xpParser.getAttributeValue(null, "Name"));
						}
					} 
					else if ("Program".equals(nodeName)) {
						vodProgram = new VodProgram();
						vodProgram.setID(Integer.parseInt( xpParser.getAttributeValue(null, "ID")));
						vodProgram.setName(xpParser.getAttributeValue(null, "Name"));
						vodProgram.setChannelID(Integer.parseInt( xpParser.getAttributeValue(null, "ChanID")));
					} 
					//¼���ļ����㲥������λ
					else if ("Serial".equals(nodeName)) {
						vodVedio = new VodVedio();
						vodVedio.setNumber(Integer.parseInt( xpParser.getAttributeValue(null, "No")));
						vodVedio.setName(xpParser.getAttributeValue(null, "Name"));
						vodVedio.setFileDuration(xpParser.getAttributeValue(null, "Duration"));
						vodVedio.setLowUrl(xpParser.getAttributeValue(null, "LowURL"));
						vodVedio.setHighUrl(xpParser.getAttributeValue(null, "HiURL"));
						vodProgram.setVedioList(vodVedio);
					} 
					//ͨ����ֱ��������λ
					else if ("Channel".equals(nodeName)) {
						liveChannel = new LiveChannel();
						liveChannel.setId(Integer.parseInt( xpParser.getAttributeValue(null, "No")));
						liveChannel.setName(xpParser.getAttributeValue(null, "Name"));
						liveChannel.setLowUrl(xpParser.getAttributeValue(null, "LowURL"));
						liveChannel.setHighUrl(xpParser.getAttributeValue(null, "HiURL"));
						liveClass.setChannelList(liveChannel);
					}
					break;
				case XmlPullParser.END_TAG:
					if ("Program".equals(nodeName)) {
						vodClass.setProgramList(vodProgram);
					}
					else if ("Class".equals(nodeName)) {
						if (!isLive) {
							vodTotalClasses.add(vodClass);
						}
						else {
							liveTotalClasses.add(liveClass);
						}
					}
					break;
				default:
					break;
				}
				
				try {
					//���Ǹ�ֵ��type��������ѭ����С��
					type = xpParser.next();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
		catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (isLive) {
			return liveTotalClasses;
		}
		else {
			return vodTotalClasses;
		}
		
	}
}
