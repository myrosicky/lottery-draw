package lottery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
public class App {

	private final static Logger log = LoggerFactory.getLogger(App.class);
	
	public static void main(String[] args){
		SpringApplication.run(App.class, args);
	}
	
	@GetMapping("/")
	String index(){
		return "3d";
	}
	
	private List<Profile> profileList;
	
	class Profile implements Serializable{
		 private int id;
		 private String image;
		 private String thumb_image;
		 private String name;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getImage() {
			return image;
		}
		public void setImage(String image) {
			this.image = image;
		}
		public String getThumb_image() {
			return thumb_image;
		}
		public void setThumb_image(String thumb_image) {
			this.thumb_image = thumb_image;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		@Override
		public String toString() {
			return "Profile [id=" + id + ", name=" + name + "]";
		}
		
	}
	
	class Profiles implements Serializable{
		private int res;
		private List<Profile> data;
		public int getRes() {
			return res;
		}
		public void setRes(int res) {
			this.res = res;
		}
		public List<Profile> getData() {
			return data;
		}
		public void setData(List<Profile> data) {
			this.data = data;
		}
		
	}
	
	@GetMapping("/lucky/data")
	@ResponseBody
	Profiles data(){
		Profiles profiles = new Profiles();
		profiles.setRes(1);
		
		profileList = new ArrayList<Profile>();
		Profile tmp = new Profile();
		tmp.setId(12652);
		tmp.setImage("img/userNotExist.jpg");
		tmp.setThumb_image("img/userNotExist.jpg");
		tmp.setName("no name");
		profileList.add(tmp);
		profiles.setData(profileList);
		return profiles;
	}
	
	class DrawResult  implements Serializable{
		private int res;
		private List<Profile>  luckyResult;
		public int getRes() {
			return res;
		}
		public void setRes(int res) {
			this.res = res;
		}
		public List<Profile> getLuckyResult() {
			return luckyResult;
		}
		public void setLuckyResult(List<Profile> luckyResult) {
			this.luckyResult = luckyResult;
		}
		
	}
	
	@GetMapping("/lucky/index")
	@ResponseBody
	DrawResult luckyDraw(@RequestParam("lucky_num") Integer totalLuckyNum, @RequestParam Integer lucky_prize){
		DrawResult rtn = new DrawResult();
		rtn.setRes(1);
		
		List<Profile> luckyResult = new ArrayList<>(totalLuckyNum);
		ThreadLocalRandom tlr = ThreadLocalRandom.current();
		for(int i = 0; i < totalLuckyNum; i++){
			int nextLuckyNum = tlr.nextInt(0, profileList.size());
			Profile luckyProfile = profileList.remove(nextLuckyNum);
			luckyResult.add(luckyProfile);
		}
		
		log.info("luckyPrize:" + lucky_prize + ", lucky guys: " + luckyResult);
		rtn.setLuckyResult(luckyResult);
		return rtn;
	}
	
	
	
}
