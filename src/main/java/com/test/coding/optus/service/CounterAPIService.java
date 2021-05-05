package com.test.coding.optus.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.test.coding.optus.dto.WordCount;
import com.test.coding.optus.service.exceptions.CounterAPIException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CounterAPIService {
	
	private Map<String, Long> initializeWordCountCache(){
		File file = null;
		Map<String, Long> wordCountMap = new HashMap<String, Long>();
		try {
			file = ResourceUtils.getFile("classpath:sample.txt");
			log.info("file path" + file.toPath().toString());
			wordCountMap = Files.lines(file.toPath())
													.parallel()
													.flatMap(line -> Arrays.stream(line.trim().split(" ")))
													.map(word -> word.replaceAll("[^a-zA-Z]", "").toLowerCase().trim())
													.filter(word -> !word.isEmpty())
													.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		} catch (IOException e) {
			throw new CounterAPIException("File sample.txt is not present under the resource folder.");
		}
		
		return wordCountMap;
		
	}
	
	
	public Map<String, Long> search(Map<String, Object> searchRequest) {
		
		Map<String, Long> wordCountMap = initializeWordCountCache();
		List<String> searchList = (List<String>) searchRequest.get("searchText");
		if(!searchList.isEmpty() && !wordCountMap.isEmpty()) {
		List<String> searchStrings = searchList.stream()
											.map(str -> str.toLowerCase())
											.collect(Collectors.toList());

			Map<String, Long> result = wordCountMap.entrySet().stream()
													.filter(entry -> searchStrings.contains(entry.getKey()))
													// .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
													.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
													        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
			return result;
		}
		return null;
		
	}
	
	public List<WordCount> topSearchCount(int topNumbers) {
		Map<String, Long> wordCountMap = initializeWordCountCache();
		Map<String, Long> result = new HashMap<>();
		List<WordCount> wordCount = new ArrayList<WordCount>();
		if(!wordCountMap.isEmpty()) {
			result = wordCountMap.entrySet().stream()
	                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
	                .limit(topNumbers)
	                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
	                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
			
		}
		if(!result.isEmpty()) {
			for(Map.Entry<String, Long> entry :result.entrySet()) {
				WordCount wordCnt = new WordCount();
				wordCnt.setWord(entry.getKey());
				wordCnt.setCount(entry.getValue());
				wordCount.add(wordCnt);
			}
		}
		log.info("*********" + wordCount);
		return wordCount;
	}
}
