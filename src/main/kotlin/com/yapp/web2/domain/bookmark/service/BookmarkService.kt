package com.yapp.web2.domain.bookmark.service

import com.yapp.web2.domain.bookmark.entity.Bookmark
import com.yapp.web2.domain.bookmark.entity.Information
import com.yapp.web2.domain.bookmark.entity.InformationDto
import com.yapp.web2.domain.bookmark.repository.BookmarkRepository
import com.yapp.web2.domain.folder.repository.FolderRepository
import com.yapp.web2.exception.BusinessException
import com.yapp.web2.exception.ObjectNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookmarkService(
    private val bookmarkRepository: BookmarkRepository,
    private val folderRepository: FolderRepository
) {
    @Transactional
    fun addBookmark(folderId: Long, informationDto: InformationDto): Bookmark {
        // TODO: Order 어떻게 해줄건지? --> 폴더에 가지고 있는 북마크의 수를 저장하고 가져오기로?
        // TODO: 토큰을 통해 userId 가져오기.
        checkFolderAbsence(folderId)
        val toSaveUrl = informationDtoToInformation(informationDto, 0)
        checkSameUrl(toSaveUrl, folderId)

        return bookmarkRepository.save(Bookmark(1, 1, toSaveUrl))
    }

    private fun checkFolderAbsence(folderId: Long) {
        if(folderRepository.findById(folderId).isEmpty) throw ObjectNotFoundException("해당 폴더가 존재하지 않습니다.")
    }

    private fun checkSameUrl(information: Information, folderId: Long) {
        val bookmarkList = bookmarkRepository.findAllByFolderId(folderId).toMutableList()
        for (bookmark in bookmarkList) {
            if (bookmark.information.link == information.link) throw BusinessException("똑같은 게 있어요.")
        }
    }

    private fun informationDtoToInformation(informationDto: InformationDto, order: Int): Information {
        return Information(informationDto.url, informationDto.title, order)
    }

    private fun urlToUrlDto(information: Information): InformationDto {
        return InformationDto(information.link, information.title)
    }

    fun deleteBookmark(bookmarkId: Long) {
        val bookmark = getBookmarkIfPresent(bookmarkId)
        bookmarkRepository.delete(bookmark)
    }

    private fun getBookmarkIfPresent(bookmarkId: Long): Bookmark {
        val bookmark = bookmarkRepository.findById(bookmarkId)
        if(bookmark.isEmpty) throw BusinessException("없어요")
        return bookmark.get()
    }
}