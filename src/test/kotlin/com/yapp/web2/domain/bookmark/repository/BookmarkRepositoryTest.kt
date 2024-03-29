package com.yapp.web2.domain.bookmark.repository

import com.yapp.web2.domain.bookmark.entity.Bookmark
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

@DataMongoTest
internal open class BookmarkRepositoryTest {
    @Autowired
    lateinit var bookmarkRepository: BookmarkRepository

    @Test
    open fun `bookmark가 mongoDB에 저장된다`() {
        // given
        val bookmark = Bookmark(1, 1, "www.naver.com")

        //when, then
        Assertions.assertThat(bookmarkRepository.save(bookmark)).isEqualTo(bookmark)
    }

    @Nested
    inner class PageNation {
        @Test
        fun `폴더 아이디에 해당하는 북마크들을 최신순으로 가져온다`() {
            // when
            val bookmarkPages = bookmarkRepository.findAllByFolderIdAndDeletedIsFalse(1, PageRequest.of(1, 5, Sort.by("saveTime").descending()))
            // then
            for (page in bookmarkPages)
                assertEquals(1, page.folderId)
        }
    }

    @Nested
    inner class Search {

        @Test
        fun `사용자 아이디에 해당하는 북마크 중 검색어가 들어간 것을 제목과 url에서 검색한다`() {
            //given
            val testUserId: Long = 1
            val testKeyword = "nav"
            val pageAble = PageRequest.of(2, 4, Sort.by("saveTime").descending())

            //when
            val actualPages = bookmarkRepository.findByTitleContainingIgnoreCaseOrLinkContainingIgnoreCaseAndUserId(
                testKeyword,
                testKeyword,
                testUserId,
                pageAble
            )

            //then
            for (page in actualPages) {
                when (page.title.isNullOrEmpty()) {
                    false -> assertTrue(page.title!!.contains(testKeyword) || page.link.contains(testKeyword))
                }
            }
        }
    }
}