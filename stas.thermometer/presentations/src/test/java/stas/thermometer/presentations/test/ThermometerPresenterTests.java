package stas.thermometer.presentations.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;
import stas.thermometer.domains.*;
import stas.thermometer.presentations.ThermometerPresenter;
import stas.thermometer.presentations.ThermometerView;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ThermometerPresenterTests {

    private ThermometerPresenter presenter;
    private ThermometerView mockView;
    private Sonde mockSonde;

    private SondeHumidity mockSondeHumidity;
    private Thermometer thermometer;
    private TemperatureAlert mockAlert;
    private final InputStream originalIn = System.in;
    @BeforeEach
    void setUp() {
        mockView = mock(ThermometerView.class);
        mockSonde = mock(Sonde.class);
        thermometer = new Thermometer("Test Thermometer", "00.00°", "dd/MM/yyyy à HH:mm:ss", List.of(new Jalon(LocalTime.now(), 20.0)));
        mockAlert = mock(TemperatureAlert.class);
        mockSondeHumidity = mock(SondeHumidity.class);

        presenter = new ThermometerPresenter(thermometer, mockView, mockSonde,mockSondeHumidity);
    }

    @Test
    void testPresentName() {
        String expectedName = "Test Thermometer\n";
        assertEquals(expectedName, presenter.presentName());
    }

    @AfterEach
    void restoreSystemIn() {
        System.setIn(originalIn);
    }
    @Test
    void testRunMainLoop_QuitCommand() {
        String inputCommands = "q\n";
        System.setIn(new ByteArrayInputStream(inputCommands.getBytes(StandardCharsets.UTF_8)));

        presenter.runMainLoop();

        verify(mockView, atLeastOnce()).display("> ");
        verifyNoMoreInteractions(mockView);
    }

    @Test
    void testHandleCommand_RaiseTemperature() {
        presenter.handleCommand("r");
        verify(mockSonde).adjustValue(0.5);
    }

    @Test
    void testHandleCommand_MitigateTemperature() {
        presenter.handleCommand("m");
        verify(mockSonde).adjustValue(-0.5);
    }

    @Test
    void testDisplayHelp() {
        presenter.displayHelp();
        verify(mockView).display("l - List the commands");
        verify(mockView).display("m - Mitigate the generated temperature");
        verify(mockView).display("r - Raise the generated temperature");
        verify(mockView).display("q - quit the application");
    }

    @Test
    void testHandleUnknownCommand() {
        assertFalse(presenter.handleCommand("unknown"));
        verifyNoInteractions(mockSonde);
        verifyNoInteractions(mockView);
    }



    @Test
    void testDisplayTemperature() {
        LocalDateTime now = LocalDateTime.now();
        Mesure mesure = new Mesure(25.0, now,null);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy a HH:mm:ss");
        String formattedDate = now.format(formatter);

        presenter.displayTemperature(mesure);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockView).display(captor.capture());

        String result = captor.getValue();
        assertTrue(result.contains(formattedDate));
        assertTrue(result.contains("25.0"));
    }

    @Test
    void testDisplayAlert() {
        String alertType = "Surchauffe";
        presenter.displayAlert(alertType, 20.0, 5.0);
        verify(mockView).displayAlert(alertType, 20.0, 5.0);
    }

    @Test
    void testDisplayAlertReturn() {
        String message = "Condition d'alerte résolue. La température est revenue à la normale.";
        presenter.displayAlertReturn(message);
        verify(mockView).display(message);
    }

    @Test
    void testToggleSondeSelection() {
        presenter.toggleSondeSelection();
        presenter.handleCommand("r");
        verify(mockSondeHumidity).adjustValue(4);

        presenter.toggleSondeSelection();
        presenter.handleCommand("r");
        verify(mockSonde).adjustValue(0.5);
    }

    @Test
    void testRaiseTemperatureHumidity() {
        presenter.toggleSondeSelection();
        presenter.handleCommand("r");
        verify(mockSondeHumidity).adjustValue(4);
    }

    @Test
    void testMitigateTemperatureHumidity() {
        presenter.toggleSondeSelection();
        presenter.handleCommand("m");
        verify(mockSondeHumidity).adjustValue(-4);
    }

    @Test
    void testDisplayHumidity() {
        LocalDateTime now = LocalDateTime.now();
        Mesure mesure = new Mesure(60.0, now, null);
        presenter.displayHumidity(mesure);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockView).display(captor.capture());

        String result = captor.getValue();
        assertTrue(result.contains("60.0"));
    }

    @Test
    void testDisplayAlertHumidity() {
        presenter.toggleSondeSelection();

        String alertType = "Trop Humide";
        presenter.displayAlertHumidity(alertType, 40.0, 20.0);

        verify(mockView).displayAlertHumidity(alertType, 40.0, 20.0);
    }


    @Test
    void testDisplayAlertReturnHumidity() {
        presenter.toggleSondeSelection();
        presenter.displayAlertReturnHumidity();
        verify(mockView).display("Condition d'alerte d'humidité résolue pour l'humidité");
    }

}
