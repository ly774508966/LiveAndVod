package Model;

//ֱ��ͨ��
public class LiveChannel {
	private int Id;
	private String Name;
	private String LowUrl;
	private String HighUrl;
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getLowUrl() {
		return LowUrl;
	}
	public void setLowUrl(String lowUrl) {
		LowUrl = lowUrl;
	}
	public String getHighUrl() {
		return HighUrl;
	}
	public void setHighUrl(String highUrl) {
		HighUrl = highUrl;
	}

	//ֱ����¼���Ƿ���HttpUri��������,ֱ����¼���ǰ�ᣬȻ���ȡֱ��url�ڱ��ز���
	public int BeginLive(){return 1;}
	public int EndLive(){return 1;}
	public int BeginRecord(){return 1;}
	public int EndRecord(){return 1;}
	//public int BeginPlay(){return 1;}
	//public int EndPlay(){return 1;}
}

