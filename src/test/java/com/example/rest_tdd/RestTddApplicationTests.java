package com.example.rest_tdd;

import com.example.rest_tdd.domain.member.member.controller.ApiV1MemberController;
import com.example.rest_tdd.domain.member.member.entity.Member;
import com.example.rest_tdd.domain.member.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class RestTddApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private MemberService memberService;

	@Test
	@DisplayName("회원 가입")
	void join() throws Exception {
		ResultActions resultActions = mvc
			.perform(
				post("/api/v1/members/join")
						.content("""
								{
									"username": "usernew",
									"password": "1234",
									"nickname": "user4"
								}
								""".stripIndent())
						.contentType(
								new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
						)

			)
			.andDo(print());

		Member member = memberService.findByUsername("usernew").get();

		assertThat(member.getNickname()).isEqualTo("무명");

		resultActions
			.andExpect(status().isCreated())
			.andExpect(handler().handlerType(ApiV1MemberController.class))
			.andExpect(handler().methodName("join"))
			//jsonPath = 결과 body에 json 데이터가 있으면 값은~
			.andExpect(jsonPath("$.code").value("201-1"))
			.andExpect(jsonPath("$.msg").value("회원 가입이 완료되었습니다."));
	}

}