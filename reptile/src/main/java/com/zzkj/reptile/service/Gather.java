package com.zzkj.reptile.service;

import com.zzkj.reptile.entity.IpPostEntity;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
//@PropertySource({"classpath:webUser.properties","classpath:application.yml"})
public class Gather {

	private static final Logger log = LoggerFactory.getLogger(Gather.class);

	@Autowired
	private com.zzkj.reptile.dao.ReptileDao reptileDao;

	 @Value("${WEB_URL}")
	private String WEB_URL ;
	@Value("${WEB_COOKIE}")
	private String WEB_COOKIE ;

	public static List<String> userAgent;

	public List<String> getUserAgent() {
		return userAgent;
	}
	@Value("#{'${UserAgentValue}'.split('#;#')}")
	public void setUserAgent(List<String> userAgent) {
		this.userAgent = userAgent;
	}

	@Value("${WEB_CONDITION}")
	private String WEB_CONDITION ;


	public static Document getHeader(Random ran,String url,List<IpPostEntity> ipPost,int i,List<String>  cookie) {
		Document document =null;
		IpPostEntity sp = ipPost.get(ran.nextInt(ipPost.size()));
		String ip =sp.getIp();
		int post = sp.getPost();
		try {
//			System.out.println(url);
			Connection con= Jsoup.connect(url);//获取连接

			con.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0."+(ran.nextInt(5)+5)+",image/webp,image/apng,*/*;q=0."+(ran.nextInt(5)+5));
			con.header("Accept-Encoding", "gzip, deflate, br");
			con.header("Accept-Language", "zh-CN,zh;q=0."+(ran.nextInt(5)+5));
			con.header("Connection", "keep-alive");
			con.header("Upgrade-Insecure-Requests", "1");
			con.header("User-Agent", userAgent.get(ran.nextInt(userAgent.size())));
			con.header("Host", "weixin.sogou.com");
			con.header("Referer", url);
			con.header("Cookie", cookie.get(ran.nextInt(cookie.size())).replace("window",""+(ran.nextInt(10))+1).replace("ARTICLE_COOKIE",(230000000+ran.nextInt(9999999))+""));
			con.proxy(ip, post);
        con.ignoreContentType(true).ignoreHttpErrors(true);
        con.timeout(1000 * 60);
		con.maxBodySize(0);
		System.setProperty("https.proxySet", "true");
		System.getProperties().setProperty("http.proxyHost", ip);
		System.getProperties().setProperty("http.proxyPort", post+"");
		document  = con.get();
//			Connection.Response rs= con.execute();//获取响应
//			document=Jsoup.parse(rs.body());//转换为Dom树
//		Thread.sleep(ran.nextInt(5000));
//			System.out.println(ip+"请求地址"+document);
			if(document!=null&&document.text().contains("用户您好，您的访问过于频繁，为确认本次访问为正常用户行为，需要您协助验证")){
				log.info(ip+document.text());
//				IpPostEntity ipPostEntity = new IpPostEntity();
//				ipPostEntity.setState(0);
//				ipPostEntity.setIp(ip);
//				ipPostEntity.setPost(post);
//				reptileDao.insertsIpPost(ipPostEntity);
			}

		} catch (Exception e) {
//			System.out.println("请求地址错误"+ip);
			try {
				Thread.sleep(ran.nextInt(5000));
			}catch (Exception e2) {}
			if(i==10) {
				return null;
			}
			i++;
			return getHeader(ran,url,ipPost,i,cookie);
		}

		return  document;
	}



	public static Connection.Response  getCookie(Random ran,String url,List<IpPostEntity> ipPost) {
		IpPostEntity sp = ipPost.get(ran.nextInt(ipPost.size()));
		String ip =sp.getIp();
		int post = sp.getPost();
		try {
			Connection con= Jsoup.connect("http://weixin.sogou.com/weixin");//获取连接
			con.proxy(ip, post);
			con.ignoreContentType(true).ignoreHttpErrors(true);
			con.timeout(1000 * 30);
			con.maxBodySize(0);
			Connection.Response rs= con.execute();//获取响应
			Map<String,String> map = rs.cookies();
			String cookie = mapToString(map);
			System.out.println(cookie);
			return  rs;
		} catch (Exception e) {
			try {
				Thread.sleep(ran.nextInt(5000));
			}catch (Exception e2) {}
			return getCookie(ran,url,ipPost);
		}

	}
	public static String mapToString(Map<String,String> map){
		//通过map.entrySet()方法
		//方法一：循环map里面的每一对键值对，然后获取key和value
		StringBuffer cookie = new StringBuffer();
	  for(Map.Entry<String, String> vo : map.entrySet()){
		  cookie.append(vo.getKey());
		  cookie.append("=");
		  cookie.append( vo.getValue());
		  cookie.append("; ");
	  }

	  /*//使用迭代器，获取key
	  Iterator<Entry<String,String>> iter = map.entrySet().iterator();
	  while(iter.hasNext()){
	   Entry<String,String> entry = iter.next();
	   String key = entry.getKey();
	   String value = entry.getValue();
	   System.out.println(key+" "+value);
	  }*/
  		return cookie.toString().substring(0,cookie.length()-2);
	}

	public static String getSUV(){
		try {
			Date e = new Date();
			return EncoderByMd5(1E3 * e.getTime() + Math.round(1E3 * Math.random())+"");
		} catch (Exception es) {
			return "";
		}
	}

	public static String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {

		StringBuilder sig= new StringBuilder();
		MessageDigest digest = MessageDigest.getInstance("md5");
		byte[] bytes = digest.digest(str.getBytes("utf-8"));
		for (byte b : bytes) {
			String hex = Integer.toHexString(b&0xff);
			if(hex.length()==1){
				sig.append("0"+hex);
			}else{
				sig.append(hex);
			}
		}
		return sig.toString();
	}
}

