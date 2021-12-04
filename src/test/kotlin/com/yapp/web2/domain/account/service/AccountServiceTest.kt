package com.yapp.web2.domain.account.service

import com.yapp.web2.domain.account.entity.Account
import com.yapp.web2.domain.account.repository.AccountRepository
import com.yapp.web2.exception.BusinessException
import com.yapp.web2.security.jwt.JwtProvider
import com.yapp.web2.config.S3Uploader
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.util.*

internal class AccountServiceTest {

    @MockK
    private lateinit var accountRepository: AccountRepository

    @MockK
    private lateinit var jwtProvider: JwtProvider

    @MockK
    private lateinit var s3Uploader: S3Uploader
    private lateinit var accountService: AccountService

    @BeforeEach
    internal fun init() {
        MockKAnnotations.init(this)
        accountService = AccountService(accountRepository, jwtProvider, s3Uploader)
    }


    @Nested
    inner class ChangeNickName {

        private lateinit var testToken: String
        private lateinit var testNickName: Account.nextNickName
        private lateinit var account: Account

        @BeforeEach
        internal fun setUp() {
            testToken = "testToken"
            testNickName = Account.nextNickName("testNickName")
            account = Account("test")
        }

        @Test
        fun `Account가 존재하지 않으면 예외를 던진다`() {
            //given
            every { jwtProvider.getIdFromToken(testToken) } returns 1
            every { accountRepository.findById(any()) } returns Optional.empty()
            val expectedException = BusinessException("계정이 존재하지 않습니다.")

            //when
            val actualException = Assertions.assertThrows(BusinessException::class.java) {
                accountService.changeNickName(testToken, testNickName)
            }
            //then
            assertEquals(expectedException.message, actualException.message)
        }

        //controller 쪽에서 검사하자
//        @Test
//        fun `닉네임의 길이가 넘어가면 예외를 던진다`() {
//
//        }
//
//        @Test
//        fun `변경 이름이 존재하지 않는다면 예외를 던진다`() {
//
//        }

        @Test
        fun `닉네임이 변경된다`() {
            //given
            every { jwtProvider.getIdFromToken(testToken) } returns 1
            every { accountRepository.findById(any()) } returns Optional.of(account)

            //when
            accountService.changeNickName(testToken, testNickName)

            //then
            assertEquals(testNickName.nickName, account.nickname)
        }
    }

    @Nested
    inner class ProfileImageChange {
        private lateinit var testToken: String
        private lateinit var account: Account

        @MockK
        private lateinit var testFile: MultipartFile

        @BeforeEach
        internal fun setUp() {
            testToken = "testToken"
            account = Account("test")
            testFile = MockMultipartFile("file", "imagefile.jpeg", "image/jpeg", "<<jpeg data>>".encodeToByteArray())
        }

        @Test
        fun `Account가 존재하지 않으면 예외를 던진다`() {
            //given
            every { jwtProvider.getIdFromToken(testToken) } returns 1
            every { accountRepository.findById(any()) } returns Optional.empty()
            every { s3Uploader.upload(any(), any()) } returns "good/test"
            val expectedException = BusinessException("계정이 존재하지 않습니다.")

            //when
            val actualException = assertThrows(BusinessException::class.java) {
                accountService.changeProfileImage(testToken, testFile)
            }
            //then
            assertEquals(expectedException.message, actualException.message)
        }

        // S3 test로 진행
//        @Test
//        fun `이미지가 아니면 예외를 던진다`() {
//            //given
//            every { jwtProvider.getIdFromToken(testToken) } returns 1
//            every { accountRepository.findById(any()) } returns Optional.of(account)
//            every { s3Uploader.upload(any(), any()) } returns "good/test"
//            testFile = MockMultipartFile("file", "asdf.pdf", "application/pdf", "<<jpeg data>>".encodeToByteArray())
//            val expectedException = BusinessException("이미지가 아닙니다")
//
//            //when
//            val actualException = assertThrows(BusinessException::class.java) {
//                accountService.changeProfileImage(testToken, testFile)
//            }
//
//            //then
//            assertEquals(expectedException.message, actualException.message)
//        }
    }

    @Nested
    inner class BackgroundColorSetting {

    }
}