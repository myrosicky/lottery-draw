package lottery;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
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
	private List<TablePrize> tablePrizeList;
	private Map<Integer, List<Profile>> tablePrizeLuckyGuyMap;
	
	class Profile implements Serializable{
		 private String id;
		 private String image;
		 private String thumb_image;
		 private String name;
		 
		 
		public Profile(String id, String image, String thumb_image, String name) {
			super();
			this.id = id;
			this.image = image;
			this.thumb_image = thumb_image;
			this.name = name;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
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
	class TablePrize implements Serializable{
		private int id;
		 private String image;
		 private String name;
		 
		 
		public TablePrize(int id, String image, String name) {
			super();
			this.id = id;
			this.image = image;
			this.name = name;
		}
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
	
	
	class LotteryData implements Serializable{
		private int res;
		private List<Profile> data;
		private List<TablePrize> tablePrize;
		private Map<Integer, List<Profile>> tablePrizeLuckyPeople;
		
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
		public List<TablePrize> getTablePrize() {
			return tablePrize;
		}
		public void setTablePrize(List<TablePrize> tablePrize) {
			this.tablePrize = tablePrize;
		}
		public Map<Integer, List<Profile>> getTablePrizeLuckyPeople() {
			return tablePrizeLuckyPeople;
		}
		public void setTablePrizeLuckyPeople(
				Map<Integer, List<Profile>> tablePrizeLuckyPeople) {
			this.tablePrizeLuckyPeople = tablePrizeLuckyPeople;
		}
		
		
		
	}
	
	@GetMapping("/lucky/data")
	@ResponseBody
	LotteryData loadAllDat(){
		LotteryData lotteryData = new LotteryData();
		lotteryData.setRes(1);
		
		// load profiles
		if(profileList == null){
			profileList = loadProfiles();
		}
		lotteryData.setData(profileList);
		
		// load table prize
		if(tablePrizeList == null){
			tablePrizeList = loadTablePrize();
			tablePrizeLuckyGuyMap = new HashMap<Integer, List<Profile>>(tablePrizeList.size()*2);
		}
		lotteryData.setTablePrize(tablePrizeList);
		lotteryData.setTablePrizeLuckyPeople(tablePrizeLuckyGuyMap);
		return lotteryData;
	}
	
	
	private List<TablePrize> loadTablePrize(){
		List<TablePrize> rtn =  new ArrayList<TablePrize>();
		InputStream in  = null;
		XSSFWorkbook wb = null;
    	try {
			in  = new FileInputStream(new ClassPathResource("tablePrize.xlsx").getFile());
			wb = new XSSFWorkbook(in); 
			XSSFSheet sheet = wb.getSheetAt(0);
			int rowIndex = 0;
			while(true){
				Row row = sheet.getRow(rowIndex);
				if(row == null || row.getCell(0)==null){
					break;
				}
				String prizeName = row.getCell(0).getStringCellValue();
				if(log.isDebugEnabled()){
					log.debug("rowIndex:" + rowIndex + ", prizeName:" + prizeName);
				}
				if(prizeName == null || prizeName.trim().length()==0){
					break;
				}
				rtn.add(new TablePrize(rowIndex + 1, "img/" + row.getCell(1).getStringCellValue().trim(), prizeName.trim()));
				rowIndex++;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			
			
			if(wb != null){
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
    	return rtn;

	}
	
	private List<Profile> loadProfiles(){
		List<Profile> rtn =  new ArrayList<Profile>();
		InputStream in  = null;
		XSSFWorkbook wb = null;
    	try {
			in  = new FileInputStream(new ClassPathResource("profiles.xlsx").getFile());
			wb = new XSSFWorkbook(in); 
			XSSFSheet sheet = wb.getSheetAt(0);
			int rowIndex = 1;
			while(true){
				Row row = sheet.getRow(rowIndex);
				if(row == null || row.getCell(0)==null){
					break;
				}
				String id = null;
				try{
					id = String.valueOf(((Double)row.getCell(0).getNumericCellValue()).intValue());
				}catch(Exception e){
					try {
						id = row.getCell(0).getStringCellValue();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				if(log.isDebugEnabled()){
					log.debug("rowIndex:" + rowIndex + ", id:" + id);
				}
				if(id == null || id.trim().length()==0){
					break;
				}
				rtn.add(new Profile(id, "img/" + row.getCell(2).getStringCellValue().trim(), "img/" + row.getCell(2).getStringCellValue().trim(), row.getCell(1).getStringCellValue().trim()));
				rowIndex++;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			
			
			if(wb != null){
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
    	return rtn;

	}
	
	
	
	class DrawResult  implements Serializable{
		private int res;
		private List<Profile>  luckyResult;
		private String nextAvailableAttendance;
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
		public String getNextAvailableAttendance() {
			return nextAvailableAttendance;
		}
		public void setNextAvailableAttendance(String nextAvailableAttendance) {
			this.nextAvailableAttendance = nextAvailableAttendance;
		}
	}
	
	@GetMapping("/lucky/index")
	@ResponseBody
	DrawResult luckyDraw(@RequestParam("lucky_num") Integer totalLuckyNum, @RequestParam("lucky_prize") Integer tablePrizeId){
		DrawResult rtn = new DrawResult();
		rtn.setRes(1);
		List<Profile> luckyResult = new ArrayList<>(totalLuckyNum);
		if(!profileList.isEmpty()){
			ThreadLocalRandom tlr = ThreadLocalRandom.current();
			for(int i = 0; i < totalLuckyNum; i++){
				int nextLuckyNum = tlr.nextInt(0, profileList.size());
				Profile luckyProfile = profileList.remove(nextLuckyNum);
				luckyResult.add(luckyProfile);
			}
		}
		String tablePrizeName = null;
		for(TablePrize tablePrize : tablePrizeList){
			if(tablePrize.getId()== tablePrizeId){
				tablePrizeName = tablePrize.getName();
			}
		}
		log.info("tablePrize:" + tablePrizeName + "--------------------");
		log.info("lucky guys: "  + luckyResult );
		log.info("");
		
		rtn.setLuckyResult(luckyResult);
		tablePrizeLuckyGuyMap.put(tablePrizeId, luckyResult);
		rtn.setNextAvailableAttendance(profileList.size() + "");
		return rtn;
	}
	
	
	
}
