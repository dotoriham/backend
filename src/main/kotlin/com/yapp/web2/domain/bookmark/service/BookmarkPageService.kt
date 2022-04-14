package com.yapp.web2.domain.bookmark.service

import com.yapp.web2.domain.bookmark.entity.Bookmark
import com.yapp.web2.domain.bookmark.repository.BookmarkRepository
import com.yapp.web2.domain.folder.entity.Folder
import com.yapp.web2.domain.folder.repository.FolderRepository
import com.yapp.web2.exception.ObjectNotFoundException
import com.yapp.web2.security.jwt.JwtProvider
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class BookmarkPageService(
    private val bookmarkRepository: BookmarkRepository,
    private val folderRepository: FolderRepository,
    private val jwtProvider: JwtProvider
) {
    @Transactional(readOnly = true)
    fun getAllPageByFolderId(folderId: Long, pageable: Pageable, remind: Boolean): Page<Bookmark> {
        return when (remind) {
            true -> bookmarkRepository.findAllByFolderIdAndDeleteTimeIsNullAndRemindTimeIsNotNull(folderId, pageable)
            false -> bookmarkRepository.findAllByFolderIdAndDeleteTimeIsNull(folderId, pageable)
        }
    }

    fun getTrashPageByUserId(token: String, pageable: Pageable, remind: Boolean): Page<Bookmark> {
        val idFromToken = jwtProvider.getIdFromToken(token)
        return when (remind) {
            true -> bookmarkRepository.findAllByUserIdAndDeleteTimeIsNotNullAndRemindTimeIsNotNull(
                idFromToken,
                pageable
            )
            false -> bookmarkRepository.findAllByUserIdAndDeleteTimeIsNotNull(idFromToken, pageable)
        }
    }

    fun getAllPageByUserId(token: String, pageable: Pageable, remind: Boolean): Page<Bookmark> {
        val idFromToken = jwtProvider.getIdFromToken(token)

        return when (remind) {
            true -> bookmarkRepository.findAllByUserIdAndRemindTimeIsNotNullAndDeleteTimeIsNull(idFromToken, pageable)
            false -> bookmarkRepository.findAllByUserIdAndDeleteTimeIsNull(idFromToken, pageable)
        }
    }

    private fun checkFolderAbsence(folderId: Long): Folder {
        return folderRepository.findById(folderId).orElseThrow { ObjectNotFoundException() }
    }

    fun getTodayRemindBookmark(token: String): Bookmark.RemindList {
        val idFromToken = jwtProvider.getIdFromToken(token)
        val yesterDay = LocalDate.now().minusDays(1).toString()

        return Bookmark.RemindList(
            bookmarkRepository.findAllByRemindTimeAfterAndUserIdAndDeleteTimeIsNull(
                yesterDay,
                idFromToken
            )
        )
    }
}