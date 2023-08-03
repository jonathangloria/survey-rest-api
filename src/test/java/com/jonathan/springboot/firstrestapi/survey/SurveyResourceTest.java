/**
 * 
 */
package com.jonathan.springboot.firstrestapi.survey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * @author Jonathan Gloriawan
 *
 */
@WebMvcTest(controllers = SurveyResource.class)
@AutoConfigureMockMvc(addFilters = false)
class SurveyResourceTest {
	
	@MockBean
	private SurveyService surveyService;
	
	@Autowired
	private MockMvc mockMvc;

	//mock -> surveyService.retreiveSpecificSurveyQuestion(surveyId, questionId);
	//fire a request
	// /surveys/{surveyId}/questions/Question1
	
	private static String SPECIFIC_SURVEY_URL = "http://localhost:8080/surveys/Survey1";
	private static String GENERIC_QUESTION_URL = "http://localhost:8080/surveys/Survey1/questions";
	private static String SPECIFIC_QUESTION_URL = "http://localhost:8080/surveys/Survey1/questions/Question1";
	
	@Test
	void retreiveSpecificSurveyQuestion_404Scenario() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(SPECIFIC_QUESTION_URL).accept(MediaType.APPLICATION_JSON);
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		assertEquals(404, mvcResult.getResponse().getStatus());
		
		System.out.println(mvcResult.getResponse().getContentAsString());
		System.out.println(mvcResult.getResponse().getStatus());
	}
	
	@Test
	void retreiveSpecificSurveyQuestion_basicScenario() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(SPECIFIC_QUESTION_URL).accept(MediaType.APPLICATION_JSON);
		
		Question question = new Question("Question1", "Most Popular Cloud Platform Today", 
				Arrays.asList("AWS", "Azure", "Google Cloud", "Oracle Cloud"), "AWS");
		
		when(surveyService.retreiveSpecificSurveyQuestion("Survey1", "Question1")).thenReturn(question);
		
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		String expectedResponse = """
				{
				  "id": "Question1",
				  "description": "Most Popular Cloud Platform Today",
				  "correctAnswer": "AWS"
				}
				""";
		
		assertEquals(200, mvcResult.getResponse().getStatus());
		JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(), false);
	}
	
	@Test
	void retreiveSurveyById_basicScenario() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(SPECIFIC_SURVEY_URL).accept(MediaType.APPLICATION_JSON);
		
		Question question1 = new Question("Question1", "Most Popular Cloud Platform Today", 
				Arrays.asList("AWS", "Azure", "Google Cloud", "Oracle Cloud"), "AWS");
        Question question2 = new Question("Question2", "Fastest Growing Cloud Platform", 
        		Arrays.asList("AWS", "Azure", "Google Cloud", "Oracle Cloud"), "Google Cloud");
        Question question3 = new Question("Question3", "Most Popular DevOps Tool", 
        		Arrays.asList("Kubernetes", "Docker", "Terraform", "Azure DevOps"), "Kubernetes");
 
        List<Question> questions = new ArrayList<>(Arrays.asList(question1, question2, question3));
		Survey survey = new Survey("Survey1", "My Favorite Survey", "Description of the Survey", questions);
		
		when(surveyService.retreiveSurveyById("Survey1")).thenReturn(survey);
		
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		String expectedResponse = """
					{
						"id": "Survey1",
						"title": "My Favorite Survey",
						"description": "Description of the Survey",
						"questions": [
							{
								"id": "Question1",
								"description": "Most Popular Cloud Platform Today",
								"correctAnswer": "AWS"
							},
							{
								"id": "Question2",
								"description": "Fastest Growing Cloud Platform",
								"correctAnswer": "Google Cloud"
							},
							{
								"id": "Question3",
								"description": "Most Popular DevOps Tool",
								"correctAnswer": "Kubernetes"
							}
						]
					}
				""";
		
		assertEquals(200, mvcResult.getResponse().getStatus());
		JSONAssert.assertEquals(expectedResponse, mvcResult.getResponse().getContentAsString(), false);
	}
	
	@Test
	void addNewSurveyQuestion_basicScenario() throws Exception {

		String requestBody = """
				{
				  "description": "Your Favorite Language",
				  "options": [
				    "Java",
				    "Python",
				    "JavaScript",
				    "Haskell"
				  ],
				  "correctAnswer": "Java"
				}
			""";
		
		when(surveyService.addNewSurveyQuestion(anyString(),any())).thenReturn("SOME_ID");

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(GENERIC_QUESTION_URL).accept(MediaType.APPLICATION_JSON)
				.content(requestBody).contentType(MediaType.APPLICATION_JSON);

		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();		
		
		MockHttpServletResponse response = mvcResult.getResponse();
		String locationHeader = response.getHeader("Location");
		
		assertEquals(201, response.getStatus());
		assertTrue(locationHeader.contains("/surveys/Survey1/questions/SOME_ID"));
		
	}

}
