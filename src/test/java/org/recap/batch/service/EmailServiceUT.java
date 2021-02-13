package org.recap.batch.service;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.EmailPayLoad;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

/**
 * Created by rajeshbabuk on 19/4/17.
 */
public class EmailServiceUT extends BaseTestCaseUT {

    @InjectMocks
    EmailService emailService;

    @Mock
    CommonService commonService;

    @Mock
    RestTemplate restTemplate;

    @Mock
    EmailPayLoad emailPayLoad;

    @Mock
    ResponseEntity<String> responseEntity;

    @Test
    public void testEmailService() {
        Mockito.when(commonService.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(restTemplate.exchange( Matchers.anyString(),
                Matchers.any(HttpMethod.class),
                Matchers.<HttpEntity<?>> any(),
                Matchers.<Class<String>> any())).thenReturn(responseEntity);
        Mockito.when(responseEntity.getBody()).thenReturn(RecapConstants.SUCCESS);
        String status=emailService.sendEmail("solrClientUrl",emailPayLoad);
        assertEquals(RecapConstants.SUCCESS,status);
    }
}
