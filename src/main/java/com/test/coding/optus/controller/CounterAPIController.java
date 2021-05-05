package com.test.coding.optus.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.coding.optus.dto.WordCount;
import com.test.coding.optus.service.CounterAPIService;
import com.test.coding.optus.service.exceptions.CounterAPIException;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/counter-api")
@AllArgsConstructor
public class CounterAPIController {
	@Autowired
	CounterAPIService counterAPIService;

	@PostMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity search(@RequestBody Map<String, Object> searchRequest) {
		Map<String, Long> resultWordCount = counterAPIService.search(searchRequest);
		Map<String, Map<String, Long>> searchResult = new HashMap<>();
		searchResult.put("counts", resultWordCount);
		return new ResponseEntity(searchResult, HttpStatus.OK);
	}

	@GetMapping(path = "/top/{id}", produces = "text/csv")
	public void getTopCount(@PathVariable int id, HttpServletResponse response) {
		try {
			writeWordCountCSV(response, counterAPIService.topSearchCount(id));
		} catch (Exception e) {
			throw new CounterAPIException("Error in writing result to file.");
		}
	}

	private void writeWordCountCSV(HttpServletResponse response, List<WordCount> wordCounts) throws Exception {
		response.setContentType("text/csv");
		CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT.withDelimiter('|'));
		for (WordCount wordCount : wordCounts) {
			csvPrinter.printRecord(wordCount.getWord(), wordCount.getCount());
		}
		if (csvPrinter != null)
			csvPrinter.close();
	}

}
