using Avalonia.Media;
using System;
using System.Globalization;
using Avalonia.Data.Converters;

namespace Stas.Monitor.Views
{
    public class ValueToColorConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (value is double number)
            {
                return number < 0 ? Brushes.CornflowerBlue : Brushes.IndianRed;
            }

            return Brushes.Black;
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
}
