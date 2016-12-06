import java.io.Serializable;

public class Food implements Serializable {
	private static final long serialVersionUID = -5399605122490343339L;
	
	private String name;
	private String type;
	private Integer days;
	private Integer volume;
	
	public Food(String name, String type, int days, int volume){
		this.name = name;
		this.type = type;
		this.days = days;
		this.volume = volume;
	}
	
	public String getName(){
		return name;
	}
	
	public String getType(){
		return type;
	}
	
	public Integer getDays(){
		return days;
	}
	
	public Integer getVolume(){
		return volume;
	}
	
	public String toString(){
		return (name + " " + type + " " + days.toString() + " " + volume.toString());
	}
}
	