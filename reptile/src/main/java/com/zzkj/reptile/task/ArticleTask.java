package com.zzkj.reptile.task;

import com.github.pagehelper.PageHelper;
import com.zzkj.reptile.dao.ArticleMapper;
import com.zzkj.reptile.dao.ArticleTypeMapper;
import com.zzkj.reptile.dao.ReptileDao;
import com.zzkj.reptile.entity.Article;
import com.zzkj.reptile.entity.ArticleWithBLOBs;
import com.zzkj.reptile.entity.IpPostEntity;
import com.zzkj.reptile.service.Gather;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@EnableScheduling
public class ArticleTask {

	private static final Logger log = LoggerFactory.getLogger(ArticleTask.class);

	@Autowired
	private ArticleMapper articleMapper;

	@Value("#{'${ARTICLE_COOKIE}'.split('#;#')}")
	private List<String> ARTICLE_COOKIE ;
//	private String ARTICLE_COOKIE = "CXID=81945D65328D26038F38D9BAB73169FB; weixinIndexVisited=1; SUV=00983DF2B781ECDA5C47D8B4AAF85422; SUID=CB669CB73765860A5C31ADC8000C97B9; ABTEST=3|1551177707|v1; sw_uuid=3558220219; ssuid=6886420000; IPLOC=CN3310; ad=3yllllllll2tBunJlllllVhGqkolllllHc47Lkllll9lllll9Vxlw@@@@@@@@@@@; SNUID=69D29809D3D1574EF89E8A30D40F0556; td_cookie=4268680264; sct=23; JSESSIONID=aaabPeiIItz6o8F9OnaMw";

	@Value("${DATA_NUM}")
	private int DATA_NUM ;

	@Value("${SERVER_NUM}")
	private int serverNum ;

	@Value("${REMAINDER_NUM}")
	private int remainderNum;

	@Autowired
	private ReptileDao mapper;

	@Autowired
	private ArticleTypeMapper articleTypeMapper;


//	@Scheduled(cron = "${TASK_TIME}")
//	public void job22() {
//		job2(1);
//	}

	@Scheduled(cron = "${TASK_TIME}")
	public void job23() {
		job2(2);
	}

	@Scheduled(cron = "${TASK_TIME}")
	public void job24() {
		job2(4);
	}

	@Scheduled(cron = "${TASK_TIME}")
	public void job25() {
		job2(6);
	}


	public void job2(int ii ){
		if(ii==1){
			PageHelper.startPage(ii, DATA_NUM/2);
		}else{
			PageHelper.startPage(ii, DATA_NUM);
		}
		Article article1 = new Article(serverNum,remainderNum);
		article1.setState(0);
		article1.setGetState(3);
		List<Article> list = articleMapper.selectByExample(article1);

		Document document = null;
		ArticleWithBLOBs record = null;
		Element contentDiv = null;
		String contentTxt = null;
		String articleId = null;
		Random ran = new Random();
		String detailsPath = null;
		int i =0 ;
		String maxInfo ="";

		List<IpPostEntity> ipPost = null;
		if(list!=null&&list.size()>0) {
//			IpPostEntity ipPostEntity = new IpPostEntity();
			IpPostEntity ipPostEntity = new IpPostEntity(serverNum,remainderNum);
			ipPostEntity.setState(1);
			ipPost = mapper.selectIpPost(ipPostEntity);

			int ipPostSize = ipPost.size()/serverNum+1;
			PageHelper.startPage(remainderNum+1, ipPostSize);
			ipPost = mapper.selectIpPost(ipPostEntity);

		}else{
			return;
		}
		for (Article article : list) {
			articleId = article.getArticleId();
			detailsPath = article.getDetailsPath();
			record = new ArticleWithBLOBs();
			try {
				i=0;
				document = Gather.getHeader( ran, detailsPath,ipPost,i,ARTICLE_COOKIE);
				if(document!=null) {
					maxInfo = document.getElementsByTag("body").text();
					if(maxInfo==null||"Maximum number of open connections reached.".equals(maxInfo)||
							"".equals(maxInfo)||
							maxInfo.startsWith("Not Found")||
							maxInfo.indexOf("Internal Privoxy Error")!=-1||
							maxInfo.indexOf("Server dropped connection")!=-1||
							maxInfo.indexOf("Host Not Found or connection failed")!=-1
					) {
						if(article.getGetState()>=2) {
							record.setState(3);
							record.setArticleId(articleId);
							articleMapper.updateByDetails(record);
						}else{
							if(article.getGetState()>3){
								articleTypeMapper.deleteById(articleId);
							}
						}
						articleMapper.setGetStartAdd(articleId);
						continue;}
				}else {
					continue;
				}
				contentDiv = document.getElementById("img-content");
				if(contentDiv==null) {
					if(article.getGetState()>=2){
						record.setState(3);
						record.setArticleId(articleId);
						articleMapper.updateByDetails(record);
					}else{
						if(article.getGetState()>3){
							articleTypeMapper.deleteById(articleId);
						}
					}
					articleMapper.setGetStartAdd(articleId);
					continue;
				}else {
					contentTxt = contentDiv.text();
//					contentTxt=new String(contentTxt.getBytes(),"UTF-8");
					String code1 = document.select("meta[http-equiv=Content-Type]").get(0).attr("content");
					code1 = code1.substring(code1.indexOf("charset=")+8);
					contentTxt= new String(contentTxt.getBytes(code1) ,"UTF-8");

					String div = contentDiv.toString();
					div = div.replace("data-src=", "src=");
					div = div.substring(0,div.indexOf("<script nonce"));
					div =div+"</div>";
					record.setDetailsDiv(div.getBytes());
					record.setDetailsTxt(contentTxt.getBytes());
					record.setCollectInitcount(contentTxt.length());
					record.setState(1);
				}
				record.setArticleId(articleId);
				articleMapper.updateByDetails(record);
				articleId = null;

					Thread.sleep(ran.nextInt(2000));
			} catch (Exception e) {
				try {
//						 Thread.sleep(ran.nextInt(18000));
					log.error("插入文章链接错误！"+record+":"+e.toString());
					record.setState(3);
					record.setDetailsDiv(null);
					record.setDetailsTxt(null);
					articleMapper.updateByDetails(record);
				} catch (Exception e1) {
					log.error("修改文章状态为2错误！！"+record);
				}
			}
			log.info("插入文章链接："+detailsPath);
		}
	}

}
