package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.plcoding.cleanarchitecturenoteapp.feature_note.data.repository.FakeNoteRepository
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.InvalidNoteException
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import junit.framework.TestCase.fail
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test


class AddNoteUseCaseTest {


    private lateinit var addNoteUseCase: AddNoteUseCase
    private lateinit var fakeRepository: FakeNoteRepository

    @Before
    fun setUp() {
        fakeRepository = FakeNoteRepository()
        addNoteUseCase = AddNoteUseCase(fakeRepository)
    }


    @Test
    fun `Assert blank note title throws exception`() {
        val invalidNote = Note(title = "", content = "Content", timestamp = 1L, color = 1, id = 1)
        val exception = assertThrows(InvalidNoteException::class.java) {
            runBlocking {
                addNoteUseCase.invoke(invalidNote)
            }
        }

        assertThat(exception.message).contains("The title of the note can't be empty")
    }

    @Test
    fun `Assert blank note content throws exception`() {
        val invalidNote = Note(title = "test", content = "", timestamp = 1L, color = 1, id = 1)
        val exception = assertThrows(InvalidNoteException::class.java) {
            runBlocking {
                addNoteUseCase.invoke(invalidNote)
            }
        }

        assertThat(exception.message).contains("The content of the note can't be empty")
    }
}