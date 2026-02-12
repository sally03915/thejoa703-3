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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service public class NaverBookJsonService {
	// Dao 가져오기##
	public List<BookDto>  getBooks(String query) throws UnsupportedEncodingException{
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper objectMapper = new ObjectMapper(); 
		String clientId="AqGs1swmWieSswFk8doU";  // clientId
		String clientSecret="4B8igOnoSi";        // clientSecret
		String param = "?query=" + URLEncoder.encode(query, "UTF-8");
		String url   ="https://openapi.naver.com/v1/search/book.json" + param;
		
		HttpHeaders headers = new HttpHeaders(); 
		headers.set("X-Naver-Client-Id", clientId);
		headers.set("X-Naver-Client-Secret", clientSecret);
		
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		///////////////////////////////////////////////////////////////
		List<BookDto> result = new ArrayList<>(); 
		try {
			JsonNode  root = objectMapper.readTree(response.getBody());
			System.out.println("......" + root); 
			for(JsonNode item  : root.path("items")) {
				BookDto book = new BookDto();
				book.setTitle(  item.path("title").asText()  );
				book.setImage(  item.path("image").asText()  );
				book.setAuthor( item.path("author").asText()  );
				result.add(book);  
				/// db- insert ##
			} 
		} catch (Exception e) { e.printStackTrace(); }
		return result;
	} 
} 

//curl "https://openapi.naver.com/v1/search/book.xml?query=%EC%A3%BC%EC%8B%9D&display=10&start=1" \
//-H "X-Naver-Client-Id: {애플리케이션 등록 시 발급받은 클라이언트 아이디 값}" \
//-H "X-Naver-Client-Secret: {애플리케이션 등록 시 발급받은 클라이언트 시크릿 값}" -v