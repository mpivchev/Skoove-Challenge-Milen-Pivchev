
import app.cash.turbine.test
import com.skoove.challenge.data.repository.AudioEntryRepository
import com.skoove.challenge.data.response.ApiResponse
import com.skoove.challenge.data.response.AudioEntry
import com.skoove.challenge.screens.alertlist.AlertListUIState
import com.skoove.challenge.screens.alertlist.AlertListViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AlertListViewModelUnitTest {
    private val mockAudioEntryRepository: AudioEntryRepository = mockk()
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAudioEntries should return proper entry list`() = runTest {
        val audioEntryList = listOf(AudioEntry("Title", "someUrl.com", "Cover", 23232323))

        coEvery { mockAudioEntryRepository.getAudioEntries() } returns ApiResponse(data = audioEntryList)

        val viewModel = AlertListViewModel(mockAudioEntryRepository)

        // getAudioEntries() is called in ViewModel init

        viewModel.uiState.test {
            assertEquals(AlertListUIState.Success(audioEntryList), awaitItem())
        }
    }
}