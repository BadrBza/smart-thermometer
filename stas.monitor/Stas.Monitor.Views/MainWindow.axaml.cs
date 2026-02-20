using System;
using Avalonia.Controls;
using Stas.Monitor.Presentations;

namespace Stas.Monitor.Views
{
    public partial class MainWindow : Window
    {
        public MainPresenter Presenter => DataContext as MainPresenter;

        public MainWindow()
        {
            InitializeComponent();
        }

        public MainWindow(MainPresenter presenter)
        {
            InitializeComponent();
            DataContext = presenter;
            this.ThermometerSelector.SelectionChanged += OnThermometerSelectionChanged;
            DurationComboBox.DataContext = presenter;
        }

        private void OnThermometerSelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (Presenter != null && ThermometerSelector.SelectedItem is string selectedThermometer)
            {
                Presenter.SelectedThermometerName = selectedThermometer;
            }
        }

        private void DurationComboBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (e.AddedItems[0] is ComboBoxItem selectedItem && TimeSpan.TryParse(selectedItem.Tag.ToString(), out var duration))
            {
                Presenter.SelectedDuration = duration;
                Presenter.UpdateMeasurements();
                Presenter.UpdateMeasurementsHumidity();
            }
        }
    }
}
