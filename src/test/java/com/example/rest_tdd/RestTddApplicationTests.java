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
									"nickname": "무명"
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
			.andExpect(jsonPath("$.msg").value("회원 가입이 완료되었습니다."))
			.andExpect(jsonPath("$.data").exists())
			.andExpect(jsonPath("$.data.id").isNumber())
			.andExpect(jsonPath("$.data.nickname").value("무명"))
			.andExpect(jsonPath("$.data.createdDate").exists())
			.andExpect(jsonPath("$.data.modifiedDate").exists());

		;
	}

	@Test
	@DisplayName("회원 가입2 - username이 이미 존재하는 케이스")
	void join2() throws Exception {
		ResultActions resultActions = mvc
				.perform(
						post("/api/v1/members/join")
								.content("""
								{
									"username": "user1",
									"password": "1234",
									"nickname": "무명"
								}
								""".stripIndent())
								.contentType(
										new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
								)

				)
				.andDo(print());

		resultActions
				.andExpect(status().isConflict())
				.andExpect(handler().handlerType(ApiV1MemberController.class))
				.andExpect(handler().methodName("join"))
				.andExpect(jsonPath("$.code").value("409-1"))
				.andExpect(jsonPath("$.msg").value("이미 사용중인 아이디입니다."));
	}

}