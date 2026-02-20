namespace Stas.Monitor.Views;

using Avalonia.Data.Converters;
using System;
using System.Globalization;

public class DifferenceDisplayConverter : IValueConverter
{
    public object Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
    {
        if (value is double difference)
        {
            return $"Erreur: {difference:F2}°";
        }

        return string.Empty;
    }

    public object ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture)
    {
        throw new NotImplementedException();
    }
}
