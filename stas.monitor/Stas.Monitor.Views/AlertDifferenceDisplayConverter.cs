namespace Stas.Monitor.Views;

using System;
using System.Globalization;
using Avalonia.Data.Converters;

public class AlertDifferenceDisplayConverter : IValueConverter
{
    public object Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
    {
        if (value is double AlertDifference)
        {
            return $"Erreur: {AlertDifference:F2}%";
        }

        return string.Empty;
    }

    public object ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture)
    {
        throw new NotImplementedException();
    }
}
