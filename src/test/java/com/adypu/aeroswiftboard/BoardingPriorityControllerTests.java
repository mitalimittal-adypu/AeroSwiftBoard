package com.adypu.aeroswiftboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.adypu.aeroswiftboard.model.BoardingPrioritySelection;
import com.adypu.aeroswiftboard.service.BoardingPrioritySelectionState;

@SpringBootTest
@AutoConfigureMockMvc
class BoardingPriorityControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BoardingPrioritySelectionState selectionState;

	@Test
	void savesUiSelectionsInSingletonState() throws Exception {
		mockMvc.perform(post("/api/boarding-priority")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"seniorCitizen\":true,\"veteran\":false}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.seniorCitizen").value(true))
				.andExpect(jsonPath("$.veteran").value(false));

		assertEquals(new BoardingPrioritySelection(true, false), selectionState.getCurrentSelection());
	}

	@Test
	void rejectsRequestsMissingASelection() throws Exception {
		mockMvc.perform(post("/api/boarding-priority")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"seniorCitizen\":true}"))
				.andExpect(status().isBadRequest());
	}
}
