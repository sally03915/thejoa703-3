package com.thejoa703.external;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@Service
public class NaverBookXmlService {
	
	@JsonIgnoreProperties(ignoreUnknown = true)  //정의 되지 않은태그들은 무시   ##2
	static class Channel{

		@JacksonXmlProperty(localName="item")  // 태그
		@JacksonXmlElementWrapper(useWrapping = false)  // items들을 묶어서 wrapper태그없이 <item></item>  <item></item>
		private List<BookDto> items;

		public List<BookDto> getItems() { return items; }
		public void setItems(List<BookDto> items) { this.items = items; }
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)  //##1
	static class Rss{
		@JacksonXmlProperty(localName="channel")
		private Channel channel;
		public Channel getChannel() { return channel; }
		public void setChannel(Channel channel) { this.channel = channel; }
	}
	
	///////////////////////////////////////////////////
	// Dao 가져오기##
	public List<BookDto>  getBooks(String query) throws UnsupportedEncodingException{
		RestTemplate restTemplate = new RestTemplate();
		XmlMapper xmlMapper = new XmlMapper(); //### 
		String clientId="AqGs1swmWieSswFk8doU";  // clientId
		String clientSecret="4B8igOnoSi";        // clientSecret
		String param = "?query=" + URLEncoder.encode(query, "UTF-8");
		String url   ="https://openapi.naver.com/v1/search/book.xml" + param;   //####
		
		HttpHeaders headers = new HttpHeaders(); 
		headers.set("X-Naver-Client-Id", clientId);
		headers.set("X-Naver-Client-Secret", clientSecret);
		headers.set("Accept", "application/xml");  //####
		
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		///////////////////////////////////////////////////////////////
		try {
			//xml응답을 Rss → Channel  → item 구조로 파싱 
			Rss rss = xmlMapper.readValue( response.getBody()  , Rss.class  );
			return rss.getChannel().getItems();   //List<BookDto>  컬렉션프레임워크  list형식으로 받아서 → db 처리
		} catch (Exception e) { 
			throw new  RuntimeException("xml 파싱오류" , e);
		}
	} 
} 

//curl "https://openapi.naver.com/v1/search/book.xml?query=%EC%A3%BC%EC%8B%9D&display=10&start=1" \
//-H "X-Naver-Client-Id: {애플리케이션 등록 시 발급받은 클라이언트 아이디 값}" \
//-H "X-Naver-Client-Secret: {애플리케이션 등록 시 발급받은 클라이언트 시크릿 값}" -v