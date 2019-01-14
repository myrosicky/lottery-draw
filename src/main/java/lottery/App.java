package lottery;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
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

import javax.imageio.ImageIO;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
public class App {

	private final static Logger log = LoggerFactory.getLogger(App.class);
	
	public static void main(String[] args){
//		String[] fontNames=GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		try {
//			for(int i=0;i<fontNames.length;i++){
//				createImage("A", new Font(fontNames[i], Font.PLAIN, 500), new File("src/main/webapp/img2/"+fontNames[i] +".png"), 500, 500);
//			}
			
//			for(int i = 65; i < 91; i++){
//				createImage(String.valueOf((char)i), new Font("Kinnari", Font.PLAIN, 500), new File("src/main/webapp/img/"+String.valueOf((char)i)+".png"), 500, 500);
//			}
//			createImage("J", new Font("微软雅黑", Font.PLAIN, 500), new File("src/main/webapp/img/"+"J"+".png"), 500, 500);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		SpringApplication.run(App.class, args);
	}
	
	@GetMapping("/")
	String index(){
		return "3d";
	}
	
	private List<Profile> profileList;
	private List<Profile> deletedProfileList = new  ArrayList<>(2);
	private List<TablePrize> tablePrizeList;
	private Map<Integer, List<Profile>> tablePrizeLuckyGuyMap;
	
	class Profile implements Serializable{
		 private String id;
		 private String image;
		 private String thumb_image;
		 private String name;
		 private String status;
		 
		 
		public Profile(String id, String image, String thumb_image, String name, String status) {
			super();
			this.id = id;
			this.image = image;
			this.thumb_image = thumb_image;
			this.name = name;
			this.status = status;
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
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
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
		private String amount;
		 
		 
		public TablePrize(int id, String image, String name, String amount) {
			super();
			this.id = id;
			this.image = image;
			this.name = name;
			this.amount = amount;
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
		
		
		public String getAmount() {
			return amount;
		}
		public void setAmount(String amount) {
			this.amount = amount;
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
	
	@PostMapping("/lucky/addIndex")
	@ResponseBody
	void addIndex(@RequestParam("lucky_prize") Integer tablePrizeId, @RequestParam("profileId") String profileId){
		log.debug("add index, tablePrizeId:" + tablePrizeId + ", profileId:" + profileId);
		List<Profile> luckyGuys = tablePrizeLuckyGuyMap.get(tablePrizeId);
		for(int i = 0, length = deletedProfileList.size(); i < length ; i++){
			Profile profile = deletedProfileList.get(i);
			if(profile.getId().equals(profileId)){
				luckyGuys.add(profile);
				deletedProfileList.remove(i);
				break;
			}
		}
		recordLuckyDrawResultIfNeed();
	}
	
	@PostMapping("/lucky/removeIndex")
	@ResponseBody
	void removeIndex(@RequestParam("lucky_prize") Integer tablePrizeId, @RequestParam("profileId") String profileId){
		log.debug("removeIndex, tablePrizeId:" + tablePrizeId + ", profileId:" + profileId);
		List<Profile> luckyGuys = tablePrizeLuckyGuyMap.get(tablePrizeId);
		for(int i = 0, length = luckyGuys.size(); i < length ; i++){
			Profile profile = luckyGuys.get(i);
			if(profile.getId().equals(profileId)){
				deletedProfileList.add(profile);
				luckyGuys.remove(i);
				break;
			}
		}
		recordLuckyDrawResultIfNeed();
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
				if(prizeName == null || prizeName.trim().length()==0){
					break;
				}
				
				String amount = "";
				try{
					amount = String.valueOf(((Double)row.getCell(2).getNumericCellValue()).intValue());
				}catch(Exception e){
					try {
						amount = row.getCell(2).getStringCellValue();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				
				rtn.add(new TablePrize(rowIndex + 1, "img/" + row.getCell(1).getStringCellValue().trim(), prizeName.trim(), amount));
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
				
				// handle id column
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
				if(id == null || id.trim().length()==0){
					break;
				}
				
				// handle name column
				String name = row.getCell(1).getStringCellValue().trim();
				
				// handle image column
				String image = null;
				if(row.getCell(2) == null){
					image = "img/" + name.substring(0, 1).toUpperCase() + ".png";
				}else{
					image = row.getCell(2).getStringCellValue();
					if(image == null){
						image = "img/" + name.substring(0, 1).toUpperCase() + ".png";
					}else{
						image = "img/" + image.trim();
					}
				}
				
				// handle status column
				String status = null;
				if(row.getCell(3) == null || row.getCell(3).getStringCellValue() == null){
					status = "";
				}else{
					status = row.getCell(3).getStringCellValue().trim();
				}
				
				rtn.add(new Profile(id, image, image, name, status));
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
		List<Profile> luckyCurrentResult = new ArrayList<>(totalLuckyNum);
		if(!profileList.isEmpty()){
			ThreadLocalRandom tlr = ThreadLocalRandom.current();
			for(int i = 0; i < totalLuckyNum; i++){
				int nextLuckyNum = tlr.nextInt(0, profileList.size());
				Profile luckyProfile = profileList.remove(nextLuckyNum);
				luckyCurrentResult.add(luckyProfile);
			}
		}
		String tablePrizeName = null;
		for(TablePrize tablePrize : tablePrizeList){
			if(tablePrize.getId()== tablePrizeId){
				tablePrizeName = tablePrize.getName();
			}
		}
		log.info("tablePrize:" + tablePrizeName + "--------------------");
		log.info("lucky guys: "  + luckyCurrentResult );
		log.info("");
		
		rtn.setLuckyResult(luckyCurrentResult);
		
		List<Profile> luckyResult = tablePrizeLuckyGuyMap.get(tablePrizeId);
		if(luckyResult == null){
			luckyResult = luckyCurrentResult;
		}else{
			luckyResult.addAll(luckyCurrentResult);
		}
		tablePrizeLuckyGuyMap.put(tablePrizeId, luckyResult);
		rtn.setNextAvailableAttendance(profileList.size() + "");
		
		recordLuckyDrawResultIfNeed();
		
		return rtn;
	}
	
	private void recordLuckyDrawResultIfNeed(){
		if(tablePrizeLuckyGuyMap.size() == tablePrizeList.size()){ // the end of lottery draw
			log.info("最终结果（或许有多个最终结果，请以最后一个为准） >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			for(TablePrize prize : tablePrizeList){
				log.info("--------------------" + prize.getName() + "--------------------");
				StringBuilder luckGuys = new StringBuilder(100);
				for(Profile profile : tablePrizeLuckyGuyMap.get(prize.getId())){
					luckGuys.append(", ").append(profile.getName()).append("(").append(profile.getId()).append(")");
				}
				luckGuys = luckGuys.deleteCharAt(0);
				log.info(luckGuys.toString());
				log.info("");
			}
			
		}
	}
	
	
    // 根据str,font的样式以及输出文件目录  
    public static void createImage(String str, Font font, File outFile,  
            Integer width, Integer height) throws Exception {  
        // 创建图片  
        BufferedImage image = new BufferedImage(width, height,  
                BufferedImage.TYPE_INT_BGR);  
        Graphics g = image.getGraphics();  
        g.setClip(0, 0, width, height);  
//        g.setColor(Color.white);  
        g.setColor(Color.red);  
        g.fillRect(0, 0, width, height);// 先用黑色填充整张图片,也就是背景  
//        g.setColor(Color.black);// 在换成黑色  
        g.setColor(Color.white);// 在换成黑色  
        g.setFont(font);// 设置画笔字体  
        /** 用于获得垂直居中y */  
        Rectangle clip = g.getClipBounds();  
        FontMetrics fm = g.getFontMetrics(font);  
        int ascent = fm.getAscent();  
        int descent = fm.getDescent();  
        int y = (clip.height - (ascent + descent)) / 2 + ascent - 50;  
//        for (int i = 0; i < 6; i++) {// 256 340 0 680  
//            g.drawString(str, i * 680, y);// 画出字符串  
//        }  
        
        int x = (clip.width - fm.stringWidth(str)) / 2;  
        g.drawString(str, x, y);// 画出字符串  
        
        g.dispose();  
        ImageIO.write(image, "png", outFile);// 输出png图片  
    }
	
	
	
}
