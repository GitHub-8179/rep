package com.zzkj.reptile.task;

import com.github.pagehelper.PageHelper;
import com.zzkj.reptile.dao.KeywordMapper;
import com.zzkj.reptile.entity.IpPostEntity;
import com.zzkj.reptile.entity.Keyword;
import com.zzkj.reptile.entity.ReptileEntity;
import com.zzkj.reptile.service.Gather;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sound.midi.Soundbank;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

//@Component
//@EnableScheduling
//@EnableAsync
public class SchedulerTask {
	private static final Logger log = LoggerFactory.getLogger(SchedulerTask.class);

	@Autowired
	private KeywordMapper keywordMapper ;

	@Autowired
	private com.zzkj.reptile.dao.ReptileDao reptileDao;

	@Value("${WEB_URL}")
	private String WEB_URL ;

	@Value("${WEB_CONDITION}")
	private String WEB_CONDITION ;

	@Value("${SERVER_NUM}")
	private int serverNum ;

	@Value("${REMAINDER_NUM}")
	private int remainderNum;

//	@Value("${WEB_COOKIE}")
	@Value("#{'${WEB_COOKIE}'.split('#;#')}")
	private List<String> WEB_COOKIE ;

	@Value("${stateTime}")
	public String stateTime ;
	@Value("${endTime}")
	public String endTime ;
	@Value("${addTime}")
	public int addTime ;

//	@Scheduled(initialDelay=100,fixedDelay=1000*60*60*24)
//	@Scheduled(initialDelay=100,fixedDelay=1000*60*5)
	@Scheduled(cron = "0 05 0 * * ?")
	@Async
	public void job1(){
		try {

			Keyword  keyword = new Keyword(serverNum,remainderNum) ;
			List<Keyword>  listKeyword = keywordMapper.selectKeyWork(keyword);
			System.out.println("关键词之前"+listKeyword.size());

			int keyWordsize = listKeyword.size()/serverNum+1;
			PageHelper.startPage(remainderNum+1, keyWordsize);
			listKeyword = keywordMapper.selectKeyWork(keyword);
			System.out.println("关键词之后"+listKeyword.size());


//			IpPostEntity ipPostEntity = new IpPostEntity(serverNum,remainderNum);
			IpPostEntity ipPostEntity = new IpPostEntity();
			ipPostEntity.setState(1);
			List<IpPostEntity> ipPost = reptileDao.selectIpPost(ipPostEntity);
			System.out.println("ip之前"+ipPost.size());
			int ipPostSize = ipPost.size()/serverNum+1;
			PageHelper.startPage(remainderNum+1, ipPostSize);
			ipPost = reptileDao.selectIpPost(ipPostEntity);

			System.out.println("ip之后"+ipPost.size());

			LocalDate today = LocalDate.now();
			LocalDate yesterday = today.plusDays(-1);
			stateTime = yesterday.toString();
			for (int i=0 ,num =listKeyword.size();i<num;i++){
//			for (Keyword kyword:listKeyword ) {
				Keyword kyword = listKeyword.get(i);
				kyword.setCreateTime(stateTime);
				kyword.setLastTime(stateTime);
				setData(1,kyword,ipPost);
				log.info("{} _ {}/{} {}插入结束",stateTime,i,num,kyword.getKeywordName());
			}

//			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//			Date dd = df.parse(stateTime);
//			Calendar calendar = Calendar.getInstance();
//			calendar.setTime(dd);
//			calendar.add(Calendar.DAY_OF_MONTH, addTime);//加一天
//
//			stateTime =  df.format(calendar.getTime());
//
//			Date dd1 = df.parse(endTime);
//			Calendar calendar1 = Calendar.getInstance();
//			calendar1.setTime(dd1);
//			calendar1.add(Calendar.DAY_OF_MONTH, addTime);//加一天
//			endTime =  df.format(calendar1.getTime());
//
//			System.out.println(stateTime+"::::"+endTime);

//		    	Calendar cld = Calendar.getInstance();//可以对每个时间域单独修改
//		    	int hour = cld.get(Calendar.HOUR_OF_DAY);
//		    	if(hour<listArticleType.size()) {
//		    		gather.setData(1,listArticleType.get(hour),ipPost);
//		    	}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void setData(int contentType, Keyword keyword, List<IpPostEntity> ipPost) throws Exception {

		StringBuffer url = new StringBuffer(WEB_URL);
		url.append("?");
		url.append(WEB_CONDITION);
		url.append("&query=" + keyword.getKeywordName().replace("&", "与"));

		Random ran = new Random();
		Elements sogouNext = null;
		ReptileEntity reptileEntity = null;
		Elements imgtxtBox = null;
		String detailsPath = null;
		String articleId = null;
		String articleTitle = null;
		String contentExcerpt = null;
		String source = null;
		Long createTime = 0L;
		String urlPath = url.toString();
		urlPath = urlPath.replace("startTime",keyword.getCreateTime());
		urlPath = urlPath.replace("endTime", keyword.getLastTime());
//		urlPath = urlPath.replace("pageSize", 1+"");

		Document document = null;
		int i = 0;
		String maxInfo = "";
		int j = 0;

		while (true) {
//			document = Gather.getHeader(ran, urlPath, ipPost, i, WEB_COOKIE.get(ran.nextInt(WEB_COOKIE.size())));
			document = Gather.getHeader(ran, urlPath, ipPost, i, WEB_COOKIE );
			if (document != null) {
				maxInfo = document.getElementsByTag("body").text();
				if (maxInfo == null ||
						"Maximum number of open connections reached.".equals(maxInfo) ||
						"".equals(maxInfo) ||
						maxInfo.startsWith("Not Found") ||
						maxInfo.indexOf("Internal Privoxy Error") != -1 ||
						maxInfo.indexOf("Server dropped connection") != -1 ||
						maxInfo.indexOf("Host Not Found or connection failed") != -1 ||
						maxInfo.length() < 350
				) {
					Thread.sleep(ran.nextInt(18000));
					continue;
				}
			} else {
				if(maxInfo.indexOf("没有找到相关的微信公众号文章。")!=-1){
					break;
				}
				if (j >= 2) {
					break;
				} else {
					j++;
					Thread.sleep(ran.nextInt(18000));
					continue;
				}
			}
			Element elements = document.getElementsByClass("news-list").last();
			if (elements != null) {
				Elements lis = elements.getElementsByTag("li");
				if (lis != null) {

					for (Element e : lis) {

						reptileEntity = new ReptileEntity();
						reptileEntity.setArticleTypeId(keyword.getId());
						imgtxtBox = e.getElementsByTag("div");

						articleId = e.attr("d");
						articleId = articleId.substring(articleId.lastIndexOf("-") + 1);
						reptileEntity.setArticleId(articleId);

						detailsPath=imgtxtBox.select("a[data-share]").attr("data-share");
//						detailsPath = imgtxtBox.select("a").first().attr("data-share");
						reptileEntity.setDetailsPath(detailsPath);
//						System.out.println("     ======================      "+detailsPath);

						reptileEntity.setContentCrawl(imgtxtBox.toString().getBytes());

						articleTitle = imgtxtBox.select("h3").last().text();
						reptileEntity.setArticleTitle(articleTitle);

						reptileEntity.setArticleKeyword(keyword.getKeywordName());

						contentExcerpt = imgtxtBox.select("p").last().text();
						reptileEntity.setContentExcerpt(contentExcerpt);

						Element txtBox2 = imgtxtBox.get(2);
						source = txtBox2.getElementsByTag("a").first().text();
						reptileEntity.setSource(source);
						createTime = Long.valueOf(txtBox2.attr("t"));
						reptileEntity.setCreateTime(createTime);

						reptileEntity.setContentType(contentType);
						try {
							log.trace(reptileEntity.getArticleId());
							reptileDao.insert(reptileEntity);
						} catch (Exception es) {
						}
					}
				}

			}
			log.info("访问地址：{} ——长度：{}" ,urlPath, maxInfo.length());
//			sogouNext = document.getElementById("sogou_next");
			sogouNext =document.select("a[uigs=page_next]");
			if (sogouNext == null ||sogouNext.size()==0) {
				if (j >= 2) {
					break;
				} else {
					j++;
					Thread.sleep(ran.nextInt(18000));
					continue;
				}
			} else {
				j = 0;
				urlPath = sogouNext.attr("href");
				urlPath = WEB_URL + urlPath;
				sogouNext = null;
			}
			Thread.sleep(ran.nextInt(8000));

		}


	}



}
