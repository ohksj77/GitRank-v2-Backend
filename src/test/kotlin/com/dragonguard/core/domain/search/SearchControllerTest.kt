package com.dragonguard.core.domain.search

import com.dragonguard.core.domain.member.Member
import com.dragonguard.core.domain.search.dto.SearchRequest
import com.dragonguard.core.support.docs.RestDocsTest
import com.dragonguard.core.support.docs.RestDocsUtils
import com.dragonguard.core.support.fixture.SearchFactory
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(SearchController::class)
class SearchControllerTest : RestDocsTest() {
    @MockBean
    private lateinit var searchService: SearchService

    @Test
    fun `Github 회원 검색`() {
        // given
        val expected = SearchFactory.createSearchMemberResponse()

        BDDMockito
            .given(
                searchService
                    .searchMembers(any(SearchRequest::class.java), any(Member::class.java)),
            ).willReturn(expected)

        // when
        val perform: ResultActions =
            mockMvc.perform(
                RestDocumentationRequestBuilders
                    .get("/search?type=MEMBER&q=dragon&page=1")
                    .queryParam("type", "MEMBER")
                    .queryParam("q", "dragon")
                    .queryParam("page", "1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer apfawfawfa.awfsfawef2.r4svfv32"),
            )

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isOk())

        // docs
        perform
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "search member",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    queryParameters(
                        parameterWithName("type").description("검색 타입 (MEMBER, GIT_REPO)"),
                        parameterWithName("q").description("검색어"),
                        parameterWithName("page").description("페이징에 사용될 페이지 (최소 1)"),
                    ),
                ),
            )
    }

    @Test
    fun `Github Git Repo 검색`() {
        // given
        val expected = SearchFactory.createSearchGitRepoResponse()

        BDDMockito
            .given(
                searchService
                    .searchGitRepos(
                        any(SearchRequest::class.java),
                        any(List::class.java) as List<String>?,
                        any(Member::class.java),
                    ),
            ).willReturn(expected)

        // when
        val perform: ResultActions =
            mockMvc.perform(
                RestDocumentationRequestBuilders
                    .get("/search")
                    .queryParam("type", "GIT_REPO")
                    .queryParam("q", "dragon")
                    .queryParam("page", "1")
                    .queryParam("filters", "language:swift,language:kotlin,language:java")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer apfawfawfa.awfsfawef2.r4svfv32"),
            )

        // then
        perform
            .andExpect(MockMvcResultMatchers.status().isOk())

        // docs
        perform
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "search git repo",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    queryParameters(
                        parameterWithName("type").description("검색 타입 (MEMBER, GIT_REPO)"),
                        parameterWithName("q").description("검색어"),
                        parameterWithName("page").description("페이징에 사용될 페이지 (최소 1)"),
                        parameterWithName("filters").description("검색 필터 (nullable)"),
                    ),
                ),
            )
    }
}
