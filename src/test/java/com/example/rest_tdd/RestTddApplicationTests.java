package com.example.rest_tdd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
//서버를 띄우지 않아도 스프링이 내부적으로 네트워크에서 실행한 것처럼 작동
@AutoConfigureMockMvc
//DB 작업을 해도 테스트 환경에서는 원래대로 돌아가게 함
@Transactional
class AuthApplicationTests {

	@Autowired
	private MockMvc mvc;


	@Test
	@DisplayName("회원가입")
	void join() throws Exception {
		ResultActions resultActions = mvc
				.perform(
						post("/api/v1/member/join")
				)
				.andDo(print());
		resultActions
				.andExpect(status().isCreated());
	}

}
